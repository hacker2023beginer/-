package org.vladproj.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileService {

    public static String read(String path) throws IOException {
        return Files.readString(Path.of(path));
    }

    public static void write(String path, String content) throws IOException {
        Files.writeString(Path.of(path), content);
    }
}

