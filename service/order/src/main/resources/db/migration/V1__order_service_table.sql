CREATE TABLE orders (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_no BINARY(16) NOT NULL,
  idempotency_key VARCHAR(64) NOT NULL,
  user_id BIGINT NOT NULL,
  address_id BIGINT NOT NULL,
  store_id BIGINT NOT NULL,
  coupon_id BIGINT NULL,
  order_status VARCHAR(30) NOT NULL,
  payment_status VARCHAR(30) NOT NULL,
  delivery_status VARCHAR(30) NOT NULL,
  line_total DECIMAL(10,0) NOT NULL DEFAULT 0,
  delivery_fee DECIMAL(10,0) NOT NULL DEFAULT 0,
  order_discount DECIMAL(10,0) NOT NULL DEFAULT 0,
  payable_total DECIMAL(10,0) NOT NULL DEFAULT 0,
  cancel_reason VARCHAR(200) NULL,
  created_at DATETIME(6)NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6)NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

CREATE TABLE order_line (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT NULL,
  menu_id BIGINT NULL,
  menu_name  VARCHAR(255),
  base_price DECIMAL(10,0) NOT NULL DEFAULT 0,
  unit_price DECIMAL(10,0) NOT NULL DEFAULT 0,
  line_total DECIMAL(10,0) NOT NULL DEFAULT 0,
  line_discount DECIMAL(10,0) NOT NULL DEFAULT 0,
  refunded_price DECIMAL(10,0) NOT NULL DEFAULT 0,
  quantity INT NOT NULL,
  refunded_quantity INT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL
);

CREATE TABLE order_line_option (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_line_id BIGINT,
  option_value_id BIGINT,
  option_name  VARCHAR(255),
  option_price NUMERIC(19,2),
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL
);

CREATE TABLE outbox_events (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  event_id BINARY(16) NOT NULL,
  event_type VARCHAR(128) NOT NULL,
  aggregate_id BIGINT NULL,
  aggregate_type VARCHAR(30) NULL,
  status VARCHAR(20) NOT NULL,
  partition_key VARCHAR(128) NOT NULL,
  payload TEXT NOT NULL,
  occurred_at DATETIME(6) NULL,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  processed_at DATETIME(6) NULL
);

CREATE TABLE inbox_events (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  event_id VARCHAR(36) NOT NULL,
  event_type VARCHAR(128) NOT NULL,
  payload TEXT NOT NULL,
  status VARCHAR(20) NOT NULL,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
 );
