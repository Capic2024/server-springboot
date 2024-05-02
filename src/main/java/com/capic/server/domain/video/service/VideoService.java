package com.capic.server.domain.video.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.capic.server.domain.video.dto.TargetRes;
import com.capic.server.domain.video.dto.VideoReq;
import com.capic.server.domain.video.dto.VideoRes;
import com.capic.server.global.common.ByteArrayMultipartFile;
import com.capic.server.global.util.s3.S3Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;


@Service
@Transactional
@RequiredArgsConstructor
public class VideoService {

    private final S3Client s3Client;
//    public VideoRes createFolder(){
//        String folderName = UUID.randomUUID().toString();
//        return VideoRes.of(folderName);
//    }

    public S3ObjectInputStream getFile(String imageUrl) {
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
    public TargetRes sendToFlaskWithVideo(String folderName, String VideoName) throws IOException {
        S3ObjectInputStream file = s3Client.get(folderName + "/" + VideoName);
        byte[] content = IOUtils.toByteArray(file);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("video", new ByteArrayResource(content) {
            @Override
            public String getFilename() {
                return VideoName; // 파일 이름 지정
            }
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();

        String url = "http://127.0.0.1:5000/target";
        System.out.println("check1");
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, List<List<String>>> imageDataMap = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, List<List<String>>>>() {});
        List<List<byte[]>> identifiedFaces = new ArrayList<>();

        for (List<String> personImages : imageDataMap.get("images")) {
            List<byte[]> personImagesBytes = new ArrayList<>();
            for (String base64Image : personImages) {
                byte[] imageData = Base64.getDecoder().decode(base64Image);
                personImagesBytes.add(imageData);
            }
            identifiedFaces.add(personImagesBytes);
        }
        // JSON 응답에서 이미지 배열 추출
        System.out.println("upload start");
        for (int personIndex = 0; personIndex < identifiedFaces.size(); personIndex++) {
            List<byte[]> personImages = identifiedFaces.get(personIndex);
            String personFolder = "person" + (personIndex + 1);

            for (int imageIndex = 0; imageIndex < personImages.size(); imageIndex++) {
                byte[] imageData = personImages.get(imageIndex);

                // 이미지를 S3에 업로드
                String imageName = (imageIndex + 1) + ".jpeg";
                String s3ImagePath = folderName + "/" + personFolder + "/" + imageName;

                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(imageData.length);
                metadata.setContentType("image/jpeg"); // 이미지 타입에 따라 변경

                ByteArrayMultipartFile multipartFile = new ByteArrayMultipartFile(imageData, imageName);
                s3Client.upload(s3ImagePath, multipartFile, metadata);
                System.out.println(s3ImagePath+personIndex+"upload");
            }
        }

        return new TargetRes(folderName, identifiedFaces.size());
    }




    //flask에 이미지, 동영상 보내기
    public VideoRes sendToFlaskWithImagesAndVideo(String folderName, VideoReq videoReq) throws IOException{
        // 동영상 파일 가져오기
        S3ObjectInputStream videoFile = s3Client.get(folderName + "/" + videoReq.videoName());
        byte[] videoContent = IOUtils.toByteArray(videoFile);

        // 이미지 파일들을 바이트 배열로 변환하여 리스트에 추가
        List<byte[]> imageContents = new ArrayList<>();
        for (String imageName : videoReq.imageName()) {
            // 이미지 파일 가져오기
            for(int i=0;i<3;i++){
                String imagePath = folderName + "/" + imageName+"/"+(i+1)+".jpeg";
                System.out.println(imagePath);
                S3ObjectInputStream imageFile = s3Client.get(imagePath);
                byte[] imageContent = IOUtils.toByteArray(imageFile);
                imageContents.add(imageContent);
            }
            System.out.println("finish");
        }
        System.out.println("check");
//        return videoReq.videoName();
        // Multipart 요청 생성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        System.out.println("check2");
        body.add("video", new ByteArrayResource(videoContent) {
            @Override
            public String getFilename() {
                return videoReq.videoName(); // 동영상 파일 이름 가져오기
            }
        });
        System.out.println("check3");
        // 이미지 파일들을 요청에 추가
        for (int i = 0; i < imageContents.size(); i++) {
            byte[] content = imageContents.get(i);
            final String imageName = i +".jpeg";
            body.add("image" + (i + 1), new ByteArrayResource(content) {
                @Override
                public String getFilename() {
                    return imageName; // 이미지 파일 이름 가져오기
                }
            });
        }
        System.out.println("check4");

        body.add("imageSize",imageContents.size());
        System.out.println("check5");
        // HTTP 요청 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Flask 서버로 요청 보내기
        RestTemplate restTemplate = new RestTemplate();
        String url =  "http://127.0.0.1:5000/video"; // Flask 서버 URL
        ResponseEntity<byte[]> response = restTemplate.postForEntity(url, requestEntity, byte[].class);

        // Flask에서 반환된 파일을 다시 클라이언트에게 반환
        ByteArrayResource resource = new ByteArrayResource(response.getBody());

        //여기에 나중에 update 구현
        s3Client.update(folderName + "/" + videoReq.videoName(), (MultipartFile) resource);
        return VideoRes.of(folderName, videoReq.videoName(), null);
    }

    //flask에 이미지, 동영상 보내기 test
    public ResponseEntity<Resource> sendToFlaskWithImagesAndVideoTest(String folderName, VideoReq videoReq) throws IOException{
        // 동영상 파일 가져오기
        S3ObjectInputStream videoFile = s3Client.get(folderName + "/" + videoReq.videoName());
        byte[] videoContent = IOUtils.toByteArray(videoFile);

        // 이미지 파일들을 바이트 배열로 변환하여 리스트에 추가
        List<byte[]> imageContents = new ArrayList<>();
        for (String imageName : videoReq.imageName()) {
            // 이미지 파일 가져오기
            S3ObjectInputStream imageFile = s3Client.get(folderName + "/" + imageName);
            byte[] imageContent = IOUtils.toByteArray(imageFile);
            imageContents.add(imageContent);
        }

        // Multipart 요청 생성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("video", new ByteArrayResource(videoContent) {
            @Override
            public String getFilename() {
                return videoReq.videoName(); // 동영상 파일 이름 가져오기
            }
        });

        // 이미지 파일들을 요청에 추가
        for (int i = 0; i < imageContents.size(); i++) {
            byte[] content = imageContents.get(i);
            final String imageName = videoReq.imageName().get(i);
            body.add("image" + (i + 1), new ByteArrayResource(content) {
                @Override
                public String getFilename() {
                    return imageName; // 이미지 파일 이름 가져오기
                }
            });
        }

        body.add("imageSize",imageContents.size());

        // HTTP 요청 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Flask 서버로 요청 보내기
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://13.124.110.226:5000/video"; // Flask 서버 URL
        ResponseEntity<byte[]> response = restTemplate.postForEntity(url, requestEntity, byte[].class);

        // Flask에서 반환된 파일을 다시 클라이언트에게 반환
        ByteArrayResource resource = new ByteArrayResource(response.getBody());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    public void delete(String deleteFile) {
        s3Client.delete(deleteFile);
    }
}