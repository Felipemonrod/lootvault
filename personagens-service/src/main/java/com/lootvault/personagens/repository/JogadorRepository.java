package com.lootvault.personagens.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lootvault.personagens.model.Jogador;

public interface JogadorRepository extends JpaRepository<Jogador, Long> {

}
