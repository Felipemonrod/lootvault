package com.lootvault.inventario.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lootvault.inventario.model.SlotInventario;

public interface SlotInventarioRepository extends JpaRepository<SlotInventario, Long> {

	public List<SlotInventario> findByPersonagemIdOrderByPosicao(Long personagemId);

	public SlotInventario findByPersonagemIdAndPosicao(Long personagemId, Integer posicao);

	public SlotInventario findByPersonagemIdAndItemId(Long personagemId, Long itemId);
}
