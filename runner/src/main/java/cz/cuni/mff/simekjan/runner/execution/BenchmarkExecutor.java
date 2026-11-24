package cz.cuni.mff.simekjan.runner.execution;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.io.PrintWriter;
import java.nio.file.Path;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

/**
 * Launches the registered benchmark queries through JUnit.
 */
public final class BenchmarkExecutor {

    /**
     * Runs the benchmark test factory.
     *
     * @param configFile path to the runner configuration
     * @return whether at least one benchmark ran successfully without failures
     */
    public boolean execute(Path configFile) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
            .selectors(selectClass(BenchmarkTests.class))
            .configurationParameter(BenchmarkParameters.CONFIG_FILE, configFile.toString())
            .build();
        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);
        var summary = listener.getSummary();
        PrintWriter output = new PrintWriter(System.err, true);
        summary.printTo(output);
        summary.printFailuresTo(output);
        return summary.getTestsFoundCount() > 0
            && summary.getTestsSucceededCount() == summary.getTestsFoundCount()
            && summary.getTotalFailureCount() == 0;
    }
}
