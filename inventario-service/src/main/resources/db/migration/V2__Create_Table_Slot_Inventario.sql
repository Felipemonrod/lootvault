CREATE TABLE IF NOT EXISTS slot_inventario (
  id BIGINT NOT NULL AUTO_INCREMENT,
  personagem_id BIGINT NOT NULL,
  item_id BIGINT NOT NULL,
  posicao INT NOT NULL,
  quantidade INT NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_slot_item FOREIGN KEY (item_id) REFERENCES item (id),
  CONSTRAINT uk_slot_personagem_posicao UNIQUE (personagem_id, posicao)
);
