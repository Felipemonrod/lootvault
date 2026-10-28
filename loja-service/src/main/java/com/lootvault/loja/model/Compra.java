package com.lootvault.loja.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class Compra implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Referência lógica ao personagem do personagens-service (não é FK entre bancos)
	@Column(name = "personagem_id", nullable = false)
	private Long personagemId;

	// Chave estrangeira para a tabela oferta (definida na migration V2)
	@Column(name = "oferta_id", nullable = false)
	private Long ofertaId;

	@Column(nullable = false)
	private Integer valor; // preço efetivamente cobrado

	@Column(nullable = false, length = 20)
	private String situacao; // PENDENTE, CONCLUIDA ou FALHOU

	@Column(name = "data_compra", nullable = false)
	private LocalDateTime dataCompra;

	@Transient
	private String environment; // porta da instância que atendeu a requisição

	public Compra() {
	}

	public Compra(Long id, Long personagemId, Long ofertaId, Integer valor, String situacao,
			LocalDateTime dataCompra, String environment) {
		this.id = id;
		this.personagemId = personagemId;
		this.ofertaId = ofertaId;
		this.valor = valor;
		this.situacao = situacao;
		this.dataCompra = dataCompra;
		this.environment = environment;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPersonagemId() {
		return personagemId;
	}

	public void setPersonagemId(Long personagemId) {
		this.personagemId = personagemId;
	}

	public Long getOfertaId() {
		return ofertaId;
	}

	public void setOfertaId(Long ofertaId) {
		this.ofertaId = ofertaId;
	}

	public Integer getValor() {
		return valor;
	}

	public void setValor(Integer valor) {
		this.valor = valor;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public LocalDateTime getDataCompra() {
		return dataCompra;
	}

	public void setDataCompra(LocalDateTime dataCompra) {
		this.dataCompra = dataCompra;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, personagemId, ofertaId, valor, situacao, dataCompra, environment);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Compra other = (Compra) obj;
		return Objects.equals(id, other.id) && Objects.equals(personagemId, other.personagemId)
				&& Objects.equals(ofertaId, other.ofertaId) && Objects.equals(valor, other.valor)
				&& Objects.equals(situacao, other.situacao) && Objects.equals(dataCompra, other.dataCompra)
				&& Objects.equals(environment, other.environment);
	}
}
