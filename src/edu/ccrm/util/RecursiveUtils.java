package edu.ccrm.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class RecursiveUtils {

    public static long calculateDirectorySize(Path path) throws IOException {
        if (!Files.isDirectory(path)) {
            return Files.size(path);
        }

        long size = 0;
        try (Stream<Path> stream = Files.list(path)) {
            for (Path entry : stream.toList()) {
                size += calculateDirectorySize(entry);
            }
        }
        return size;
    }
}