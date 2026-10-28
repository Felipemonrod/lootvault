package com.lootvault.loja.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lootvault.loja.model.Compra;

public interface CompraRepository extends JpaRepository<Compra, Long> {

	public List<Compra> findByPersonagemId(Long personagemId);
}
