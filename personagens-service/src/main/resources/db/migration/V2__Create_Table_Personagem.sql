CREATE TABLE IF NOT EXISTS personagem (
  id BIGINT NOT NULL AUTO_INCREMENT,
  jogador_id BIGINT NOT NULL,
  nome VARCHAR(100) NOT NULL,
  moedas INT NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_personagem_jogador FOREIGN KEY (jogador_id) REFERENCES jogador (id)
);
