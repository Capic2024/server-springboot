package com.capic.server.domain.video.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Null;

import java.util.List;

public record VideoRes(
       String folderName,
       String videoName,
       @Null
       @JsonInclude(JsonInclude.Include.NON_NULL)
       List<String> imageNames
) {
    public static VideoRes of(String folderName,String videoName,
                              List<String> imageNames){
        return new VideoRes(folderName,videoName,imageNames);
    }
}
