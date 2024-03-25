package com.capic.server.domain.video.dto;

public record VideoRes(
       String url
) {
    public static VideoRes of(String url){
        return new VideoRes(url);
    }
}
