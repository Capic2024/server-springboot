package com.capic.server.domain.video.controller;

import com.capic.server.domain.video.dto.VideoReq;
import com.capic.server.global.common.ApplicationResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/video")
@Tag(name = "Video", description = "Video API")
public class VideoController {
    //test api
    @PostMapping("/test")
    public ApplicationResponse<Void> addApply(@RequestBody VideoReq req){
//        videoService.testService(req);
        return ApplicationResponse.ok();
    }
}
