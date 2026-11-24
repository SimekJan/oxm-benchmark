# OXM-benchmark

As of now the benchmark is in an early stage of development and is not yet fully functional. The readme reflects an
older version of the application and will be updated as the project progresses. Currently, the closest to a finished
documentation are the [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) file and the [docs/FLOW.md](docs/FLOW.md) file with
visualizations of the intended inner workings.

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

Generated files in `/data` are not automatically deleted ofter Docker ends running.

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

Similarly, for the rest of the DBs.

## Building the Spring Boot app

To build the project first you need to compile the generated source by running:

```bash
mvn clean generate-sources
```

in root directory. This will generate API endpoints interfaces to be implemented by the code and model for data transfer
in the API methods.

## Standalone runner

The standalone runner is built independently with `mvn -f runner/pom.xml package`. Java sources under
`runner/example-submission/src/main/java` are compiled into the runner. Replace the placeholder Hibernate entity and
query with your own classes and update `HibernateRegistration` before rebuilding. All classes share one classpath.

In IntelliJ IDEA, link `runner/pom.xml` as a Maven project (right-click the file and select **Add as Maven Project**),
then reload Maven projects. This imports the runner's dependencies and example source root without combining the builds.

Run the packaged application with `java -jar runner/target/oxm-benchmark-runner-1.0-SNAPSHOT.jar [oxm_config.yaml]`.
The optional configuration path defaults to `CONFIG_FILE` or `oxm_config.yaml`.