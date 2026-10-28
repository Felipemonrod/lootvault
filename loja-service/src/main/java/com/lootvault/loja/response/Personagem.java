package com.lootvault.loja.response;

import java.io.Serializable;
import java.util.Objects;

// Cópia da classe Personagem do personagens-service, sem as annotations de persistência.
// Serve apenas para receber (desserializar) o JSON retornado por aquele microsserviço.
public class Personagem implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Long jogadorId;
	private String nome;
	private Integer moedas;
	private String environment;

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
