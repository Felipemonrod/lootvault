package com.lootvault.personagens.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lootvault.personagens.exceptions.ConflictException;
import com.lootvault.personagens.exceptions.InvalidValueException;
import com.lootvault.personagens.exceptions.ResourceNotFoundException;
import com.lootvault.personagens.model.Jogador;
import com.lootvault.personagens.model.Personagem;
import com.lootvault.personagens.repository.JogadorRepository;
import com.lootvault.personagens.repository.PersonagemRepository;

@RestController
@RequestMapping("personagens-service")
public class PersonagemController {

	@Autowired
	private Environment environment;

	@Autowired
	private PersonagemRepository repository;

	@Autowired
	private JogadorRepository jogadorRepository;

	// http://localhost:8081/personagens-service/jogador/1
	@GetMapping(value = "/jogador/{id}")
	public Jogador findJogador(@PathVariable("id") Long id) {
		return jogadorRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Jogador não encontrado"));
	}

	// http://localhost:8081/personagens-service/jogador/1/personagens
	@GetMapping(value = "/jogador/{id}/personagens")
	public List<Personagem> findPersonagensDoJogador(@PathVariable("id") Long id) {
		findJogador(id);
		return repository.findByJogadorId(id);
	}

	// http://localhost:8081/personagens-service/1
	@GetMapping(value = "/{id}")
	public Personagem findPersonagem(@PathVariable("id") Long id) {
		var personagem = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Personagem não encontrado"));

		var port = environment.getProperty("local.server.port");
		personagem.setEnvironment(port);
		return personagem;
	}

	// Chamado pela Loja: http://localhost:8081/personagens-service/1/debitar/50
	@PostMapping(value = "/{id}/debitar/{valor}")
	public Personagem debitar(@PathVariable("id") Long id, @PathVariable("valor") Integer valor) {
		if (valor <= 0) {
			throw new InvalidValueException("O valor do débito deve ser maior que zero");
		}
		var personagem = findPersonagem(id);
		if (personagem.getMoedas() < valor) {
			throw new ConflictException("Saldo insuficiente: o personagem possui " + personagem.getMoedas()
					+ " moedas e a compra custa " + valor);
		}
		personagem.setMoedas(personagem.getMoedas() - valor);
		repository.save(personagem);
		return personagem;
	}

	// Chamado pela Loja para devolver as moedas (compensação) quando a compra falha
	@PostMapping(value = "/{id}/creditar/{valor}")
	public Personagem creditar(@PathVariable("id") Long id, @PathVariable("valor") Integer valor) {
		if (valor <= 0) {
			throw new InvalidValueException("O valor do crédito deve ser maior que zero");
		}
		var personagem = findPersonagem(id);
		personagem.setMoedas(personagem.getMoedas() + valor);
		repository.save(personagem);
		return personagem;
	}
}
