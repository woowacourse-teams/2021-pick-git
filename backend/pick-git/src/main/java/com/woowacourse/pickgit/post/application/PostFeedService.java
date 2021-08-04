package com.woowacourse.pickgit.post.application;

import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import com.woowacourse.pickgit.exception.user.UserNotFoundException;
import com.woowacourse.pickgit.post.application.dto.request.HomeFeedRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.SearchPostsRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.application.search.type.SearchTypes;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PostFeedService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final SearchTypes searchTypes;

    public PostFeedService(
        PostRepository postRepository,
        UserRepository userRepository,
        SearchTypes searchTypes
    ) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.searchTypes = searchTypes;
    }

    public List<PostResponseDto> homeFeed(HomeFeedRequestDto homeFeedRequestDto) {
        return readFeed(homeFeedRequestDto, Optional.empty());
    }

    public List<PostResponseDto> myFeed(HomeFeedRequestDto homeFeedRequestDto) {
        String userName = homeFeedRequestDto.getRequestUserName();

        if (Objects.isNull(userName)) {
            throw new UnauthorizedException();
        }

        return readFeed(homeFeedRequestDto, Optional.of(userName));
    }

    public List<PostResponseDto> userFeed(HomeFeedRequestDto homeFeedRequestDto, String userName) {
        return readFeed(homeFeedRequestDto, Optional.of(userName));
    }

    private List<PostResponseDto> readFeed(
        HomeFeedRequestDto homeFeedRequestDto,
        Optional<String> userName
    ) {
        int page = homeFeedRequestDto.getPage().intValue();
        int limit = homeFeedRequestDto.getLimit().intValue();
        String requestUserName = homeFeedRequestDto.getRequestUserName();
        boolean isGuest = homeFeedRequestDto.isGuest();

        Pageable pageable = PageRequest.of(page, limit);
        List<Post> result = getPostsBy(userName, pageable);

        User requestUser = findUserByName(requestUserName);

        return PostDtoAssembler.assembleFrom(requestUser, isGuest, result);
    }

    private List<Post> getPostsBy(Optional<String> userName, Pageable pageable) {
        return userName
            .map(this::findUserByName)
            .map(target -> postRepository.findAllPostsByUser(target, pageable))
            .orElse(postRepository.findAllPosts(pageable));
    }

    private User findUserByName(String userName) {
        if(Objects.isNull(userName)) {
            return null;
        }

        return userRepository
            .findByBasicProfile_Name(userName)
            .orElseThrow(UserNotFoundException::new);
    }

    public List<PostResponseDto> search(SearchPostsRequestDto searchPostsRequestDto) {
        String keyword = searchPostsRequestDto.getKeyword();
        String type = searchPostsRequestDto.getType();
        int page = searchPostsRequestDto.getPage();
        int limit = searchPostsRequestDto.getLimit();
        String userName = searchPostsRequestDto.getUserName();
        boolean isGuest = searchPostsRequestDto.isGuest();

        PageRequest pageable = PageRequest.of(page, limit);
        String[] keywords = keyword.split(" ");

        List<Post> search = searchTypes.findByTypeName(type).search(keywords, pageable);
        User user = findUserByName(userName);

        return PostDtoAssembler.assembleFrom(user, isGuest, search);
    }
}
