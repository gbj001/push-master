DROP TABLE "CS_ORDER_EXCHANGE_INFO";
CREATE TABLE "CS_ORDER_EXCHANGE_INFO" (
  "out_order_id" CHAR(20) NOT NULL,
  "order_type" CHAR(1) DEFAULT '' NOT NULL,
  "push_count" NUMBER(6) DEFAULT 0 NOT NULL,
  "push_process_count" NUMBER(6) DEFAULT 0 NOT NULL,
  "push_status" CHAR(1) DEFAULT 0 NOT NULL,
  "push_channel" VARCHAR2(10),
  "created_user" VARCHAR2(20) DEFAULT '',
  "created_time" DATE,
  "updated_user" VARCHAR2(20) DEFAULT '',
  "updated_time" DATE,
  "request_content" VARCHAR2(4000) DEFAULT '',
  "response_time" DATE,
  "response_content" VARCHAR2(4000) DEFAULT ''
)
TABLESPACE "NNC_DATA01"
LOGGING
NOCOMPRESS
PCTFREE 10
INITRANS 1
STORAGE (
  INITIAL 262144
  NEXT 262144
  MINEXTENTS 1
  MAXEXTENTS 2147483645
  BUFFER_POOL DEFAULT
)
PARALLEL 1
NOCACHE
DISABLE ROW MOVEMENT
;


COMMENT ON COLUMN CS_ORDER_EXCHANGE_INFO."out_order_id" is '请求单号';
COMMENT ON COLUMN CS_ORDER_EXCHANGE_INFO."order_type" is '请求类型 1-普通采购；2-普通销售；3-销售试吃；4-退换货；5-发货单';
COMMENT ON COLUMN CS_ORDER_EXCHANGE_INFO."push_count" is '推送次数';
COMMENT ON COLUMN CS_ORDER_EXCHANGE_INFO."push_process_count" is '对方返回失败后再次推送次数';
COMMENT ON COLUMN CS_ORDER_EXCHANGE_INFO."push_status" is '推送状态 0-推送失败，1-推送成功';
COMMENT ON COLUMN CS_ORDER_EXCHANGE_INFO."push_channel" is '推送wms简称';
COMMENT ON COLUMN CS_ORDER_EXCHANGE_INFO."created_user" is '推送人';
COMMENT ON COLUMN CS_ORDER_EXCHANGE_INFO."created_time" is '推送创建时间';
COMMENT ON COLUMN CS_ORDER_EXCHANGE_INFO."updated_user" is '修改人';
COMMENT ON COLUMN CS_ORDER_EXCHANGE_INFO."updated_time" IS '最后修改时间';
COMMENT ON COLUMN CS_ORDER_EXCHANGE_INFO."request_content" IS '请求信息';
COMMENT ON COLUMN CS_ORDER_EXCHANGE_INFO."response_time" IS '响应时间';
COMMENT ON COLUMN CS_ORDER_EXCHANGE_INFO."response_content" IS '响应信息';
COMMIT;


DROP TABLE "CS_WAREHOUSE_CONFIG";
CREATE TABLE "CS_WAREHOUSE_CONFIG" (
  "wms_name" VARCHAR2(50) NOT NULL,
  "wms_short" VARCHAR2(20) NOT NULL,
  "wms_warehouse_code" VARCHAR2(10) NOT NULL,
  "nc_warehouse_code" VARCHAR2(20) DEFAULT 0 NOT NULL,
  "nc_warehouse_name" VARCHAR2(30),
  "nc_org_code" VARCHAR2(30),
  "flag" CHAR (1) DEFAULT '0' NOT NULL
)
TABLESPACE "NNC_DATA01"
LOGGING
NOCOMPRESS
PCTFREE 10
INITRANS 1
STORAGE (
  INITIAL 262144
  NEXT 262144
  MINEXTENTS 1
  MAXEXTENTS 2147483645
  BUFFER_POOL DEFAULT
)
PARALLEL 1
NOCACHE
DISABLE ROW MOVEMENT
;


COMMENT ON COLUMN CS_WAREHOUSE_CONFIG."wms_name" is '外部wms系统名称';
COMMENT ON COLUMN CS_WAREHOUSE_CONFIG."wms_short" is '外部wms系统简称';
COMMENT ON COLUMN CS_WAREHOUSE_CONFIG."wms_warehouse_code" is '外部wms系统仓库编码';
COMMENT ON COLUMN CS_WAREHOUSE_CONFIG."nc_warehouse_code" is 'nc系统仓库编码';
COMMENT ON COLUMN CS_WAREHOUSE_CONFIG."nc_warehouse_name" is 'nc系统仓库名称';
COMMENT ON COLUMN CS_WAREHOUSE_CONFIG."nc_org_code" is 'nc组织单位代码';
COMMENT ON COLUMN CS_WAREHOUSE_CONFIG."flag" is '是否启用';
COMMIT;

ALTER TABLE CS_WAREHOUSE_CONFIG ADD ("start_time" varchar2(20));
ALTER TABLE CS_WAREHOUSE_CONFIG ADD ("end_time" varchar2(20));
COMMENT ON COLUMN CS_WAREHOUSE_CONFIG."start_time" is '推送订单开始时间';
COMMENT ON COLUMN CS_WAREHOUSE_CONFIG."end_time" is '推送订单截止时间';

UPDATE CS_WAREHOUSE_CONFIG set "start_time"='2018-05-20 00:00:00', "end_time"='9999-12-30 00:00:00' where "wms_short"='FL';
INSERT INTO CS_WAREHOUSE_CONFIG("wms_name","wms_short","wms_warehouse_code","nc_warehouse_code","nc_warehouse_name","nc_org_code","flag","start_time","end_time")
VALUES('码上配wms系统','MSP','SPC','W506','上海仓','10001','1','2018-05-24 00:00:00','9999-12-30 00:00:00');
COMMIT;

DELETE FROM CS_WAREHOUSE_CONFIG;

INSERT INTO CS_WAREHOUSE_CONFIG("wms_name","wms_short","wms_warehouse_code","nc_warehouse_code","nc_warehouse_name","nc_org_code","flag","start_time","end_time")
VALUES('唯捷wms系统','WJ','1001','W505','上海仓库','10001','1','9999-12-30 00:00:00','9999-12-30 00:00:00');
INSERT INTO CS_WAREHOUSE_CONFIG("wms_name","wms_short","wms_warehouse_code","nc_warehouse_code","nc_warehouse_name","nc_org_code","flag","start_time","end_time")
VALUES('码上配wms系统','MSP','SPC','W506','上海仓','10001','1','9999-12-30 00:00:00','9999-12-30 00:00:00');
INSERT INTO CS_WAREHOUSE_CONFIG("wms_name","wms_short","wms_warehouse_code","nc_warehouse_code","nc_warehouse_name","nc_org_code","flag","start_time","end_time")
VALUES('马上配wms系统','MSP','JDC','WH004','嘉定仓','10001','1','9999-12-30 00:00:00','9999-12-30 00:00:00');
INSERT INTO CS_WAREHOUSE_CONFIG("wms_name","wms_short","wms_warehouse_code","nc_warehouse_code","nc_warehouse_name","nc_org_code","flag","start_time","end_time")
VALUES('新联道wms系统','FL','WH00500','WH00500','北京仓','10001','1','9999-12-30 00:00:00','9999-12-30 00:00:00');
COMMIT;


DROP TABLE "CS_ALIPAY_BILL_EXTEND";
CREATE TABLE "CS_ALIPAY_BILL_EXTEND"(
  "bill_no" VARCHAR2(20) NOT NULL,
  "business_no" VARCHAR2(35) NOT NULL,
  "trade_time" DATE NOT NULL,
  "source_name" VARCHAR2(50) NOT NULL,
  "source_account" VARCHAR2(30) NOT NULL,
  "income_amount" VARCHAR2(20) NOT NULL,
  "business_type" VARCHAR2(10),
  "import_type" CHAR(1) DEFAULT 'S',
  "remark" VARCHAR2(100) DEFAULT '',
  "match_status" CHAR(1) DEFAULT '2',
  "match_content" VARCHAR2(1000) DEFAULT '',
  "match_customer_code" VARCHAR2(30) DEFAULT '',
  "match_customer_name" VARCHAR2(100) DEFAULT '',
  "request_status" CHAR(1) DEFAULT '2',
  "request_time" DATE,
  "request_text" VARCHAR2(1000),
  "response_status" CHAR(1) DEFAULT 'X',
  "response_time" DATE,
  "response_text" VARCHAR2(1000) DEFAULT '',
  "verify_status" CHAR(1) DEFAULT '2',
  "verify_time" DATE,
  "verify_text" VARCHAR2(1000) DEFAULT '',
  "created_user" VARCHAR2(20) DEFAULT '',
  "created_time" DATE,
  "updated_user" VARCHAR2(20) DEFAULT '',
  "updated_time" DATE,
  "def1" VARCHAR2(100) DEFAULT '',
  "def2" VARCHAR2(100) DEFAULT '',
  "def3" VARCHAR2(100) DEFAULT '',
  "def4" VARCHAR2(100) DEFAULT '',
  "def5" VARCHAR2(100) DEFAULT '',
  "def6" VARCHAR2(100) DEFAULT '',
  "def7" VARCHAR2(100) DEFAULT '',
  "def8" VARCHAR2(100) DEFAULT '',
  "def9" VARCHAR2(100) DEFAULT '',
  "def10" VARCHAR2(100)  DEFAULT ''
)
TABLESPACE "NNC_DATA01"
LOGGING
NOCOMPRESS
PCTFREE 10
INITRANS 1
STORAGE (
  INITIAL 262144
  NEXT 262144
  MINEXTENTS 1
  MAXEXTENTS 2147483645
  BUFFER_POOL DEFAULT
)
PARALLEL 1
NOCACHE
DISABLE ROW MOVEMENT
;




DROP INDEX CABE_IDX_BUSINESS_NO;
CREATE INDEX CABE_IDX_BUSINESS_NO ON CS_ALIPAY_BILL_EXTEND("business_no");


COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."bill_no" is '账务流水号';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."business_no" is '业务流水号';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."trade_time" is '交易时间';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."source_name" is '支付宝账号名称';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."source_account" is '支付宝账号';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."income_amount" is '收入金额';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."business_type" is '业务类型';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."remark" is '备注';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."import_type" is '导入类型 S-系统导入 M-手工录入';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."match_status" is '匹配状态 0-匹配失败 1-匹配成功 2-没有匹配';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."match_content" is '是否匹配的原因';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."match_customer_code" is '匹配的客户编码';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."match_customer_name" is '匹配的客户名称';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."request_status" is '发送收款单状态 0-失败 1-成功 2-未发送';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."request_time" is '发送收款单时间';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."request_text" is '发送收款单内容';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."response_text" is '响应内容';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."response_time" is '响应时间';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."response_status" is '响应状态 Y-成功 N-失败 X-未响应';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."verify_status" is '校验状态 0-待校验 1-校验成功 2-校验失败';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."verify_time" is '校验时间';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."verify_text" is '校验信息';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."created_user" is '创建人';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."created_time" is '创建时间';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."updated_user" is '修改人';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."updated_time" is '修改时间';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."def1" is '自定义字段1';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."def2" is '自定义字段2';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."def3" is '自定义字段3';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."def4" is '自定义字段4';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."def5" is '自定义字段5';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."def6" is '自定义字段6';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."def7" is '自定义字段7';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."def8" is '自定义字段8';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."def9" is '自定义字段9';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."def10" is '自定义字段10';



ALTER TABLE CS_ALIPAY_BILL_EXTEND MODIFY("remark" VARCHAR2(300));
ALTER TABLE CS_ALIPAY_BILL_EXTEND MODIFY("match_content" VARCHAR2(4000));
ALTER TABLE CS_ALIPAY_BILL_EXTEND MODIFY("verify_text" VARCHAR2(4000));
ALTER TABLE CS_ALIPAY_BILL_EXTEND MODIFY("response_text" VARCHAR2(4000));
ALTER TABLE CS_ALIPAY_BILL_EXTEND MODIFY("def1" VARCHAR2(4000));
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."def1" is '匹配的多客户信息';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."def2" is '手动匹配人';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."def3" is '手动匹配时间';


DROP TABLE "CS_ALIPAY_BILL_INFO";
CREATE TABLE "CS_ALIPAY_BILL_INFO" (
  "bill_no" VARCHAR2(20) NOT NULL,
  "business_no" VARCHAR2(35) NOT NULL,
  "trade_time" DATE NOT NULL,
  "source_name" VARCHAR2(50) NOT NULL,
  "source_account" VARCHAR2(30) NOT NULL,
  "income_amount" VARCHAR2(20) NOT NULL,
  "business_type" VARCHAR2(10),
  "remark" VARCHAR2(100),
  "created_time" DATE
)
TABLESPACE "NNC_DATA01"
LOGGING
NOCOMPRESS
PCTFREE 10
INITRANS 1
STORAGE (
  INITIAL 262144
  NEXT 262144
  MINEXTENTS 1
  MAXEXTENTS 2147483645
  BUFFER_POOL DEFAULT
)
PARALLEL 1
NOCACHE
DISABLE ROW MOVEMENT
;

DROP INDEX CABI_IDX_BUSINESS_NO;
CREATE INDEX CABI_IDX_BUSINESS_NO ON CS_ALIPAY_BILL_INFO("business_no");
DROP INDEX CABI_IDX_TRADE_TIME;
CREATE INDEX CABI_IDX_TRADE_TIME ON CS_ALIPAY_BILL_INFO("trade_time");

COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."bill_no" is '账务流水号';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."business_no" is '业务流水号';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."trade_time" is '交易时间';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."source_name" is '支付宝账号名称';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."source_account" is '支付宝账号';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."income_amount" is '收入金额';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."business_type" is '业务类型';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."remark" is '备注';
COMMENT ON COLUMN CS_ALIPAY_BILL_EXTEND."created_time" is '创建时间';


CREATE OR REPLACE VIEW V_NC_CUSTOMER AS
SELECT pk_customer "customer_id", code "customer_code", name "customer_name", def6 "def_name1", def7 "def_account1", def8 "def_name2",
def9 "def_account2", def10 "def_name3", def11 "def_account3", def12 "def_name4", def13 "def_account4", def14 "def_name5", def15 "def_account5"
  FROM bd_customer
 WHERE NVL(dr, 0) = 0
   AND enablestate = 2;



DROP TABLE "CS_USER_INFO";
CREATE TABLE "CS_USER_INFO" (
  "user_name" VARCHAR2(20) NOT NULL,
  "password" VARCHAR2(20) NOT NULL,
  "mobile" VARCHAR2(15) NOT NULL,
  "email" VARCHAR2(25) DEFAULT '',
  "real_name" VARCHAR2(50) NOT NULL,
  "status" CHAR(1) DEFAULT '1',
  "created_time" DATE,
  "updated_time" DATE
)
TABLESPACE "NNC_DATA01"
LOGGING
NOCOMPRESS
PCTFREE 10
INITRANS 1
STORAGE (
  INITIAL 262144
  NEXT 262144
  MINEXTENTS 1
  MAXEXTENTS 2147483645
  BUFFER_POOL DEFAULT
)
PARALLEL 1
NOCACHE
DISABLE ROW MOVEMENT
;


COMMENT ON COLUMN CS_USER_INFO."user_name" is '用户名';
COMMENT ON COLUMN CS_USER_INFO."password" is '密码';
COMMENT ON COLUMN CS_USER_INFO."mobile" is '手机号';
COMMENT ON COLUMN CS_USER_INFO."email" is '电子邮箱';
COMMENT ON COLUMN CS_USER_INFO."real_name" is '真实姓名';
COMMENT ON COLUMN CS_USER_INFO."status" is '状态：1-启用 2-禁用';
COMMENT ON COLUMN CS_USER_INFO."created_time" is '创建时间';
COMMENT ON COLUMN CS_USER_INFO."updated_time" is '修改时间';

DELETE FROM CS_USER_INFO;
INSERT INTO CS_USER_INFO VALUES ('admin','admin_123','13588888888','','管理员','1', sysdate, sysdate);
INSERT INTO CS_USER_INFO VALUES ('super','super_123','13588888899','','超级管理员','1', sysdate, sysdate);

INSERT INTO CS_USER_INFO VALUES ('gaozhiwen','gaozhiwen_123','13588888888','','高志文','1', sysdate, sysdate);
INSERT INTO CS_USER_INFO VALUES ('jiangzhe','jiangzhe_123','13588888888','','姜哲','1', sysdate, sysdate);
INSERT INTO CS_USER_INFO VALUES ('houshengnan','houshengnan_123','13588888888','','侯胜男','1', sysdate, sysdate);
INSERT INTO CS_USER_INFO VALUES ('liyanbing','liyanbing_123','13588888888','','李彦兵','1', sysdate, sysdate);
INSERT INTO CS_USER_INFO VALUES ('xuexiao','xuexiao_123','13588888888','','薛笑','1', sysdate, sysdate);

INSERT INTO CS_USER_INFO VALUES ('shirongmei','shirongmei_123','13588888888','','史荣梅','1', sysdate, sysdate);
INSERT INTO CS_USER_INFO VALUES ('wumingming','wumingming_123','13588888888','','吴明明','1', sysdate, sysdate);
INSERT INTO CS_USER_INFO VALUES ('liutingting','liutingting_123','13588888888','','刘婷婷','1', sysdate, sysdate);
COMMIT;