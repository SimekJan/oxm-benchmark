# Importing datasets guide

- For now **only PostgreSQL** is supported. Other databases will be supported in the future.

## Quick step-by-step guide

1. Generate dataset into `/generatedData` (default)
2. Connect to the Postgres Docker image (or your local installation)
   - to connect to the Docker image use: 
    ```shell
    docker compose exec postgres psql -U postgres -d postgres
    ```
    Or change the provided username and database name to that specified in `oxm_config.yaml` file.
3. Create a tables for the benchmark:
   ```sql
   CREATE TABLE employees (
       id bigint PRIMARY KEY,
       employee_id bigint, 
       first_name text, 
       last_name text, 
       birth_date date, 
       hire_date date, 
       city text, 
       reports_to bigint
   );
   ```
4. Copy generated data into the table:
   ```sql
   COPY employees FROM '/generatedData/csv/employees.csv'
   WITH (FORMAT csv, HEADER true);
   ```
5. Repeat for all tables in dataset.
6. Add foreign key constraints, do not validate here:
   ```sql
   ALTER TABLE orders ADD CONSTRAINT orders_customers_fk
   FOREIGN KEY (customer_id) REFERENCES customers(id) NOT VALID;
   ```
7. Repeat for all foreign key constraints in dataset.
8. Optionally validate after. All generated data should be valid, so this should not fail:
   ```sql
   ALTER TABLE orders VALIDATE CONSTRAINT orders_customers_fk;
   ```
9. Create indexes for the tables, if needed.
    ```sql
    CREATE INDEX ON orders (order_id);
    ```

### Copy-paste command to use prepared SQL script
```shell
docker compose exec -T postgres psql -U postgres -d postgres \
  -v ON_ERROR_STOP=1 --single-transaction \
  < runner/example-submission/src/main/java/queries/northwind/hibernate/import/CreateTables.sql
```

### Possible improvements to importing speed got PostgreSQL

```sql
-- Session-level, before the load
SET synchronous_commit = off;        -- biggest single win, safe for a load
SET maintenance_work_mem = '1GB';    -- for index builds afterwards
SET work_mem = '64MB';
SET max_wal_size = '8GB';            -- fewer checkpoints during load
SET wal_compression = on;            -- if CPU is not the bottleneck

CREATE UNLOGGED TABLE my_table (     -- no WAL at all for the heap
  id bigint, name text, amount numeric, created date
);

-- One transaction, one COPY, no indexes
BEGIN;
COPY my_table FROM '/data/big.csv' WITH (FORMAT csv, HEADER true);
CREATE INDEX ON my_table (created, id);   -- build once, at the end
ANALYZE my_table;
COMMIT;
```