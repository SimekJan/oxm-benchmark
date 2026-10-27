# AGENTS.md

## Project snapshot

This repository is a Java/Spring Boot benchmark harness for comparing document, graph, and relational database workloads. The benchmark flow is intentionally staged: generate data, import it into selected databases, then run queries and save results.

## Build and run

- Build the jar: `mvn clean package`
- If generated OpenAPI sources need to be refreshed, run `mvn clean generate-sources` first.
- Run the full benchmark: `./run.sh`
- Stop all benchmark services and remove generated DB data: `docker compose down -v`

The Docker workflow is defined in `compose.yaml`:

- `mongo`, `neo4j`, and `postgres` are the backing database services.
- `generator`, `importer`, and `runner` are the benchmark stages.
- The generator/importer/runner containers all mount `./oxm_config.yaml` as `/oxm_config.yaml` and use `CONFIG_FILE=/oxm_config.yaml`.
- The packaged app entrypoints are invoked by class name:
  - `generator.cz.cuni.mff.simekjan.Generator`
  - `importer.cz.cuni.mff.simekjan.Importer`
  - `runner.cz.cuni.mff.simekjan.Runner`

## Architecture and data flow

The key execution pattern is:

1. `generator/` creates CSV/JSON files under `data/`.
2. `importer/` reads the selected databases from `databaseToUse` and imports benchmark data into each target.
3. `runner/` executes query sets and writes `data/results.json`.

Important code-level locations:

- `src/main/java/cz/cuni/mff/simekjan/generator/` contains dataset generation logic (`Generator`, `DataProvider`, `DataPools`).
- `src/main/java/cz/cuni/mff/simekjan/importer/` contains database-specific importers (`MongoImporter`, `Neo4jImporter`, `PostgresImporter`, `EmbeddedMongoImporter`).
- `src/main/java/cz/cuni/mff/simekjan/runner/` contains the benchmark execution orchestration (`Runner`, query execution classes, result handling).
- `src/main/java/cz/cuni/mff/simekjan/document/`, `document_embedded/`, `graph/`, and `relational/` hold DB-specific queries, session managers, and adapters.
- `src/main/java/cz/cuni/mff/simekjan/application/dataset/` is the Spring dataset/application layer backed by SQLite (`data/app.db`), configured in `src/main/resources/application.yaml`.

## Configuration conventions

The project uses a root `oxm_config.yaml` as the source of truth.

- `databaseToUse` is a list of enabled backends, e.g. `postgres`, `neo4j`, `mongo`, `mongoEmbedded`.
- `generator`, `importer`, and `runner` each define the relevant runtime options.
- The code reads config through `ConfigLoader.getConfig()` and `ConfigLoader.getSection(...)`; prefer that over hard-coded paths.

Concrete examples from the codebase:

- `Importer.main()` reads `databaseToUse` and conditionally calls `MongoImporter.run()`, `EmbeddedMongoImporter.run()`, `Neo4jImporter.run()`, and `PostgresImporter.run()`.
- `Runner.main()` does the same for query execution, selecting `DocumentQueriesRun`, `EmbeddedDocumentQueriesRun`, `GraphQueriesRun`, and `RelationalQueriesRun` based on the enabled database names.
- `ConfigLoader` resolves `outputDir` under the generator config and maps it to `data/csv` and `data/json` by default.

## Project-specific conventions

- Keep database-specific logic in the DB-specific package tree; do not mix Mongo/Neo4j/Postgres logic into a single service layer.
- Preserve the benchmark stage boundaries: generator != importer != runner.
- When changing generated data volume or benchmark behavior, update the matching config keys in `oxm_config.yaml` and the corresponding Docker/service setup in `compose.yaml`.
- For new DB-specific behavior, follow the existing pattern of a small session manager or importer class plus query runner classes in the matching package.
- `README.md` is the best source for user-facing build and connectivity details; `compose.yaml` and `oxm_config.yaml` are the authoritative operational references for this project.

