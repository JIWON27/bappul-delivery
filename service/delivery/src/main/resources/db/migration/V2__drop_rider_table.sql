ALTER TABLE delivery
CHANGE COLUMN rider_id rider_user_id BIGINT NULL;

DROP TABLE IF EXISTS rider;
