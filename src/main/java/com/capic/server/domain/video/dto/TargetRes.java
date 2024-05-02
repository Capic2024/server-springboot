package com.capic.server.domain.video.dto;

public record TargetRes(
    String folderName,
    int personSize
) {
    public static TargetRes of(String folderName,int psersonSize){
        return new TargetRes(folderName,psersonSize);
    }
}
