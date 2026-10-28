package com.lootvault.loja.response;

import java.io.Serializable;
import java.util.Objects;

// Cópia da classe Item do inventario-service, sem as annotations de persistência.
// Serve apenas para receber (desserializar) o JSON retornado por aquele microsserviço.
public class Item implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String nome;
	private String categoria;
	private String raridade;
	private String environment;

	public Item() {
	}

	public Item(Long id, String nome, String categoria, String raridade, String environment) {
		this.id = id;
		this.nome = nome;
		this.categoria = categoria;
		this.raridade = raridade;
		this.environment = environment;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
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
		return Objects.hash(id, nome, categoria, raridade, environment);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		return Objects.equals(id, other.id) && Objects.equals(nome, other.nome)
				&& Objects.equals(categoria, other.categoria) && Objects.equals(raridade, other.raridade)
				&& Objects.equals(environment, other.environment);
	}
}
