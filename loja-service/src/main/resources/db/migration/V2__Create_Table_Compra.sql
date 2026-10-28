CREATE TABLE IF NOT EXISTS compra (
  id BIGINT NOT NULL AUTO_INCREMENT,
  personagem_id BIGINT NOT NULL,
  oferta_id BIGINT NOT NULL,
  valor INT NOT NULL,
  situacao VARCHAR(20) NOT NULL,
  data_compra DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_compra_oferta FOREIGN KEY (oferta_id) REFERENCES oferta (id)
);
