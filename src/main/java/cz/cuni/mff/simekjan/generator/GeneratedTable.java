package cz.cuni.mff.simekjan.generator;

import java.util.List;
import java.util.UUID;

public record GeneratedTable(
    UUID id,
    String name,
    List<String> columns,
    List<List<Object>> rows
) {
}
