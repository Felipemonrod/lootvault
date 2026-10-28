INSERT INTO item (id, nome, categoria, raridade) VALUES
  (1, 'Espada Longa', 'Arma', 'Comum'),
  (2, 'Escudo de Madeira', 'Armadura', 'Comum'),
  (3, 'Poção de Vida', 'Consumível', 'Comum'),
  (4, 'Arco Élfico', 'Arma', 'Raro'),
  (5, 'Cajado Arcano', 'Arma', 'Épico'),
  (6, 'Elmo do Dragão', 'Armadura', 'Lendário');

-- Inventário inicial dos personagens (personagem_id vem do personagens-service)
INSERT INTO slot_inventario (personagem_id, item_id, posicao, quantidade) VALUES
  (1, 3, 0, 3),
  (1, 2, 1, 1),
  (2, 3, 0, 1),
  (3, 3, 0, 2);
