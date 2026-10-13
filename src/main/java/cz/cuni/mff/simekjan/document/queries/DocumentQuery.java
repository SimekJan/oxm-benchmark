package cz.cuni.mff.simekjan.document.queries;

import com.mongodb.client.MongoDatabase;
import cz.cuni.mff.simekjan.document.MongoDbManager;
import cz.cuni.mff.simekjan.runner.Query;

public interface DocumentQuery<P> extends Query<P> {

    MongoDatabase db = MongoDbManager.getDb();
}
