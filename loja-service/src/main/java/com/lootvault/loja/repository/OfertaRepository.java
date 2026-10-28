package com.lootvault.loja.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lootvault.loja.model.Oferta;

public interface OfertaRepository extends JpaRepository<Oferta, Long> {

	public List<Oferta> findByAtivo(Boolean ativo);
}
