package cz.cuni.mff.simekjan.runner.registration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resolves backend registrations for configured database entries.
 */
public final class RegistrationRegistry {

    private final Map<String, QueryRegistration> registrations;

    public RegistrationRegistry() {
        this(List.of(new HibernateRegistration(), new MongoRegistration(), new Neo4jRegistration()));
    }

    public RegistrationRegistry(List<? extends QueryRegistration> registrations) {
        Map<String, QueryRegistration> byId = new HashMap<>();
        for (QueryRegistration registration : registrations) {
            if (byId.putIfAbsent(registration.backendId(), registration) != null) {
                throw new IllegalArgumentException("Duplicate backend registration: " + registration.backendId());
            }
        }
        this.registrations = Map.copyOf(byId);
    }

    public QueryRegistration get(String backendId) {
        QueryRegistration registration = registrations.get(backendId);
        if (registration == null) {
            throw new IllegalArgumentException("Unknown backend registration: " + backendId);
        }
        return registration;
    }
}
