package com.lootvault.personagens.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lootvault.personagens.model.Personagem;

public interface PersonagemRepository extends JpaRepository<Personagem, Long> {

	public List<Personagem> findByJogadorId(Long jogadorId);
}
