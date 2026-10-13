# OXM-benchmark 

## Running the experiment
```bash
# first run this to compile JAR file, or after changing Java code
mvn clean package

# Build and run the whole benchmark
# Prepares specified dbs (all by default) and runs the program
# DBs keeps running after the experiment ends
./run.sh

# Run to end the benchmark, stop the databases if in Docker mode
# and remove the generated data from the dbs images
docker compose down -v
```

After the `generator` runs, you can find the generated data in `/data` folder.

After the `importer` runs, you can find the data imported to the running instances of databases.

After the `runner` runs, you can find the results in `/data/results.json`.

Generated files in `/data` are not automatically deleted ofter Docker ends runing.

## Connecting to the DBs

- All ports for the databases are moved from the standard ports to avoid clash 
  with local instances during development and benchmark usage. 

### MongoDB

- From (e.g. Mongo Compass) connect to `mongodb://localhost:27018/`.
- Authentication **TBD**, there is none right now

### Neo4j

- From (e.g. Neo4j Desktop) connect to `bolt://localhost:7688`.
- Use username `neo4j` and password `oxm_password`.

### PostgreSQL

- From (e.g. DBeaver) connect to `jdbc:postgresql://localhost:5433/oxm_benchmark`.
- Use username `postgre` and password `oxm_password`.

## Config options

All config can be changed in `oxm_config.yaml` in root of the project.

### Mongo

To use local instance of MongoDB instead of container change: 

```yaml
importer:
  mongo:
    connection: mongodb://host.docker.internal:27017
```

Similarly for the rest of the DBs.

## Building the Spring Boot app

To build the project first you need to compile the generated source by running:

```bash
mvn clean generate-sources
```
in root directory. This will generate API endpoints interfaces to be implemented by the code and model for data transfer in the API methods.

