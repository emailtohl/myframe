-- Table: t_goods

-- DROP TABLE t_goods;

CREATE TABLE t_goods
(
  id bigserial NOT NULL,
  goods_name character varying(255),
  price numeric(10,2),
  goods_type character varying(64),
  description character varying(255),
  amount integer,
  supplier_id bigint,
  create_time timestamp without time zone,
  CONSTRAINT t_goods_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE t_goods
  OWNER TO postgres;