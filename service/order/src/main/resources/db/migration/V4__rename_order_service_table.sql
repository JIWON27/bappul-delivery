RENAME TABLE order_line TO order_item;
RENAME TABLE order_line_option TO order_item_option;

ALTER TABLE orders
RENAME COLUMN line_total TO order_subtotal_price,
RENAME COLUMN delivery_fee TO delivery_fee_price,
RENAME COLUMN order_discount TO order_discount_price,
RENAME COLUMN payable_total TO payable_total_price;

ALTER TABLE order_item
RENAME COLUMN line_total TO line_total_price,
RENAME COLUMN line_discount TO line_discount_price,
RENAME COLUMN refunded_price TO refund_price,
RENAME COLUMN refunded_quantity TO refund_quantity;



