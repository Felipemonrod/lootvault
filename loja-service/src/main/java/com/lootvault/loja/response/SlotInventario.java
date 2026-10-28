package com.lootvault.loja.response;

import java.io.Serializable;
import java.util.Objects;

// Cópia da classe SlotInventario do inventario-service, sem as annotations de persistência.
// Serve apenas para receber (desserializar) o JSON retornado por aquele microsserviço.
public class SlotInventario implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Long personagemId;
	private Long itemId;
	private Integer posicao;
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
