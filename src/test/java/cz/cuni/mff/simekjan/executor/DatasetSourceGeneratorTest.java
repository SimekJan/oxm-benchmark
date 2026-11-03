package cz.cuni.mff.simekjan.executor;

import cz.cuni.mff.simekjan.model.DatasetSchema;
import cz.cuni.mff.simekjan.model.DatasetSchemaRelationshipsInner;
import cz.cuni.mff.simekjan.model.DatasetSchemaTablesInner;
import cz.cuni.mff.simekjan.model.DatasetSchemaTablesInnerColumnsInner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.tools.ToolProvider;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatasetSourceGeneratorTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void generatesCompilableClassesForTablesAndRelationships() throws Exception {
        DatasetSchema schema = new DatasetSchema()
            .tables(List.of(
                new DatasetSchemaTablesInner("sales-order", List.of(
                    new DatasetSchemaTablesInnerColumnsInner(
                        "order-id",
                        DatasetSchemaTablesInnerColumnsInner.DatatypeEnum.INTEGER
                    ),
                    new DatasetSchemaTablesInnerColumnsInner(
                        "created-at",
                        DatasetSchemaTablesInnerColumnsInner.DatatypeEnum.DATE
                    )
                )),
                new DatasetSchemaTablesInner("line item", List.of(
                    new DatasetSchemaTablesInnerColumnsInner(
                        "quantity",
                        DatasetSchemaTablesInnerColumnsInner.DatatypeEnum.INTEGER
                    )
                ))
            ))
            .relationships(List.of(new DatasetSchemaRelationshipsInner(
                "line-items",
                "sales-order",
                "order-id",
                "line item",
                "quantity",
                DatasetSchemaRelationshipsInner.RelationshipTypeEnum.ONE_TO_MANY
            )));

        Path sources = temporaryDirectory.resolve("sources");
        List<String> generatedClasses = new DatasetSourceGenerator()
            .generate(UUID.randomUUID(), schema, sources);
        Path classes = temporaryDirectory.resolve("classes");
        Files.createDirectories(classes);

        assertEquals(2, generatedClasses.size());
        assertTrue(ToolProvider.getSystemJavaCompiler().run(
            null,
            null,
            null,
            "-d", classes.toString(),
            sources.resolve(generatedClasses.getFirst().replace('.', '/') + ".java").toString(),
            sources.resolve(generatedClasses.getLast().replace('.', '/') + ".java").toString()
        ) == 0);
    }

    @Test
    void rejectsNamesThatCollideAfterJavaIdentifierConversion() {
        DatasetSchema schema = new DatasetSchema()
            .tables(List.of(
                new DatasetSchemaTablesInner("sales-order", List.of(
                    new DatasetSchemaTablesInnerColumnsInner(
                        "order-id",
                        DatasetSchemaTablesInnerColumnsInner.DatatypeEnum.INTEGER
                    ),
                    new DatasetSchemaTablesInnerColumnsInner(
                        "order id",
                        DatasetSchemaTablesInnerColumnsInner.DatatypeEnum.INTEGER
                    )
                ))
            ))
            .relationships(List.of());

        assertThrows(IllegalArgumentException.class, () ->
            DatasetSourceGenerator.validateSchema(schema)
        );
    }
}
