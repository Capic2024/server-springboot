package com.capic.server.domain.video.dto;

import com.capic.server.domain.video.entity.Video;

public record VideoReq(
        Long url
) {
    public Video toEntity(String url){
        return Video.builder()
                .url(url)
                .build();
    }
}
