package com.lootvault.personagens.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class Personagem implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Chave estrangeira para a tabela jogador (definida na migration V2)
	@Column(name = "jogador_id", nullable = false)
	private Long jogadorId;

	@Column(nullable = false, length = 100)
	private String nome;

	@Column(nullable = false)
	private Integer moedas;

	@Transient
	private String environment; // porta da instância que atendeu a requisição

	public Personagem() {
	}

	public Personagem(Long id, Long jogadorId, String nome, Integer moedas, String environment) {
		this.id = id;
		this.jogadorId = jogadorId;
		this.nome = nome;
		this.moedas = moedas;
		this.environment = environment;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getJogadorId() {
		return jogadorId;
	}

	public void setJogadorId(Long jogadorId) {
		this.jogadorId = jogadorId;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getMoedas() {
		return moedas;
	}

	public void setMoedas(Integer moedas) {
		this.moedas = moedas;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, jogadorId, nome, moedas, environment);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Personagem other = (Personagem) obj;
		return Objects.equals(id, other.id) && Objects.equals(jogadorId, other.jogadorId)
				&& Objects.equals(nome, other.nome) && Objects.equals(moedas, other.moedas)
				&& Objects.equals(environment, other.environment);
	}
}
