package com.capic.server.domain.video.repository;

import com.capic.server.domain.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video,Long> {

}