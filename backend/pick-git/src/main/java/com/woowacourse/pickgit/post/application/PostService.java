package com.woowacourse.pickgit.post.application;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.exception.post.PostNotBelongToUserException;
import com.woowacourse.pickgit.exception.post.PostNotFoundException;
import com.woowacourse.pickgit.exception.user.UserNotFoundException;
import com.woowacourse.pickgit.post.application.dto.request.AuthUserForPostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.PostDeleteRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.PostUpdateRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.RepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.SearchRepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.LikeResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.LikeUsersResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostImageUrlResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostUpdateResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.RepositoryResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.RepositoryResponsesDto;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PickGitStorage;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.post.domain.util.PlatformRepositoryExtractor;
import com.woowacourse.pickgit.post.domain.util.PlatformRepositorySearchExtractor;
import com.woowacourse.pickgit.post.domain.util.dto.RepositoryNameAndUrl;
import com.woowacourse.pickgit.tag.application.TagService;
import com.woowacourse.pickgit.tag.application.TagsDto;
import com.woowacourse.pickgit.tag.domain.Tag;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class PostService {

    private final TagService tagService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PickGitStorage pickgitStorage;
    private final PlatformRepositoryExtractor platformRepositoryExtractor;
    private final PlatformRepositorySearchExtractor platformRepositorySearchExtractor;

    public PostService(
        TagService tagService,
        UserRepository userRepository,
        PostRepository postRepository,
        PickGitStorage pickgitStorage,
        PlatformRepositoryExtractor platformRepositoryExtractor,
        PlatformRepositorySearchExtractor platformRepositorySearchExtractor
    ) {
        this.tagService = tagService;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.pickgitStorage = pickgitStorage;
        this.platformRepositoryExtractor = platformRepositoryExtractor;
        this.platformRepositorySearchExtractor = platformRepositorySearchExtractor;
    }

    public PostImageUrlResponseDto write(PostRequestDto postRequestDto) {
        Post post = createPost(postRequestDto);
        Post savedPost = postRepository.save(post);

        return PostImageUrlResponseDto.builder()
            .id(savedPost.getId())
            .imageUrls(savedPost.getImageUrls())
            .build();
    }

    private Post createPost(PostRequestDto postRequestDto) {
        String content = postRequestDto.getContent();
        List<MultipartFile> files = postRequestDto.getImages();
        String userName = postRequestDto.getUsername();
        String githubRepoUrl = postRequestDto.getGithubRepoUrl();
        List<Tag> tags = tagService.findOrCreateTags(new TagsDto(postRequestDto.getTags()));

        User user = findUserByName(userName);
        List<String> imageUrls = pickgitStorage.storeMultipartFile(files, userName);

        Post post = createPost(content, githubRepoUrl, user, imageUrls);
        post.addTags(tags);

        return post;
    }

    private Post createPost(
        String content,
        String githubRepoUrl,
        User user,
        List<String> imageUrls
    ) {
        return Post.builder()
            .content(content)
            .images(imageUrls)
            .githubRepoUrl(githubRepoUrl)
            .author(user)
            .build();
    }

    @Transactional(readOnly = true)
    public RepositoryResponsesDto userRepositories(RepositoryRequestDto repositoryRequestDto) {
        String token = repositoryRequestDto.getToken();
        String username = repositoryRequestDto.getUsername();

        Pageable pageable = PageRequest.of(
            Math.toIntExact(repositoryRequestDto.getPage()),
            Math.toIntExact(repositoryRequestDto.getLimit())
        );

        List<RepositoryNameAndUrl> repositoryNameAndUrls = platformRepositoryExtractor
            .extract(token, username, pageable);

        List<RepositoryResponseDto> repositoryResponsesDto =
            createRepositoryResponsesDto(repositoryNameAndUrls);

        return new RepositoryResponsesDto(repositoryResponsesDto);
    }

    @Transactional(readOnly = true)
    public RepositoryResponsesDto searchUserRepositories(
        SearchRepositoryRequestDto searchRepositoryRequestDto
    ) {
        String token = searchRepositoryRequestDto.getToken();
        String username = searchRepositoryRequestDto.getUsername();
        String keyword = searchRepositoryRequestDto.getKeyword();
        int page = searchRepositoryRequestDto.getPage();
        int limit = searchRepositoryRequestDto.getLimit();

        List<RepositoryNameAndUrl> repositoryNameAndUrls =
            platformRepositorySearchExtractor.extract(token, username, keyword, page, limit);

        List<RepositoryResponseDto> repositoryResponseDtos =
            createRepositoryResponsesDto(repositoryNameAndUrls);

        return new RepositoryResponsesDto(repositoryResponseDtos);
    }

    private List<RepositoryResponseDto> createRepositoryResponsesDto(
        List<RepositoryNameAndUrl> repositoryNameAndUrls
    ) {
        return repositoryNameAndUrls.stream()
            .map(toRepositoryResponseDto())
            .collect(toList());
    }

    private Function<RepositoryNameAndUrl, RepositoryResponseDto> toRepositoryResponseDto() {
        return repositoryNameAndUrl -> RepositoryResponseDto.builder()
            .name(repositoryNameAndUrl.getName())
            .url(repositoryNameAndUrl.getUrl())
            .build();
    }

    public LikeResponseDto like(AppUser user, Long postId) {
        User source = findUserByName(user.getUsername());
        Post target = findPostById(postId);

        target.like(source);
        return new LikeResponseDto(target.getLikeCounts(), true);
    }

    public LikeResponseDto unlike(AppUser user, Long postId) {
        User source = findUserByName(user.getUsername());
        Post target = findPostById(postId);

        target.unlike(source);
        return new LikeResponseDto(target.getLikeCounts(), false);
    }

    public PostUpdateResponseDto update(PostUpdateRequestDto updateRequestDto) {
        User user = findUserByName(updateRequestDto.getUsername());
        Post post = findPostById(updateRequestDto.getPostId());

        if (post.isNotWrittenBy(user)) {
            throw new PostNotBelongToUserException();
        }

        List<Tag> tags = tagService.findOrCreateTags(new TagsDto(updateRequestDto.getTags()));

        post.updateContent(updateRequestDto.getContent());
        post.updateTags(tags);

        return PostUpdateResponseDto.builder()
            .content(post.getContent())
            .tags(post.getTagNames())
            .build();
    }

    public void delete(PostDeleteRequestDto deleteRequestDto) {
        User user = findUserByName(deleteRequestDto.getUsername());
        Post post = findPostById(deleteRequestDto.getPostId());

        if (post.isNotWrittenBy(user)) {
            throw new PostNotBelongToUserException();
        }

        user.delete(post);
        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public List<LikeUsersResponseDto> likeUsers(
        AuthUserForPostRequestDto authUserRequestDto,
        Long postId
    ) {
        Post post = findPostWithLikeUsers(postId);
        List<User> likeUsers = post.getLikeUsers();

        if (authUserRequestDto.isGuest()) {
            return createLikeUsersResponseDtoOfGuest(likeUsers);
        }

        User loginUser = findUserByName(authUserRequestDto.getUsername());

        return createLikeUsersResponseDtoOfLoginUser(loginUser, likeUsers);
    }

    private Post findPostWithLikeUsers(Long postId) {
        return postRepository.findPostWithLikeUsers(postId)
            .orElseThrow(PostNotFoundException::new);
    }

    private List<LikeUsersResponseDto> createLikeUsersResponseDtoOfGuest(
        List<User> likeUsers
    ) {
        return likeUsers.stream()
            .map(user ->
                new LikeUsersResponseDto(
                    user.getImage(),
                    user.getName(),
                    null
                )
            ).collect(toList());
    }

    private List<LikeUsersResponseDto> createLikeUsersResponseDtoOfLoginUser(
        User loginUser,
        List<User> likeUsers
    ) {
        return likeUsers.stream()
            .map(user ->
                new LikeUsersResponseDto(
                    user.getImage(),
                    user.getName(),
                    loginUser.isFollowing(user)
                )
            ).collect(toList());
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
