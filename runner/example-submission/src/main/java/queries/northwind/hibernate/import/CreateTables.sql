CREATE TABLE employees (
    id bigint PRIMARY KEY,
    employee_id bigint,
    last_name varchar(20) NOT NULL,
    first_name varchar(20) NOT NULL,
    birth_date date,
    hire_date date,
    city varchar(25),
    reports_to bigint
);

CREATE TABLE customers (
    id bigint PRIMARY KEY,
    customer_id bigint,
    company_name varchar(40) NOT NULL,
    city varchar(25)
);

CREATE TABLE suppliers (
    id bigint PRIMARY KEY,
    supplier_id bigint,
    company_name varchar(40) NOT NULL,
    city varchar(25)
);

CREATE TABLE products (
    id bigint PRIMARY KEY,
    product_id bigint,
    product_name varchar(40) NOT NULL,
    unit_price double precision,
    supplier_id bigint,
    category varchar(255)
);

CREATE TABLE orders (
    id bigint PRIMARY KEY,
    order_id bigint,
    order_date date,
    employee_id bigint,
    customer_id bigint
);

--- Add foreign key constraints
ALTER TABLE employees ADD CONSTRAINT employees_reports_to_fk
    FOREIGN KEY (reports_to) REFERENCES employees(id) NOT VALID;

ALTER TABLE orders ADD CONSTRAINT orders_customers_fk
    FOREIGN KEY (customer_id) REFERENCES customers(id) NOT VALID;

ALTER TABLE orders ADD CONSTRAINT orders_employees_fk
    FOREIGN KEY (employee_id) REFERENCES employees(id) NOT VALID;

ALTER TABLE products ADD CONSTRAINT products_suppliers_fk
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id) NOT VALID;

--- many-to-many relationships
CREATE TABLE supplier_relationship (
    supplier_id bigint NOT NULL,
    supplied_to_id bigint NOT NULL,
    PRIMARY KEY (supplier_id, supplied_to_id)
);

CREATE TABLE order_product (
    order_id bigint NOT NULL,
    product_id bigint NOT NULL,
    PRIMARY KEY (order_id, product_id)
);

--- Add foreign key constraints many-to-many relationships
ALTER TABLE supplier_relationship
    ADD CONSTRAINT supplier_relationship_supplier_fk
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id) NOT VALID;

ALTER TABLE supplier_relationship
    ADD CONSTRAINT supplier_relationship_supplied_to_fk
    FOREIGN KEY (supplied_to_id) REFERENCES suppliers(id) NOT VALID;

ALTER TABLE order_product
    ADD CONSTRAINT order_product_order_fk
    FOREIGN KEY (order_id) REFERENCES orders(id) NOT VALID;

ALTER TABLE order_product
    ADD CONSTRAINT order_product_product_fk
    FOREIGN KEY (product_id) REFERENCES products(id) NOT VALID;

--- Create indexes
CREATE INDEX idx_employee_id ON employees (employee_id);
CREATE INDEX idx_customer_id ON customers (customer_id);
CREATE INDEX idx_supplier_id ON suppliers (supplier_id);
CREATE INDEX idx_product_id ON products (product_id);
CREATE INDEX idx_order_id ON orders (order_id);
