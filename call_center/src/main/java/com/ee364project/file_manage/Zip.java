package com.ee364project.file_manage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.ee364project.Fx.MainSceneController;

/**
 * The {@code Zip} class provides utility methods for extracting and compressing
 * ZIP files.
 * It includes methods to extract files from a ZIP archive and compress files
 * into a ZIP archive.
 * The class also supports compressing and decompressing CSV files related to a
 * specific application.
 *
 * {@code csvFileNames}: an array of CSV file names to be handled by the class.
 */
public class Zip {

    /**
     * The csvFileNames array contains the names of the CSV files that will be handled by the class.
     */
    private static String[] csvFileNames = { "Customer.csv", "Agent.csv", "Problem.csv", "Department.csv" };

    /**
     * Extracts files from a ZIP archive to the specified output directory.
     *
     * @param zipFile         the ZIP file to extract.
     * @param outputDirectory the directory where the files will be extracted.
     */
    public static void extractZip(File zipFile, String outputDirectory) {
        Path zipPath = zipFile.toPath();

        try (
                InputStream file = Files.newInputStream(zipPath);
                ZipInputStream zipInputStream = new ZipInputStream(file);) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String name = entry.getName();
                Path outputPath = Paths.get(outputDirectory, name);

                // Create directories if they don't exist
                Path outputPathParent = outputPath.getParent();
                Files.createDirectories(outputPathParent);

                // Extract the entry
                Files.copy(zipInputStream, outputPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            MainSceneController.showErrorAlert("Invalid File", "The file does not exist");
            return;
        }
    }

    /**
     * Compresses CSV files into a ZIP archive.
     *
     * @param outputZip     the path where the ZIP file will be created.
     * @param csvFilesPlace the directory containing the CSV files to be compressed.
     */
    public static void compressToZip(String outputZip, String csvFilesPlace) {
        String tempDirectory = "temp";

        try {
            // Create the temporary directory if it doesn't exist
            Path tempDir = Paths.get(tempDirectory);
            Files.createDirectories(tempDir);

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
                Path fileToDel = Paths.get(tempDirectory, fileName);
                Files.deleteIfExists(fileToDel);
            }
            deleteExtracted(tempDirectory);
        } catch (IOException e) {
            MainSceneController.showErrorAlert("Compression Failed", "This file can't be compressed");
            return;
        }

    }

    /**
     * Compresses the contents of a directory into a ZIP file.
     *
     * @param sourceDirectory the path to the directory whose contents will be
     *                        compressed.
     * @param zipFileName     the path where the ZIP file will be created.
     * @throws IOException if an I/O error occurs during the compression process.
     */
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

    /**
     * Deletes the extracted directory and its contents.
     *
     * @param extractedDirectory the path to the directory to be deleted.
     * @throws IOException if an I/O error occurs during the deletion process.
     */
    public static void deleteExtracted(String extractedDirectory) throws IOException {
        try {

            for (String fileName : csvFileNames) {
                Files.deleteIfExists(Paths.get(extractedDirectory, fileName));
            }
            // Delete the directory if it exists and is empty
            Path path = Paths.get(extractedDirectory);
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
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
        } catch (IOException e) {
            MainSceneController.showErrorAlert("Deletion Failed", "Unable to delete");
            return;
        }
    }
}
