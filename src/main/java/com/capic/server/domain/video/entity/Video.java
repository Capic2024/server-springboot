package com.capic.server.domain.video.entity;

import com.capic.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "apply")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Video extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false,columnDefinition = "bigint")
    private Long id;

    @Column(name = "s3 url",nullable = false,columnDefinition = "varchar(500)")
    private String url;


    @Builder
    public Video(String url){
        this.url = url;
    }
}
