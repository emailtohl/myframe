-- Table: t_supplier

-- DROP TABLE t_supplier;

CREATE TABLE t_supplier
(
  id bigserial NOT NULL,
  supplier_name character varying(255),
  address character varying(255),
  tel character varying(255),
  description character varying(255),
  email character varying(255),
  rank integer,
  mark_deleted smallint,
  CONSTRAINT t_supplier_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE t_supplier
  OWNER TO postgres;

  
INSERT INTO t_supplier VALUES ('1', '重庆啤酒厂', '重庆九龙坡 龙泉村1号', '023-68637554', '山城啤酒是重庆啤酒（集团）有限责任公司旗下的品牌。', 'Chongqing@cp.com', '2');
INSERT INTO t_supplier VALUES ('2', '重庆烟草工业有限责任公司', ' 弹子石新街36号附34号', '(023)62521621', '重庆烟草品牌每年为卷烟零售客户提供收入近6亿元，其中宏声、龙凤呈祥两个品牌', 'juanyan@jy.com', '3');
INSERT INTO t_supplier VALUES ('3', '伊利实业集团股份有限公司', '沿河东路南段新宇家苑附近', '(023)41438166', '呼和浩特回民区回民奶食品总厂', null, '1');
INSERT INTO t_supplier VALUES ('4', '重庆金星股份有限公司', '重庆市綦江区古南镇新村路80号', '401420', '老四川牛肉干', null, '2');
INSERT INTO t_supplier VALUES ('5', '福州零客食品有限公司', '福州市台江区学军路1号群升国际A区3号楼17层', '400-6869-693', '皇家伯爵松露巧克力', ' http://www.likeyfood.com', '2');
INSERT INTO t_supplier VALUES ('6', '安徽天下水坊饮品有限责任公司', '合肥双凤荣事达第六工业园', ' 0551-67175061', '竹元堂 竹盐绿茶', 'http://www.hfrsdsp.com', '2');
INSERT INTO t_supplier VALUES ('7', '雷州市优之生态食品有限公司', '雷州大道141号', '0759-8833281', '红烧牛肉面', 'http://youzhifood.spzs.com', '3');
INSERT INTO t_supplier VALUES ('8', '黑龙江省五大连池泉山矿泉水有限责任公司', '五大连池风景名胜区自然保护区', '4008383598/4008789910', '高级矿泉水，出品商：五大连池风景区天瑞冷矿泉水开发有限公司', 'http://www.hshxq.com', '1');
INSERT INTO t_supplier VALUES ('9', '珠海冰威饮品开发有限公司', '珠海市香洲紫荆路兴国街67号', '0756-2132258', '美国冰威王子啤酒', 'http://bingwei.spzs.com', '2');
INSERT INTO t_supplier VALUES ('10', '乐百氏广东食品饮料有限公司', '广州市天河区林和西路1号广州国际贸易中心28-29层', '（020）38783933', '乐百氏（广东)食品饮料有限公司是注册在啤酒、纯净水（饮料）等商品上的“乐百氏”商标以及注册在啤酒、水（饮料）和杏仁糖浆等商品上的“劲能”商标的持有人。乐百氏现有乳酸奶饮料系列、瓶装饮用水系列、功能性饮料等多个系列的优质产品', 'http://www.mizone.cc/', '1');
INSERT INTO t_supplier VALUES ('11', '重庆步正食品有限公司', '重庆市南岸区南坪西路38号嘉德中心2号1009号', '332211', '宜简苏打水', 'http://www.cqbuzheng.com', '3');
