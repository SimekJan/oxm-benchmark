package cz.cuni.mff.simekjan.runner.backend.mongodb;

import cz.cuni.mff.simekjan.runner.api.Query;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Query using a Spring Data MongoDB template.
 *
 * @param <P> query parameter type
 */
public interface MongoQuery<P> extends Query<MongoTemplate, P> {

    @Override
    default Class<MongoTemplate> contextType() {
        return MongoTemplate.class;
    }
}
