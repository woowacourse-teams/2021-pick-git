package com.woowacourse.pickgit.post.application.search.type;

import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class TagType implements SearchType {

    private static final String TYPE_NAME = "tags";

    private final PostRepository postRepository;

    public TagType(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public boolean isSatisfiedBy(String searchType) {
        return TYPE_NAME.equals(searchType);
    }

    @Override
    public List<Post> search(String keywords, Pageable pageable) {
        return postRepository.findAllPostsByTagNames(List.of(keywords.split(" ")), pageable);
    }
}
