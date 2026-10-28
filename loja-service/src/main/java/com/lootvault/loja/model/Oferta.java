package com.lootvault.loja.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class Oferta implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Referência lógica ao item do inventario-service (não é FK entre bancos)
	@Column(name = "item_id", nullable = false)
	private Long itemId;

	@Column(nullable = false)
	private Integer preco; // preço em moedas

	@Column(nullable = false)
	private Boolean ativo;

	@Transient
	private String nomeItem; // preenchido com o dado vindo do inventario-service

	@Transient
	private String raridade; // preenchido com o dado vindo do inventario-service

	@Transient
	private String environment; // porta da instância que atendeu a requisição

	public Oferta() {
	}

	public Oferta(Long id, Long itemId, Integer preco, Boolean ativo, String nomeItem, String raridade,
			String environment) {
		this.id = id;
		this.itemId = itemId;
		this.preco = preco;
		this.ativo = ativo;
		this.nomeItem = nomeItem;
		this.raridade = raridade;
		this.environment = environment;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public Integer getPreco() {
		return preco;
	}

	public void setPreco(Integer preco) {
		this.preco = preco;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public String getNomeItem() {
		return nomeItem;
	}

	public void setNomeItem(String nomeItem) {
		this.nomeItem = nomeItem;
	}

	public String getRaridade() {
		return raridade;
	}

	public void setRaridade(String raridade) {
		this.raridade = raridade;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, itemId, preco, ativo, nomeItem, raridade, environment);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Oferta other = (Oferta) obj;
		return Objects.equals(id, other.id) && Objects.equals(itemId, other.itemId)
				&& Objects.equals(preco, other.preco) && Objects.equals(ativo, other.ativo)
				&& Objects.equals(nomeItem, other.nomeItem) && Objects.equals(raridade, other.raridade)
				&& Objects.equals(environment, other.environment);
	}
}
