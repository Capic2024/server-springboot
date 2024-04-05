package com.capic.server.domain.video.dto;

public record VideoRes(
       String folderName
) {
    public static VideoRes of(String folderName){
        return new VideoRes(folderName);
    }
}
