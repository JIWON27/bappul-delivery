CREATE VIEW store_search_view AS
  SELECT
    s.id              AS store_id,
    s.name            AS store_name,
    s.category_id     AS category_id,
    c.name            AS category_name,
    m.id              AS menu_id,
    m.name            AS menu_name,
    s.min_order_price AS min_order_price,
    s.delivery_fee    AS delivery_fee,
    s.open_status     AS open_status,
    s.created_at      AS store_created_at
  FROM stores s JOIN categories c ON s.category_id = c.id
  JOIN menus m ON m.store_id = s.id AND m.visible  = 1;
