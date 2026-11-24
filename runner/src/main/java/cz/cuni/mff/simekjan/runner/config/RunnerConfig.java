package cz.cuni.mff.simekjan.runner.config;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Settings for a benchmark run.
 *
 * @param numberOfRuns measured repetitions per query
 * @param warmup       whether to run a discarded warm-up
 * @param queries      selected query IDs; empty selects all
 * @param paramsFile   path to query parameters
 * @param timeoutMs    requested query timeout in milliseconds
 * @param outputDir    report directory
 * @param databases    selected database entries
 */
public record RunnerConfig(
    int numberOfRuns,
    boolean warmup,
    List<String> queries,
    Path paramsFile,
    long timeoutMs,
    Path outputDir,
    List<Database> databases
) {

    /**
     * Settings for a database and backend combination.
     *
     * @param id                  unique runner database entry identifier
     * @param database            enabled logical database name
     * @param backend             backend identifier
     * @param connectionOverrides overrides for shared connection settings
     * @param options             backend-specific options
     */
    public record Database(
        String id,
        String database,
        String backend,
        Map<String, Object> connectionOverrides,
        Map<String, Object> options
    ) {
    }
}
