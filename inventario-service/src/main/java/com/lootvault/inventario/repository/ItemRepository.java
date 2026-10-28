package com.lootvault.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lootvault.inventario.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

}
