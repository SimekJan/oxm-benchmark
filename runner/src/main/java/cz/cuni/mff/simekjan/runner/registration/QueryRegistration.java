package cz.cuni.mff.simekjan.runner.registration;

import cz.cuni.mff.simekjan.runner.api.Query;

import java.util.List;

/**
 * Queries submitted for one backend.
 */
public interface QueryRegistration {

    /**
     * @return backend identifier used in runner configuration
     */
    String backendId();

    /**
     * @return queries registered for this backend
     */
    List<? extends Query<?, ?>> queries();

    /**
     * @return submission classes needed to configure the backend
     */
    default List<Class<?>> mappedClasses() {
        return List.of();
    }
}
