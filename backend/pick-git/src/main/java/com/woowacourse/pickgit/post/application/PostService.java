package com.woowacourse.pickgit.post.application;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.exception.post.PostNotBelongToUserException;
import com.woowacourse.pickgit.exception.post.PostNotFoundException;
import com.woowacourse.pickgit.exception.user.UserNotFoundException;
import com.woowacourse.pickgit.post.application.dto.PostDtoAssembler;
import com.woowacourse.pickgit.post.application.dto.request.AuthUserForPostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.PostDeleteRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.PostUpdateRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.RepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.SearchRepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.LikeResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.LikeUsersResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostUpdateResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.RepositoryResponseDto;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PickGitStorage;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.post.domain.util.PlatformRepositoryExtractor;
import com.woowacourse.pickgit.post.domain.util.PlatformRepositorySearchExtractor;
import com.woowacourse.pickgit.post.domain.util.dto.RepositoryNameAndUrl;
import com.woowacourse.pickgit.tag.application.TagService;
import com.woowacourse.pickgit.tag.application.dto.TagsDto;
import com.woowacourse.pickgit.tag.domain.Tag;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostService {

    private static final String REDIRECT_URL = "/api/posts/%s/%d";

    private final TagService tagService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PickGitStorage pickgitStorage;
    private final PlatformRepositoryExtractor platformRepositoryExtractor;
    private final PlatformRepositorySearchExtractor platformRepositorySearchExtractor;

    @CacheEvict(value = "homeFeed", allEntries = true)
    @Transactional
    public Long write(PostRequestDto postRequestDto) {
        Post savedPost = postRepository.save(createPost(postRequestDto));
        return savedPost.getId();
    }

    private Post createPost(PostRequestDto postRequestDto) {
        List<Tag> tags = tagService.findOrCreateTags(new TagsDto(postRequestDto.getTags()));

        User user = findUserByName(postRequestDto.getUsername());
        List<String> imageUrls = pickgitStorage.storeMultipartFile(
            postRequestDto.getImages(),
            postRequestDto.getUsername()
        );

        Post post = PostDtoAssembler.post(user, postRequestDto, imageUrls);
        post.addTags(tags);

        return post;
    }

    public List<RepositoryResponseDto> userRepositories(RepositoryRequestDto repositoryRequestDto) {
        String token = repositoryRequestDto.getToken();
        String username = repositoryRequestDto.getUsername();

        Pageable pageable = repositoryRequestDto.getPageable();

        List<RepositoryNameAndUrl> repositoryNameAndUrls =
            platformRepositoryExtractor.extract(token, username, pageable);

        return PostDtoAssembler.repositoryResponsesDtos(repositoryNameAndUrls);
    }

    public List<RepositoryResponseDto> searchUserRepositories(
        SearchRepositoryRequestDto searchRepositoryRequestDto
    ) {
        String token = searchRepositoryRequestDto.getToken();
        String username = searchRepositoryRequestDto.getUsername();
        String keyword = searchRepositoryRequestDto.getKeyword();


        List<RepositoryNameAndUrl> repositoryNameAndUrls =
            platformRepositorySearchExtractor.extract(token, username, keyword, searchRepositoryRequestDto.getPageable());

        return PostDtoAssembler.repositoryResponsesDtos(repositoryNameAndUrls);
    }

    @CacheEvict(value = "homeFeed", allEntries = true)
    @Transactional
    public LikeResponseDto like(AppUser user, Long postId) {
        User source = findUserByName(user.getUsername());
        Post target = findPostById(postId);

        target.like(source);
        return new LikeResponseDto(target.getLikeCounts(), true);
    }

    @CacheEvict(value = "homeFeed", allEntries = true)
    @Transactional
    public LikeResponseDto unlike(AppUser user, Long postId) {
        User source = findUserByName(user.getUsername());
        Post target = findPostById(postId);

        target.unlike(source);
        return new LikeResponseDto(target.getLikeCounts(), false);
    }

    @CacheEvict(value = "homeFeed", allEntries = true)
    @Transactional
    public PostUpdateResponseDto update(PostUpdateRequestDto updateRequestDto) {
        User user = findUserByName(updateRequestDto.getUsername());
        Post post = findPostById(updateRequestDto.getPostId());

        if (post.isNotWrittenBy(user)) {
            throw new PostNotBelongToUserException();
        }

        List<Tag> tags = tagService.findOrCreateTags(new TagsDto(updateRequestDto.getTags()));

        post.updateContent(updateRequestDto.getContent());
        post.updateTags(tags);

        return PostDtoAssembler.postUpdateRequestDto(post);
    }

    @CacheEvict(value = "homeFeed", allEntries = true)
    @Transactional
    public void delete(PostDeleteRequestDto deleteRequestDto) {
        User user = findUserByName(deleteRequestDto.getUsername());
        Post post = findPostById(deleteRequestDto.getPostId());

        user.delete(post);
        postRepository.delete(post);
    }

    public List<LikeUsersResponseDto> likeUsers(
        AuthUserForPostRequestDto authUserRequestDto,
        Long postId
    ) {
        Post post = findPostWithLikeUsers(postId);
        List<User> likeUsers = post.getLikeUsers();

        if (authUserRequestDto.isGuest()) {
            return PostDtoAssembler.createLikeUsersResponseDtoOfGuest(likeUsers);
        }

        User loginUser = findUserByName(authUserRequestDto.getUsername());

        return PostDtoAssembler.createLikeUsersResponseDtoOfLoginUser(loginUser, likeUsers);
    }

    private Post findPostWithLikeUsers(Long postId) {
        return postRepository.findPostWithLikeUsers(postId)
            .orElseThrow(PostNotFoundException::new);
    }

    private Post findPostById(Long id) {
        return postRepository.findById(id)
            .orElseThrow(PostNotFoundException::new);
    }

    private User findUserByName(String username) {
        return userRepository
            .findByBasicProfile_Name(username)
            .orElseThrow(UserNotFoundException::new);
    }
}
