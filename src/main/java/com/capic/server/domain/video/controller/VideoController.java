package com.capic.server.domain.video.controller;

import com.capic.server.domain.video.dto.VideoReq;
import com.capic.server.domain.video.service.VideoService;
import com.capic.server.global.common.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/video")
@Tag(name = "Apply", description = "팀 빌딩 관련 API")
public class VideoController {
    private final VideoService videoService;
    //지원 요청 api
    @Operation(summary = "공고 지원 API", description = "팀 지원하기 모달창에서 지원 요청")
    @PostMapping("/test")
    public ApplicationResponse<Void> addApply(@RequestBody VideoReq req){
//        videoService.testService(req);
        return ApplicationResponse.ok();
    }
}
