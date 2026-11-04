package cz.cuni.mff.simekjan.executor;

import java.util.List;

public record JavaSourceBundle(List<JavaSourceFile> files) {

    public record JavaSourceFile(String path, String content) {
    }
}
