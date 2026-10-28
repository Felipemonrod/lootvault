package com.lootvault.loja.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.lootvault.loja.exceptions.ConflictException;
import com.lootvault.loja.exceptions.InvalidValueException;
import com.lootvault.loja.exceptions.ResourceNotFoundException;
import com.lootvault.loja.exceptions.ServiceUnavailableException;
import com.lootvault.loja.model.Compra;
import com.lootvault.loja.model.Oferta;
import com.lootvault.loja.repository.CompraRepository;
import com.lootvault.loja.repository.OfertaRepository;
import com.lootvault.loja.response.Item;
import com.lootvault.loja.response.Personagem;
import com.lootvault.loja.response.SlotInventario;

@RestController
@RequestMapping("loja-service")
public class LojaController {

	// Rotas dos outros microsserviços. O endereço (host:porta) vem do application.properties
	// (url.personagens / url.inventario), pois no Docker ele muda de localhost para o nome do container.
	private static final String URL_ITEM = "/inventario-service/item/{itemId}";
	private static final String URL_ADICIONAR_ITEM = "/inventario-service/{personagemId}/adicionar/{itemId}";
	private static final String URL_DEBITAR = "/personagens-service/{personagemId}/debitar/{valor}";
	private static final String URL_CREDITAR = "/personagens-service/{personagemId}/creditar/{valor}";

	@Autowired
	private Environment environment;

	@Autowired
	private OfertaRepository ofertaRepository;

	@Autowired
	private CompraRepository compraRepository;

	// http://localhost:8083/loja-service/ofertas
	@GetMapping(value = "/ofertas")
	public List<Oferta> findOfertas() {
		var ofertas = ofertaRepository.findByAtivo(true);
		for (Oferta oferta : ofertas) {
			preencherDadosDoItem(oferta);
		}
		return ofertas;
	}

	// http://localhost:8083/loja-service/oferta/1
	@GetMapping(value = "/oferta/{id}")
	public Oferta findOferta(@PathVariable("id") Long id) {
		var oferta = ofertaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Oferta não encontrada"));

		preencherDadosDoItem(oferta);

		var port = environment.getProperty("local.server.port");
		oferta.setEnvironment(port);
		return oferta;
	}

	// http://localhost:8083/loja-service/compra/1
	@GetMapping(value = "/compra/{id}")
	public Compra findCompra(@PathVariable("id") Long id) {
		var compra = compraRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Compra não encontrada"));

		var port = environment.getProperty("local.server.port");
		compra.setEnvironment(port);
		return compra;
	}

	// Histórico: http://localhost:8083/loja-service/compras/1
	@GetMapping(value = "/compras/{personagemId}")
	public List<Compra> findComprasDoPersonagem(@PathVariable("personagemId") Long personagemId) {
		return compraRepository.findByPersonagemId(personagemId);
	}

	// Compra de um item: http://localhost:8083/loja-service/comprar/1/1
	// 1) registra a compra como PENDENTE
	// 2) pede ao personagens-service o débito das moedas
	// 3) pede ao inventario-service a inclusão do item
	// 4) se o passo 3 falhar, devolve as moedas (compensação) e marca FALHOU
	@PostMapping(value = "/comprar/{personagemId}/{ofertaId}")
	public Compra comprar(@PathVariable("personagemId") Long personagemId,
			@PathVariable("ofertaId") Long ofertaId) {

		var oferta = ofertaRepository.findById(ofertaId)
				.orElseThrow(() -> new ResourceNotFoundException("Oferta não encontrada"));
		if (!oferta.getAtivo()) {
			throw new InvalidValueException("Esta oferta não está mais disponível");
		}

		var compra = compraRepository.save(
				new Compra(null, personagemId, ofertaId, oferta.getPreco(), "PENDENTE", LocalDateTime.now(), null));

		HashMap<String, String> params = new HashMap<>();
		params.put("personagemId", personagemId.toString());
		params.put("valor", oferta.getPreco().toString());
		params.put("itemId", oferta.getItemId().toString());

		// Passo 2: débito das moedas no personagens-service
		try {
			new RestTemplate().postForEntity(urlPersonagens() + URL_DEBITAR, null, Personagem.class, params);
		} catch (HttpClientErrorException e) {
			// 4xx: personagem não existe ou saldo insuficiente
			marcarSituacao(compra, "FALHOU");
			throw new ConflictException("Compra " + compra.getId() + " recusada pelo personagens-service: "
					+ e.getResponseBodyAsString());
		} catch (Exception e) {
			marcarSituacao(compra, "FALHOU");
			throw new ServiceUnavailableException("Compra " + compra.getId()
					+ " não realizada: personagens-service indisponível");
		}

		// Passo 3: inclusão do item no inventario-service
		try {
			new RestTemplate().postForEntity(urlInventario() + URL_ADICIONAR_ITEM, null, SlotInventario.class, params);
		} catch (HttpClientErrorException e) {
			// 4xx: inventário cheio ou item não existe
			devolverMoedas(compra, params);
			throw new ConflictException("Compra " + compra.getId() + " cancelada e moedas devolvidas: "
					+ e.getResponseBodyAsString());
		} catch (Exception e) {
			devolverMoedas(compra, params);
			throw new ServiceUnavailableException("Compra " + compra.getId()
					+ " cancelada: inventario-service indisponível");
		}

		// Passo 4: tudo certo
		marcarSituacao(compra, "CONCLUIDA");

		var port = environment.getProperty("local.server.port");
		compra.setEnvironment(port);
		return compra;
	}

	// Busca no inventario-service o nome e a raridade do item da oferta
	private void preencherDadosDoItem(Oferta oferta) {
		HashMap<String, String> params = new HashMap<>();
		params.put("itemId", oferta.getItemId().toString());

		try {
			var response = new RestTemplate().getForEntity(urlInventario() + URL_ITEM, Item.class, params);
			var item = response.getBody();
			oferta.setNomeItem(item.getNome());
			oferta.setRaridade(item.getRaridade());
		} catch (Exception e) {
			throw new ServiceUnavailableException("Não foi possível consultar o inventario-service");
		}
	}

	// Compensação: devolve as moedas que já foram debitadas no personagens-service
	private void devolverMoedas(Compra compra, HashMap<String, String> params) {
		try {
			new RestTemplate().postForEntity(urlPersonagens() + URL_CREDITAR, null, Personagem.class, params);
			marcarSituacao(compra, "FALHOU");
		} catch (Exception e) {
			marcarSituacao(compra, "ERRO_REEMBOLSO");
		}
	}

	private String urlPersonagens() {
		return environment.getProperty("url.personagens");
	}

	private String urlInventario() {
		return environment.getProperty("url.inventario");
	}

	private void marcarSituacao(Compra compra, String situacao) {
		compra.setSituacao(situacao);
		compraRepository.save(compra);
	}
}
