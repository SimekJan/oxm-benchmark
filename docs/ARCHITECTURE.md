# Application Architecture

The application consists of one Spring Boot app. The external client calls the app's API; selected databases are
dependencies, not additional application components.

## Main App Workflow

The Main App's API coordinates analysis, dataset generation and metadata storage within the same application.

```mermaid
flowchart TD
    subgraph SPRING_BOOT_APP["Spring Boot application"]
        API[Spring Boot API]
        Analyzer[Analyzer]
        DatasetWorkbench[Dataset Workbench]
        Generator[Generator]
        Metadata[(SQLite metadata)]
    end

    RunInputs([Zipped app with entities and queries])
    Runner[Runner]
    RunInputs --> Runner
    Client[External client] -->|API requests| API
    API -->|Dataset definitions| Analyzer
    API <-->|Updates to data| DatasetWorkbench
    API -->|Generate data| Generator
    Analyzer -->|Uniform DB schema| DatasetWorkbench
    Analyzer -->|Save analyzed schema| Metadata
    DatasetWorkbench -->|Schema, distributions + optional initial data| Generator
    DatasetWorkbench <-->|Update distributions| Metadata
    Generator -->|Generated records| DataFiles[CSV or JSON files]
    Databases[(Selected databases / repositories)]
    DataFiles -->|Import data| Databases
    Metadata -->|Uses metadata| Generator
    Runner -->|Execute queries| Databases
    Metadata ~~~ Databases
    Metadata ~~~ DataFiles
    classDef external stroke: #64748B, stroke-width: 1.5px
    classDef mainApp stroke: #2563EB, stroke-width: 1.5px
    class Client, Databases external
    class API, Analyzer, Generator, Runner, Metadata, DatasetWorkbench mainApp
```

### Responsibilities

- The **API** implements the OpenAPI contract, accepts client commands, coordinates internal services, and returns
  status or results.
- The **metadata store** holds dataset and workflow metadata. Generated benchmark records belong in the selected
  database repositories.
- The **Dataset Definition & Profiling module** accepts dataset definitions in multiple formats, analyzes their
  structure, and lets users review and edit the resulting metadata on demand. Its **Analyzer** handles definition
  analysis and can profile supplied sample data to infer distributions and relationships. Possible future name can be
  **Dataset Workbench**.
- On an explicit user command, the **Dataset Definition & Profiling module** sends the complete metadata to the
  Generator: schema, distributions, and optional initial data.
- The **Generator** has one responsibility: generate dataset records from the supplied schema and distributions,
  optionally incorporating initial data, and either write the result as CSV or JSON files.
- The **Runner** executes queries against the selected databases, measures execution time, and collects per-query
  results. Needs Zipped ready project as input. Not decided exactly how will be connected to the rest of the app.

### Workflow

1. **Accept and analyze.** The API receives dataset information and OXM/query definitions. The Analyzer validates them
   and prepares definitions for the internal generation and query workflows. The exact validation and preparation
   responsibilities remain to be detailed.
2. **Generate or update records.** The Generator applies client-provided benchmark settings directly to the selected
   database repositories. The dataset is ready for benchmarking after required generation work completes successfully.
3. **Run queries and return results.** The Runner executes queries against the selected databases, collects measurements
   and per-query results, and makes completed results available through the API.

## Docker Compose Harness and Scope

Docker Compose starts the Main App and selected database services. The client remains external and calls the Main App
API.

- The **Main App** owns the public API, application-level dataset metadata, generation workflow, and query-run workflow.
- The **client** is an external API consumer, not an application component.
- The **selected databases** are external dependencies. They can be started by Docker or configured in `oxm_config.yaml`
  before the app starts.