-- BI 智能体数据库表结构
-- 维度表

-- 商品维度表
CREATE TABLE dim_product
(
    product_id    BIGINT PRIMARY KEY COMMENT '商品唯一ID',
    product_name  VARCHAR(255) COMMENT '商品名称',
    category_id   BIGINT COMMENT '商品分类ID',
    category_name VARCHAR(255) COMMENT '商品分类名称',
    brand         VARCHAR(255) COMMENT '品牌',
    cost_price    DECIMAL(10, 2) COMMENT '成本价',
    retail_price  DECIMAL(10, 2) COMMENT '建议零售价'
) COMMENT '商品维度表';

-- 门店维度表
CREATE TABLE dim_store
(
    store_id   BIGINT PRIMARY KEY COMMENT '门店唯一ID',
    store_name VARCHAR(255) COMMENT '门店名称',
    province   VARCHAR(100) COMMENT '所在省份',
    city       VARCHAR(100) COMMENT '所在城市',
    address    VARCHAR(255) COMMENT '门店详细地址',
    open_date  DATE COMMENT '开业日期'
) COMMENT '门店维度表';

-- 时间维度表
CREATE TABLE dim_date
(
    date_id    INT PRIMARY KEY COMMENT '日期(yyyyMMdd)',
    date_value DATE COMMENT '实际日期',
    year       INT COMMENT '年份',
    quarter    INT COMMENT '季度(1-4)',
    month      INT COMMENT '月份(1-12)',
    day        INT COMMENT '日(1-31)',
    week_num   INT COMMENT '周数',
    weekday    INT COMMENT '星期(1-7)'
) COMMENT '时间维度表';

-- 顾客维度表
CREATE TABLE dim_customer
(
    customer_id   BIGINT PRIMARY KEY COMMENT '顾客ID',
    customer_name VARCHAR(255) COMMENT '顾客姓名',
    gender        VARCHAR(10) COMMENT '性别',
    age           INT COMMENT '年龄',
    city          VARCHAR(100) COMMENT '所在城市',
    province      VARCHAR(100) COMMENT '所在省份',
    week_num      INT COMMENT '周数',
    weekday       INT COMMENT '星期(1-7)'
) COMMENT '顾客维度表';

-- 事实表

-- 销售事实表
CREATE TABLE fact_sales
(
    sales_id        BIGINT PRIMARY KEY COMMENT '销售记录唯一ID',
    date_id         INT COMMENT '销售日期',
    customer_id     BIGINT COMMENT '顾客ID',
    store_id        BIGINT COMMENT '销售门店',
    product_id      BIGINT COMMENT '销售商品',
    quantity        INT COMMENT '销售数量',
    sales_amount    DECIMAL(10, 2) COMMENT '销售金额(实收)',
    original_amount DECIMAL(10, 2) COMMENT '应收金额(未折扣)',
    discount_amount DECIMAL(10, 2) COMMENT '折扣金额',
    CONSTRAINT fk_sales_date FOREIGN KEY (date_id) REFERENCES dim_date (date_id),
    CONSTRAINT fk_sales_customer FOREIGN KEY (customer_id) REFERENCES dim_customer (customer_id),
    CONSTRAINT fk_sales_store FOREIGN KEY (store_id) REFERENCES dim_store (store_id),
    CONSTRAINT fk_sales_product FOREIGN KEY (product_id) REFERENCES dim_product (product_id)
) COMMENT '销售事实表';

-- 库存事实表
CREATE TABLE fact_inventory
(
    inventory_id  BIGINT PRIMARY KEY COMMENT '库存记录ID',
    date_id       INT COMMENT '日期',
    store_id      BIGINT COMMENT '门店',
    product_id    BIGINT COMMENT '商品',
    stock_qty     INT COMMENT '库存数量',
    stock_value   DECIMAL(10, 2) COMMENT '库存金额',
    last_in_time  DATETIME COMMENT '最近一次入库时间',
    last_out_time DATETIME COMMENT '最近一次出库时间',
    CONSTRAINT fk_inventory_date FOREIGN KEY (date_id) REFERENCES dim_date (date_id),
    CONSTRAINT fk_inventory_store FOREIGN KEY (store_id) REFERENCES dim_store (store_id),
    CONSTRAINT fk_inventory_product FOREIGN KEY (product_id) REFERENCES dim_product (product_id)
) COMMENT '库存事实表';
