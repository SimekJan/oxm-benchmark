package cz.cuni.mff.simekjan.runner.registration;

import cz.cuni.mff.simekjan.runner.backend.mongodb.MongoQuery;

import java.util.List;

/**
 * Placeholder for Spring Data MongoDB submissions.
 */
public final class MongoRegistration implements QueryRegistration {

    @Override
    public String backendId() {
        return "springdata-mongodb";
    }

    @Override
    public List<MongoQuery<?>> queries() {
        throw new UnsupportedOperationException("Spring Data MongoDB query registration is not implemented");
    }
}
