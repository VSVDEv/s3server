package vsvdev.co.ua.s3server.controllers;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vsvdev.co.ua.s3server.config.AWSConfig;
import vsvdev.co.ua.s3server.services.BucketService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static vsvdev.co.ua.s3server.security.SignatureChecker.validRequest;

@RestController
public class BucketController {

    private AWSConfig config;
    private BucketService service;


    public BucketController(AWSConfig config, BucketService service) {
        this.config = config;
        this.service = service;
    }


    @PutMapping("/{bucketName}")
    public ResponseEntity<String> createBucket(@PathVariable String bucketName,HttpServletRequest req) {
        if (!validRequest(req,config)) {
            return ResponseEntity.status(403).body("");
        }
        String bucket = service.createBucket(bucketName);
        return new ResponseEntity<>(bucket, HttpStatus.OK);
    }


    @DeleteMapping("/{bucketName}")
    public ResponseEntity<String> deleteBucket(@PathVariable String bucketName, HttpServletRequest req) {
        if (!validRequest(req,config)) {
            return ResponseEntity.status(403).body("");
        }
        String bucket = service.deleteBucket(bucketName);
        return new ResponseEntity<>(bucket, HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<List<String>> getS3BucketListing(HttpServletRequest req) {
        if (!validRequest(req,config)) {
            return ResponseEntity.status(403).body(Collections.emptyList());
        }

        List<String> res = service.getAllBuckets();
        if (res == null) {
            res = Collections.emptyList();
        }

        return ResponseEntity.ok(res);
    }


    @GetMapping("/{bucketName}")
    public ResponseEntity<List<String>> getS3BucketObjects(@PathVariable String bucketName, HttpServletRequest req) {
        if (!validRequest(req,config)) {
            return ResponseEntity.status(403).body(Collections.emptyList());
        }

        List<String> res = service.getAllBucketObjects(bucketName);
        if (res == null) {
            res = Collections.emptyList();
        }

        return ResponseEntity.ok(res);
    }

    @PutMapping("/{bucketName}/{key}")
    public ResponseEntity<String> uploadS3Object(@PathVariable String bucketName, @PathVariable String key,
                                                  @RequestBody MultipartFile file, HttpServletRequest req) {
        if (!validRequest(req,config)) {
            return ResponseEntity.status(403).body("");
        }
        try {
            service.saveFile(bucketName, key, file);
        } catch (IOException e) {
            return ResponseEntity.ok("Can't upload the file");

        }
        return ResponseEntity.ok("Successfully uploaded the file");
    }

    @GetMapping("/{bucketName}/{key}")
    public ResponseEntity<Resource> downloadS3Object(@PathVariable String bucketName, @PathVariable String key, HttpServletRequest req) {
        if (!validRequest(req,config)) {
            return ResponseEntity.status(403).contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=" + key)
                    .body(null);
        }


        Resource file = null;
        try {
            Path path = service.getFileBykey(bucketName, key);
            file = new PathResource(path);
        } catch (IOException e) {
            return ResponseEntity
                    .status(404)
                    .header("Content-Disposition", "attachment; filename=" + key)
                    .body(null);
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=" + file.getFilename())
                .body(file);
    }

    @DeleteMapping("/{bucketName}/{key}")
    public ResponseEntity<String> deleteObject(@PathVariable String bucketName, @PathVariable String key, HttpServletRequest req) {
        if (!validRequest(req,config)) {
            return ResponseEntity.status(403).body("");
        }

        try {
            service.deleteObject(bucketName, key);
            return new ResponseEntity<>("deleted", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("can't delete", HttpStatus.OK);
        }

    }
}
