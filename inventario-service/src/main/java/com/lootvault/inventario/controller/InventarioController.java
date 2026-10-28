package com.lootvault.inventario.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lootvault.inventario.exceptions.ConflictException;
import com.lootvault.inventario.exceptions.InvalidValueException;
import com.lootvault.inventario.exceptions.ResourceNotFoundException;
import com.lootvault.inventario.model.Item;
import com.lootvault.inventario.model.SlotInventario;
import com.lootvault.inventario.repository.ItemRepository;
import com.lootvault.inventario.repository.SlotInventarioRepository;

@RestController
@RequestMapping("inventario-service")
public class InventarioController {

	// A mochila de cada personagem tem as posições 0 até 9
	private static final int CAPACIDADE = 10;

	@Autowired
	private Environment environment;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private SlotInventarioRepository slotRepository;

	// http://localhost:8082/inventario-service/item/1
	@GetMapping(value = "/item/{id}")
	public Item findItem(@PathVariable("id") Long id) {
		var item = itemRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Item não encontrado"));

		var port = environment.getProperty("local.server.port");
		item.setEnvironment(port);
		return item;
	}

	// http://localhost:8082/inventario-service/itens
	@GetMapping(value = "/itens")
	public List<Item> findAllItens() {
		return itemRepository.findAll();
	}

	// http://localhost:8082/inventario-service/1
	@GetMapping(value = "/{personagemId}")
	public List<SlotInventario> findInventario(@PathVariable("personagemId") Long personagemId) {
		return slotRepository.findByPersonagemIdOrderByPosicao(personagemId);
	}

	// Chamado pela Loja: http://localhost:8082/inventario-service/1/adicionar/2
	@PostMapping(value = "/{personagemId}/adicionar/{itemId}")
	public SlotInventario adicionarItem(@PathVariable("personagemId") Long personagemId,
			@PathVariable("itemId") Long itemId) {

		findItem(itemId);

		// Se o personagem já tem esse item, apenas aumenta a quantidade (empilha)
		var slot = slotRepository.findByPersonagemIdAndItemId(personagemId, itemId);
		if (slot != null) {
			slot.setQuantidade(slot.getQuantidade() + 1);
			return slotRepository.save(slot);
		}

		// Senão, procura a primeira posição livre da mochila
		for (int posicao = 0; posicao < CAPACIDADE; posicao++) {
			if (slotRepository.findByPersonagemIdAndPosicao(personagemId, posicao) == null) {
				return slotRepository.save(new SlotInventario(null, personagemId, itemId, posicao, 1));
			}
		}
		throw new ConflictException("Inventário cheio: não há espaço para novos itens");
	}

	// http://localhost:8082/inventario-service/1/mover/0/5
	@PutMapping(value = "/{personagemId}/mover/{origem}/{destino}")
	public SlotInventario moverItem(@PathVariable("personagemId") Long personagemId,
			@PathVariable("origem") Integer origem, @PathVariable("destino") Integer destino) {

		if (destino < 0 || destino >= CAPACIDADE) {
			throw new InvalidValueException("Posição " + destino + " fora do inventário (0 a " + (CAPACIDADE - 1) + ")");
		}
		var slot = slotRepository.findByPersonagemIdAndPosicao(personagemId, origem);
		if (slot == null) {
			throw new ResourceNotFoundException("Não existe item na posição " + origem);
		}
		if (slotRepository.findByPersonagemIdAndPosicao(personagemId, destino) != null) {
			throw new ConflictException("A posição " + destino + " já está ocupada");
		}
		slot.setPosicao(destino);
		return slotRepository.save(slot);
	}

	// Remove 1 unidade do item na posição: http://localhost:8082/inventario-service/1/remover/0
	@DeleteMapping(value = "/{personagemId}/remover/{posicao}")
	public List<SlotInventario> removerItem(@PathVariable("personagemId") Long personagemId,
			@PathVariable("posicao") Integer posicao) {

		var slot = slotRepository.findByPersonagemIdAndPosicao(personagemId, posicao);
		if (slot == null) {
			throw new ResourceNotFoundException("Não existe item na posição " + posicao);
		}
		if (slot.getQuantidade() > 1) {
			slot.setQuantidade(slot.getQuantidade() - 1);
			slotRepository.save(slot);
		} else {
			slotRepository.delete(slot);
		}
		return findInventario(personagemId);
	}
}
