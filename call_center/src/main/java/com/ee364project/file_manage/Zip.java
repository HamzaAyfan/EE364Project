package com.ee364project.file_manage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

// import java.util.zip.ZipOutputStream;

public class Zip {

    private static String[] csvFileNames = {"Customer.csv", "Agent.csv", "Problem.csv"};

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

    public static void compressToZip(String outputZip, String csvFilesPlace){

        
        String tempDirectory = "temp";

        try {
            // Create the temporary directory if it doesn't exist
            Files.createDirectories(Paths.get(tempDirectory));

            // Move the CSV files to the temporary directory
            for (String fileName : csvFileNames) {
                Path sourcePath = Paths.get(csvFilesPlace, fileName);
                Path targetPath = Paths.get(tempDirectory, fileName);
                Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            // Compress the CSV files into a ZIP file
            compressZip(tempDirectory, outputZip);

            // Delete the original CSV files
            for (String fileName : csvFileNames) {
                Files.deleteIfExists(Paths.get(tempDirectory, fileName));
            }

            System.out.println("CSV files compressed to " + outputZip + " and original files deleted.");

            deleteExtracted(tempDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void compressZip(String sourceDirectory, String zipFileName) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipFileName);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            File sourceFolder = new File(sourceDirectory);

            // Specify the files to be compressed
            File[] filesToZip = sourceFolder.listFiles();

            if (filesToZip != null) {
                for (File file : filesToZip) {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        ZipEntry zipEntry = new ZipEntry(file.getName());
                        zipOut.putNextEntry(zipEntry);

                        byte[] bytes = new byte[1024];
                        int length;
                        while ((length = fis.read(bytes)) >= 0) {
                            zipOut.write(bytes, 0, length);
                        }
                    }
                }
            }
        }
    }

    // delete the extracted directory after exctracting the zip file
    public static void deleteExtracted(String extractedDirectory) throws IOException{
        try {

            for (String fileName : csvFileNames) {
                Files.deleteIfExists(Paths.get(extractedDirectory, fileName));
            }
            // Delete the directory if it exists and is empty
            Files.walkFileTree(Paths.get(extractedDirectory), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });

            System.out.println("Directory deleted successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
