package vsvdev.co.ua.s3server.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface BucketService {

    public List<String> getAllBuckets();
    public String createBucket(String bucketName);
    public String deleteBucket(String bucketName);
    public List<String> getAllBucketObjects(String bucketName);
    public Path getFileBykey(String bucketName, String key) throws IOException;
    public void saveMultipart(String bucketName, String key, MultipartFile file) throws IOException;
    void saveFile(String bucketName, String key, File file) throws IOException;
    void saveText(String bucketName, String key, String text) throws IOException;

    void deleteObject(String bucketName, String key) throws IOException;
    void saveInputStream(String bucketName, String key, byte[] inputStream) throws IOException;
}
