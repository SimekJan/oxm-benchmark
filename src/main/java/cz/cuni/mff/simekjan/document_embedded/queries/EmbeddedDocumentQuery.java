package cz.cuni.mff.simekjan.document_embedded.queries;

import com.mongodb.client.MongoDatabase;
import cz.cuni.mff.simekjan.document_embedded.EmbeddedMongoDbManager;
import cz.cuni.mff.simekjan.runner.Query;

public interface EmbeddedDocumentQuery<P> extends Query<P> {

    MongoDatabase db = EmbeddedMongoDbManager.getDb();
}
