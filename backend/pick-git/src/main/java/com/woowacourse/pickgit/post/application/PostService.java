package com.woowacourse.pickgit.post.application;

import com.woowacourse.pickgit.post.application.dto.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.PostResponseDto;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.PostContent;
import com.woowacourse.pickgit.post.domain.PostRepository;
import com.woowacourse.pickgit.post.domain.content.Image;
import com.woowacourse.pickgit.post.domain.content.Images;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository,
        UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public PostResponseDto writePost(PostRequestDto postRequestDto) {
        List<Image> requestImages = generateImages(postRequestDto);
        Images images = new Images(requestImages);
        PostContent content = new PostContent(postRequestDto.getContent());
        User user = userRepository.findByBasicProfile_Name(postRequestDto.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("해당하는 사용자가 없습니다."));

        Post post = postRepository.save(new Post(images, content, user));

        return new PostResponseDto(post.getId());
    }

    private List<Image> generateImages(PostRequestDto postRequestDto) {
        List<Image> images = new ArrayList<>();

        for (String requestImage : postRequestDto.getImages()) {
            Image image = new Image(requestImage);
            images.add(image);
        }

        return images;
    }
}
