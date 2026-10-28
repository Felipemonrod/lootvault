package com.lootvault.inventario.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SlotInventario implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Referência lógica ao personagem do personagens-service (não é FK entre bancos)
	@Column(name = "personagem_id", nullable = false)
	private Long personagemId;

	// Chave estrangeira para a tabela item (definida na migration V2)
	@Column(name = "item_id", nullable = false)
	private Long itemId;

	@Column(nullable = false)
	private Integer posicao;

	@Column(nullable = false)
	private Integer quantidade;

	public SlotInventario() {
	}

	public SlotInventario(Long id, Long personagemId, Long itemId, Integer posicao, Integer quantidade) {
		this.id = id;
		this.personagemId = personagemId;
		this.itemId = itemId;
		this.posicao = posicao;
		this.quantidade = quantidade;
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

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public Integer getPosicao() {
		return posicao;
	}

	public void setPosicao(Integer posicao) {
		this.posicao = posicao;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, personagemId, itemId, posicao, quantidade);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		SlotInventario other = (SlotInventario) obj;
		return Objects.equals(id, other.id) && Objects.equals(personagemId, other.personagemId)
				&& Objects.equals(itemId, other.itemId) && Objects.equals(posicao, other.posicao)
				&& Objects.equals(quantidade, other.quantidade);
	}
}
