package cz.cuni.mff.simekjan.runner.registration;

import cz.cuni.mff.simekjan.runner.api.Query;

import java.util.List;

/**
 * Query classes submitted for one backend.
 */
public interface QueryRegistration {

    /**
     * @return backend identifier used in runner configuration
     */
    String backendId();

    /**
     * @return query classes registered for this backend
     */
    List<? extends Class<? extends Query<?, ?>>> queries();
}
