package com.capic.server.domain.video.dto;

import java.util.List;

public record VideoReq(
        String videoName, List<String> imageName
) {

}
