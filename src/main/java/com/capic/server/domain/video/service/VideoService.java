package com.capic.server.domain.video.service;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.capic.server.global.util.s3.S3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;


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
    public ResponseEntity<Resource> sendToFlask(String imageUrl) throws IOException {
        S3ObjectInputStream file = s3Client.get(imageUrl);
        byte[] content = IOUtils.toByteArray(file);

        MultiValueMap<String,Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(content) {
            @Override
            public String getFilename() {
                return imageUrl; // 파일 이름 지정
            }
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();

        String url = "http://13.124.110.226:5000/video";

        ResponseEntity<byte[]> response = restTemplate.postForEntity(url, requestEntity, byte[].class);

        // Flask에서 반환된 파일을 다시 클라이언트에게 반환
        ByteArrayResource resource = new ByteArrayResource(response.getBody());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}