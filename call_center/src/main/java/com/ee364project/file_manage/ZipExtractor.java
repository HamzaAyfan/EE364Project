package com.ee364project.file_manage;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipExtractor {

    public static void extractZip(File zipFile, String outputDirectory) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFile.toPath()))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                Path outputPath = Paths.get(outputDirectory, entry.getName());

                // Create directories if they don't exist
                Files.createDirectories(outputPath.getParent());

                // Extract the entry
                Files.copy(zipInputStream, outputPath, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}
