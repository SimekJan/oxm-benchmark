package cz.cuni.mff.simekjan.runner.registration;

import cz.cuni.mff.simekjan.runner.backend.hibernate.HibernateQuery;

import java.nio.file.Path;
import java.util.List;

/**
 * Hibernate classes discovered from a submitted source tree.
 */
public final class HibernateRegistration implements QueryRegistration, AutoCloseable {
    
    private final Path sourceDir;
    private HibernateSubmission submission;
    private HibernateSubmission.Batch batch;

    public HibernateRegistration(Path sourceDir) {
        this.sourceDir = sourceDir;
    }

    @Override
    public String backendId() {
        return "hibernate";
    }

    @Override
    public List<HibernateQuery<?>> queries() {
        batch = submission().newBatch();
        return batch.queries();
    }

    public List<Class<?>> entities() {
        return submission().entities();
    }

    public List<HibernateSubmission.Failure> failures() {
        if (batch == null) {
            throw new IllegalStateException("Queries have not been registered");
        }
        return batch.failures();
    }

    private HibernateSubmission submission() {
        if (submission == null) {
            submission = HibernateSubmission.load(sourceDir);
        }
        return submission;
    }

    @Override
    public void close() {
        if (submission != null) {
            submission.close();
            submission = null;
            batch = null;
        }
    }
}
