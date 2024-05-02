package com.capic.server.domain.video.controller;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.capic.server.domain.video.dto.TargetRes;
import com.capic.server.domain.video.dto.VideoRes;
import org.springframework.core.io.Resource;
import com.capic.server.domain.video.dto.VideoReq;
import com.capic.server.domain.video.service.VideoService;
import com.capic.server.global.common.ApplicationResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RequiredArgsConstructor
@RestController
@RequestMapping("/video")
@Tag(name = "Video", description = "Video API")
public class VideoController {
    final VideoService videoService;
    //test api
    @PostMapping("/test")
    public ApplicationResponse<Void> addApply(@RequestBody VideoReq req){
//        videoService.testService(req);
        return ApplicationResponse.ok();
    }

    @GetMapping("/httptestx")
    public String test(){
//        videoService.testService(req);
        return "good";
    }

    @PostMapping("/image-test")
    public S3ObjectInputStream addApply(@RequestParam String imageUrl){
        S3ObjectInputStream file = videoService.getFile(imageUrl);
        return file;
    }

    @PostMapping("/flask")
    public ResponseEntity<Resource> sendToFlask(@RequestParam String imageUrl) throws IOException {
        return videoService.sendToFlask(imageUrl);
    }

//    @GetMapping("/folder")
//    public ApplicationResponse<VideoRes> sendToFlaskWithImages() {
//        return  ApplicationResponse.ok(videoService.createFolder());
//    }

    @PostMapping("/flask-target")
    public ApplicationResponse<TargetRes> sendToFlaskWithImages(@RequestParam String folderName, String videoName) throws IOException {
        return ApplicationResponse.ok(videoService.sendToFlaskWithVideo(folderName,videoName));
    }

    @PostMapping("/flask-mosaic")
    public ApplicationResponse<VideoRes> sendToFlaskWithImagesAndVideo(@RequestParam String folderName, @RequestBody VideoReq videoReq) throws IOException {
        return  ApplicationResponse.ok(videoService.sendToFlaskWithImagesAndVideo(folderName,videoReq));
    }
    @PostMapping("/test-flask-mosaic")
    public ResponseEntity<Resource> sendToFlaskWithImagesAndVideoTest(@RequestParam String folderName,@RequestBody VideoReq videoReq) throws IOException {
        return videoService.sendToFlaskWithImagesAndVideoTest(folderName,videoReq);
    }
    @PostMapping("/delete")
    public ApplicationResponse<Void> delete(@RequestParam String deleteFile) throws IOException {
        videoService.delete(deleteFile);
        return ApplicationResponse.ok();
    }

}
