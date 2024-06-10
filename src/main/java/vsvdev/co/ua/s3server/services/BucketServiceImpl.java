package vsvdev.co.ua.s3server.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vsvdev.co.ua.s3server.config.AWSConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BucketServiceImpl implements BucketService {

    private static final Pattern CHUNK_SIGNATURE_PATTERN =
            Pattern.compile("\\b[0-9a-f]{1,3};chunk-signature=[0-9a-f]{64}\\b");
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
    public void saveMultipart(String bucketName, String key, MultipartFile file) throws IOException {
        String directoryPath = config.getFolder() + "/" + bucketName;
        Files.createDirectories(Paths.get(directoryPath));
        File fileName = new File(directoryPath + "/" + key);
        file.transferTo(fileName);

    }

    @Override
    public void saveFile(String bucketName, String key, File file) throws IOException {
        String directoryPath = config.getFolder() + "/" + bucketName;

        File fileName = new File(directoryPath + "/" + key);

        try (FileInputStream fis = new FileInputStream(file);
             FileOutputStream fos = new FileOutputStream(fileName)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }

            System.out.println("File saved successfully to disk at: " + fileName.getAbsolutePath());
        } catch (IOException e) {
            throw new IOException("Failed to save file to disk", e);
        }
    }

    @Override
    public void saveText(String bucketName, String key, String text) throws IOException {
        String directoryPath = config.getFolder() + "/" + bucketName;
        Path filePath = Paths.get(directoryPath + "/" + key);
        String cleaned = removeChunkSignatures(text);
        Files.createDirectories(Paths.get(directoryPath));
        Files.write(filePath, cleaned.getBytes());
    }

    @Override
    public void deleteObject(String bucketName, String key) throws IOException {
        String directoryPath = config.getFolder() + "/" + bucketName;
        Path fileToDeletePath = Paths.get(directoryPath + "/" + key);
        Files.delete(fileToDeletePath);
    }

    @Override
    public void saveInputStream(String bucketName, String key, byte[] inputStream) throws IOException {

        String directoryPath = config.getFolder() + "/" + bucketName;
        Path filePath = Paths.get(directoryPath + "/" + key);

        try (final FileOutputStream fout = new FileOutputStream(filePath.toString());
             final ObjectOutputStream out = new ObjectOutputStream(fout)
        ) {
            new ByteArrayInputStream(inputStream).transferTo(out);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static String removeChunkSignatures(String content) {

        Matcher matcher = CHUNK_SIGNATURE_PATTERN.matcher(content);
        return matcher.replaceAll("");
    }
}

