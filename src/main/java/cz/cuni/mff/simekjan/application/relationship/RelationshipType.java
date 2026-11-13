package cz.cuni.mff.simekjan.application.relationship;

/**
 * Defines relationship cardinalities supported by a dataset schema.
 */
public enum RelationshipType {
    ONE_TO_ONE("one-to-one"),
    ONE_TO_MANY("one-to-many"),
    MANY_TO_ONE("many-to-one"),
    MANY_TO_MANY("many-to-many");

    private final String value;

    RelationshipType(String value) {
        this.value = value;
    }

    public static RelationshipType fromValue(String value) {
        for (RelationshipType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported relationship type: " + value);
    }
}
