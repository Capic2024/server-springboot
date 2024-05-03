package com.capic.server.global.util.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.capic.server.global.exception.ApplicationException;
import com.capic.server.global.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Client {

    private final AmazonS3 amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.baseUrl}")
    private String baseUrl;

    public void upload(String imageUrl,MultipartFile multipartFile, ObjectMetadata objectMetadata) {
        // Validation
        if(multipartFile.isEmpty()) {
            throw new ApplicationException(ErrorCode.INVALID_VALUE_EXCEPTION);
        }

        // Check File upload
        try {
            amazonS3Client.putObject(new PutObjectRequest(bucket, imageUrl, multipartFile.getInputStream(), objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public String update(String fileName, MultipartFile newFile) {
        // Validation
        if(newFile.isEmpty()) {
            throw new ApplicationException(ErrorCode.INVALID_VALUE_EXCEPTION);
        }

        // Business Logic
        // 기존 파일이 존재하는지 확인
        if(!amazonS3Client.doesObjectExist(bucket, fileName)) {
            throw new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION);
        }

        // 새 파일 메타데이터 설정
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(newFile.getContentType());
        objectMetadata.setContentLength(newFile.getSize());

        // 새 파일 업로드
        try {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, newFile.getInputStream(), objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new ApplicationException(ErrorCode.INTERNAL_SERVER_EXCEPTION);
        }

        // Response
        return baseUrl + fileName;
    }

    public S3ObjectInputStream get(String imageUrl){
        // Validation
        if(!amazonS3Client.doesObjectExist(bucket,imageUrl)) {
            throw new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION);
        }

        // Business Logic
        S3Object s3Object = amazonS3Client.getObject(bucket,imageUrl);
        if(s3Object.getObjectContent() !=null){
            System.out.println(s3Object.getObjectContent());
        }
        // Response
        return s3Object.getObjectContent();
    }

//    public byte[] download(String imageUrl) {
//
//        S3ObjectInputStream inputStream = get(imageUrl);
//        try {
//            return IOUtils.toByteArray(inputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public void delete(String imageUrl) {
        // Validation
//        String fileName = imageUrl.substring(baseUrl.length()); // baseUrl 제거
        if(!amazonS3Client.doesObjectExist(bucket, imageUrl)) {
            throw new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION);
        }

        // Business Logic
        try {
            amazonS3Client.deleteObject(bucket, imageUrl);
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.INTERNAL_SERVER_EXCEPTION);
        }
    }


}
