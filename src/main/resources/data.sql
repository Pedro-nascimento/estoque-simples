-- Dados iniciais para teste

-- Categorias
INSERT INTO categorias (nome, descricao, criado_em, atualizado_em) VALUES
('Eletrônicos', 'Produtos eletrônicos em geral', NOW(), NOW()),
('Informática', 'Computadores e acessórios', NOW(), NOW()),
('Móveis', 'Móveis para escritório e casa', NOW(), NOW()),
('Papelaria', 'Materiais de escritório', NOW(), NOW());

-- Produtos
INSERT INTO produtos (nome, descricao, sku, preco, preco_custo, quantidade_estoque, quantidade_minima, ativo, categoria_id, criado_em, atualizado_em) VALUES
('Notebook Dell Inspiron', 'Notebook Dell Inspiron 15 polegadas', 'NOT-DELL-001', 3500.00, 2800.00, 15, 5, true, 2, NOW(), NOW()),
('Mouse Logitech MX Master', 'Mouse sem fio Logitech', 'MOU-LOG-001', 450.00, 320.00, 30, 10, true, 2, NOW(), NOW()),
('Teclado Mecânico Redragon', 'Teclado mecânico RGB', 'TEC-RED-001', 280.00, 180.00, 25, 8, true, 2, NOW(), NOW()),
('Monitor Samsung 24"', 'Monitor Full HD 24 polegadas', 'MON-SAM-001', 950.00, 750.00, 8, 3, true, 1, NOW(), NOW()),
('Cadeira Gamer', 'Cadeira ergonômica para escritório', 'CAD-GAM-001', 1200.00, 850.00, 5, 2, true, 3, NOW(), NOW()),
('Papel A4 500 folhas', 'Resma de papel A4', 'PAP-A4-001', 25.00, 18.00, 100, 20, true, 4, NOW(), NOW()),
('Caneta Esferográfica (cx 50)', 'Caixa com 50 canetas', 'CAN-ESF-001', 45.00, 30.00, 50, 15, true, 4, NOW(), NOW());
