package cz.cuni.mff.simekjan.generator;

import java.util.List;
import java.util.UUID;

/**
 * Represents generated records and their table schema.
 */
public record GeneratedTable(
    UUID id,
    String name,
    List<String> columns,
    List<List<Object>> rows
) {
}
