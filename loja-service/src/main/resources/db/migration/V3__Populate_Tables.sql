-- item_id corresponde aos itens cadastrados no inventario-service
INSERT INTO oferta (id, item_id, preco, ativo) VALUES
  (1, 1, 50, 1),   -- Espada Longa
  (2, 3, 10, 1),   -- Poção de Vida
  (3, 4, 120, 1),  -- Arco Élfico
  (4, 5, 300, 1),  -- Cajado Arcano (caro: bom para testar saldo insuficiente)
  (5, 6, 999, 0);  -- Elmo do Dragão (oferta inativa)

-- Histórico de exemplo
INSERT INTO compra (personagem_id, oferta_id, valor, situacao, data_compra) VALUES
  (2, 2, 10, 'CONCLUIDA', '2026-10-20 14:30:00');
