package com.woowacourse.pickgit.post.application;


import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import com.woowacourse.pickgit.exception.post.PostNotFoundException;
import com.woowacourse.pickgit.exception.user.UserNotFoundException;
import com.woowacourse.pickgit.post.application.dto.PostDtoAssembler;
import com.woowacourse.pickgit.post.application.dto.request.HomeFeedRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.SearchPostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.SearchPostsRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.application.search.SearchTypes;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostFeedService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final SearchTypes searchTypes;

    @Cacheable(key = "#homeFeedRequestDto.pageable.pageNumber",
        value = "homeFeed",
        condition = "#homeFeedRequestDto.guest == true",
        unless = "#result == null || #result.empty"
    )
    public List<PostResponseDto> allHomeFeed(HomeFeedRequestDto homeFeedRequestDto) {
        Pageable pageable = homeFeedRequestDto.getPageable();

        if (homeFeedRequestDto.isGuest()) {
            return PostDtoAssembler.postResponseDtos(null, postRepository.findAllPosts(pageable));
        }

        User requestUser = findUserByName(homeFeedRequestDto.getRequestUserName());

        return PostDtoAssembler.postResponseDtos(requestUser, postRepository.findAllPosts(pageable));
    }

    public List<PostResponseDto> followingHomeFeed(HomeFeedRequestDto homeFeedRequestDto) {
        Pageable pageable = homeFeedRequestDto.getPageable();

        User requestUser = findUserByName(homeFeedRequestDto.getRequestUserName());
        List<Post> result = postRepository.findAllAssociatedPostsByUser(requestUser, pageable);

        return PostDtoAssembler.postResponseDtos(requestUser, result);
    }

    public List<PostResponseDto> userFeed(HomeFeedRequestDto homeFeedRequestDto, String userName) {
        Pageable pageable = homeFeedRequestDto.getPageable();
        User target = findUserByName(userName);
        List<Post> result = postRepository.findAllPostsByUser(target, pageable);

        if (homeFeedRequestDto.isGuest()) {
            return PostDtoAssembler.postResponseDtos(null, result);
        }

        User requestUser = findUserByName(homeFeedRequestDto.getRequestUserName());
        return PostDtoAssembler.postResponseDtos(requestUser, result);
    }

    public List<PostResponseDto> search(
        SearchPostsRequestDto searchPostsRequestDto,
        Pageable pageable
    ) {
        List<Post> search = searchTypes
            .findByTypeName(searchPostsRequestDto.getType())
            .search(searchPostsRequestDto.getKeyword(), pageable);

        if (searchPostsRequestDto.isGuest()) {
            return PostDtoAssembler.postResponseDtos(null, search);
        }

        User user = findUserByName(searchPostsRequestDto.getUserName());
        return PostDtoAssembler.postResponseDtos(user, search);
    }

    public PostResponseDto searchById(SearchPostRequestDto searchPostRequestDto) {
        Long id = searchPostRequestDto.getId();
        String userName = searchPostRequestDto.getUserName();

        Post post = postRepository.findById(id)
            .orElseThrow(PostNotFoundException::new);
        if (searchPostRequestDto.isGuest()) {
            return PostDtoAssembler.assembleFrom(null, post);
        }

        User user = findUserByName(userName);
        return PostDtoAssembler.assembleFrom(user, post);
    }

    private User findUserByName(String userName) {
        return userRepository
            .findByBasicProfile_Name(userName)
            .orElseThrow(UserNotFoundException::new);
    }
}
