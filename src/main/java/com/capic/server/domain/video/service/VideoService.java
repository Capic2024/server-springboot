package com.capic.server.domain.video.service;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.capic.server.global.util.s3.S3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Transactional
@RequiredArgsConstructor
public class VideoService {
    private final S3Client s3Client;
    public S3ObjectInputStream getFile(String imageUrl) {
        // Validation

        // Business Logic
        S3ObjectInputStream file = s3Client.get(imageUrl);
        // Response
        return file;
    }

}