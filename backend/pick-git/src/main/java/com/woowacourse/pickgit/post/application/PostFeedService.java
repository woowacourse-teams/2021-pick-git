package com.woowacourse.pickgit.post.application;

import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import com.woowacourse.pickgit.exception.user.UserNotFoundException;
import com.woowacourse.pickgit.post.application.dto.request.HomeFeedRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.SearchPostsRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.application.search.SearchTypes;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
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

    @Cacheable(
        key = "#homeFeedRequestDto.page",
        value = "homeFeed",
        condition = "#homeFeedRequestDto.guest == true",
        unless = "#result == null || #result.empty"
    )
    public List<PostResponseDto> homeFeed(HomeFeedRequestDto homeFeedRequestDto) {
        Pageable pageable = getPagination(homeFeedRequestDto);
        if (homeFeedRequestDto.isGuest()) {
            return PostDtoAssembler.assembleFrom(null,  postRepository.findAllPosts(pageable));
        }
        User requestUser = findUserByName(homeFeedRequestDto.getRequestUserName());
        List<Post> result = postRepository.findAllAssociatedPostsByUser(requestUser, pageable);
        return PostDtoAssembler.assembleFrom(requestUser, result);
    }

    public List<PostResponseDto> myFeed(HomeFeedRequestDto homeFeedRequestDto) {
        if (homeFeedRequestDto.isGuest()) {
            throw new UnauthorizedException();
        }
        return readUserFeed(homeFeedRequestDto, homeFeedRequestDto.getRequestUserName());
    }

    public List<PostResponseDto> userFeed(HomeFeedRequestDto homeFeedRequestDto, String userName) {
        return readUserFeed(homeFeedRequestDto, userName);
    }

    private List<PostResponseDto> readUserFeed(
        HomeFeedRequestDto homeFeedRequestDto,
        String targetUserName
    ) {
        Pageable pageable = getPagination(homeFeedRequestDto);
        User target = findUserByName(targetUserName);
        List<Post> result = postRepository.findAllPostsByUser(target, pageable);
        if (homeFeedRequestDto.isGuest()) {
            return PostDtoAssembler.assembleFrom(null, result);
        }
        User requestUser = findUserByName(homeFeedRequestDto.getRequestUserName());
        return PostDtoAssembler.assembleFrom(requestUser, result);
    }

    private PageRequest getPagination(HomeFeedRequestDto homeFeedRequestDto) {
        return PageRequest.of(
            homeFeedRequestDto.getPage().intValue(),
            homeFeedRequestDto.getLimit().intValue()
        );
    }

    public List<PostResponseDto> search(SearchPostsRequestDto searchPostsRequestDto) {
        PageRequest pageable = PageRequest.of(
            searchPostsRequestDto.getPage(),
            searchPostsRequestDto.getLimit()
        );
        String keyword = searchPostsRequestDto.getKeyword();
        String type = searchPostsRequestDto.getType();
        String[] keywords = keyword.split(" ");

        List<Post> search = searchTypes.findByTypeName(type).search(keywords, pageable);
        if (searchPostsRequestDto.isGuest()) {
            return PostDtoAssembler.assembleFrom(null, search);
        }

        User user = findUserByName(searchPostsRequestDto.getUserName());
        return PostDtoAssembler.assembleFrom(user, search);
    }

    private User findUserByName(String userName) {
        return userRepository
            .findByBasicProfile_Name(userName)
            .orElseThrow(UserNotFoundException::new);
    }
}
