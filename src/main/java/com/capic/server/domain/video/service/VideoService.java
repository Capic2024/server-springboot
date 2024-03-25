package com.capic.server.domain.video.service;

import com.capic.server.domain.video.dto.VideoReq;
import com.capic.server.domain.video.entity.Video;
import com.capic.server.domain.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class VideoService {
    private final VideoRepository videoRepository;

//    public List<MyPageRes> getMyApplyList(Member member) {
//        // Validation
//
//        // Business Logic
//        List<Apply> applyList = applyRepository.findAllByMemberAndDeletedAtIsNull(member);
//        List<Long> postIdList = applyList.stream()
//                .map(apply -> apply.getPost().getPostId())
//                .toList();
//
//        List<Post> postList = postRepository.findAllByPostIdInAndStatusAndDeletedAtIsNull(postIdList, RECRUITING);
//
//        // Response
//        return postList.stream()
//                .map(post -> {
//                    List<String> categoryList = post.getCategories().stream()
//                            .map(category -> category.getCategoryType().toString())
//                            .toList();
//
//                    return MyPageRes.of(post, member, categoryList);
//                })
//                .collect(Collectors.toList());
//    }


}
