package vsvdev.co.ua.s3server.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vsvdev.co.ua.s3server.config.AWSConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class BucketServiceImpl implements BucketService {

    private AWSConfig config;

    public BucketServiceImpl(AWSConfig config) {
        this.config = config;
    }

    @Override
    public List<String> getAllBuckets() {
        String directoryPath = config.getFolder();
        Path directory = Paths.get(directoryPath);

        try {
            return Files.list(directory)
                    .filter(Files::isDirectory)
                    .map(Path::getFileName)
                    .map(Path::toString).toList();
            //.forEach(System.out::println);
        } catch (IOException e) {
            System.out.println("Failed to list directories: " + e.getMessage());

        }

        return null;
    }

    @Override
    public String createBucket(String bucketName) {

        String parentDirPath = config.getFolder();
        Path parentDir = Paths.get(parentDirPath);
        Path subDir = parentDir.resolve(bucketName);

        try {
            // Creating the subdirectory
            Files.createDirectories(subDir);
            System.out.println("Directory created successfully");
        } catch (IOException e) {
            System.out.println("Failed to create directory: " + e.getMessage());
        }
        return bucketName;
    }

    @Override
    public String deleteBucket(String bucketName) {
        String directoryPath = config.getFolder() + "/" + bucketName;
        Path directory = Paths.get(directoryPath);

        try {
            // Deleting the directory and its contents
            Files.walk(directory)
                    .sorted((a, b) -> b.compareTo(a))
                    .map(Path::toFile)
                    .forEach(File::delete);
            System.out.println("Directory deleted successfully");
            return bucketName;
        } catch (IOException e) {
            System.out.println("Failed to delete directory: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<String> getAllBucketObjects(String bucketName) {
        String directoryPath = config.getFolder() + "/" + bucketName;
        Path directory = Paths.get(directoryPath);

        try {
            return Files.list(directory)
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString).toList();
            //.forEach(System.out::println);
        } catch (IOException e) {
            System.out.println("Failed to list directories: " + e.getMessage());

        }

        return null;
    }

    @Override
    public Path getFileBykey(String bucketName, String key) throws IOException {

        String directoryPath = config.getFolder() + "/" + bucketName;
        Path filePath = Path.of(directoryPath + "/" + key);
        if (Files.exists(filePath)) {
            return filePath;
        }
        throw new IOException();
    }

    @Override
    public void saveFile(String bucketName, String key, MultipartFile file) throws IOException {
        String directoryPath = config.getFolder() + "/" + bucketName;
        File fileName = new File(directoryPath + "/" + key);
        file.transferTo(fileName);

    }

    @Override
    public void deleteObject(String bucketName, String key) throws IOException {
        String directoryPath = config.getFolder() + "/" + bucketName;
        Path fileToDeletePath = Paths.get(directoryPath + "/" + key);
        Files.delete(fileToDeletePath);
    }
}
