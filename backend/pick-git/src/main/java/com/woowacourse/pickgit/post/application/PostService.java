package com.woowacourse.pickgit.post.application;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.exception.post.PostNotBelongToUserException;
import com.woowacourse.pickgit.exception.post.PostNotFoundException;
import com.woowacourse.pickgit.exception.user.UserNotFoundException;
import com.woowacourse.pickgit.post.application.dto.CommentResponse;
import com.woowacourse.pickgit.post.application.dto.request.PostDeleteRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.PostUpdateRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.RepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.LikeResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostImageUrlResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostUpdateResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.RepositoriesResponseDto;
import com.woowacourse.pickgit.post.domain.PickGitStorage;
import com.woowacourse.pickgit.post.domain.PlatformRepositoryExtractor;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.PostRepository;
import com.woowacourse.pickgit.post.domain.comment.Comment;
import com.woowacourse.pickgit.post.domain.content.Image;
import com.woowacourse.pickgit.post.domain.content.Images;
import com.woowacourse.pickgit.post.domain.content.PostContent;
import com.woowacourse.pickgit.post.domain.dto.RepositoryResponseDto;
import com.woowacourse.pickgit.post.presentation.dto.request.CommentRequest;
import com.woowacourse.pickgit.post.presentation.dto.request.HomeFeedRequest;
import com.woowacourse.pickgit.tag.application.TagService;
import com.woowacourse.pickgit.tag.application.TagsDto;
import com.woowacourse.pickgit.tag.domain.Tag;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

    public PostService(
        UserRepository userRepository,
        PostRepository postRepository,
        PickGitStorage pickgitStorage,
        PlatformRepositoryExtractor platformRepositoryExtractor,
        TagService tagService
    ) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.pickgitStorage = pickgitStorage;
        this.platformRepositoryExtractor = platformRepositoryExtractor;
        this.tagService = tagService;
    }

    public PostImageUrlResponseDto write(PostRequestDto postRequestDto) {
        PostContent postContent = new PostContent(postRequestDto.getContent());

        User user = findUserByName(postRequestDto.getUsername());

        Post post = new Post(
            getImagesFrom(postRequestDto),
            postContent,
            postRequestDto.getGithubRepoUrl(),
            user
        );

        List<Tag> tags = tagService.findOrCreateTags(new TagsDto(postRequestDto.getTags()));
        post.addTags(tags);

        Post findPost = postRepository.save(post);
        return new PostImageUrlResponseDto(findPost.getId(), findPost.getImageUrls());
    }

    private Images getImagesFrom(PostRequestDto postRequestDto) {
        List<File> files = filesOf(postRequestDto);

        return new Images(getImagesFrom(postRequestDto, files));
    }

    private List<Image> getImagesFrom(PostRequestDto postRequestDto, List<File> files) {
        return pickgitStorage
            .store(files, postRequestDto.getUsername())
            .stream()
            .map(Image::new)
            .collect(toList());
    }

    private List<File> filesOf(PostRequestDto postRequestDto) {
        return postRequestDto.getImages().stream()
            .map(toFile())
            .collect(toList());
    }

    private Function<MultipartFile, File> toFile() {
        return multipartFile -> {
            try {
                return multipartFile.getResource().getFile();
            } catch (IOException e) {
                return tryCreateTempFile(multipartFile);
            }
        };
    }

    private File tryCreateTempFile(MultipartFile multipartFile) {
        try {
            Path tempFile = Files.createTempFile(null, null);
            Files.write(tempFile, multipartFile.getBytes());

            return tempFile.toFile();
        } catch (IOException ioException) {
            throw new PlatformHttpErrorException();
        }
    }

    public CommentResponse addComment(CommentRequest commentRequest) {
        User user = userRepository.findByBasicProfile_Name(commentRequest.getUserName())
            .orElseThrow(() -> new UserNotFoundException(
                "U0001",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "해당하는 사용자를 찾을 수 없습니다."
            ));
        Post post = postRepository.findById(commentRequest.getPostId())
            .orElseThrow(() -> new PostNotFoundException(
                "P0002",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "해당하는 게시물을 찾을 수 없습니다."
            ));
        Comment comment = new Comment(commentRequest.getContent());
        user.addComment(post, comment);
        return CommentResponse.from(comment);
    }

    @Transactional(readOnly = true)
    public RepositoriesResponseDto showRepositories(RepositoryRequestDto repositoryRequestDto) {
        List<RepositoryResponseDto> repositories = platformRepositoryExtractor
            .extract(repositoryRequestDto.getToken(), repositoryRequestDto.getUsername());

        return new RepositoriesResponseDto(repositories);
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> readHomeFeed(HomeFeedRequest homeFeedRequest) {
        Pageable pageable = PageRequest.of(homeFeedRequest.getPage(), homeFeedRequest.getLimit());
        List<Post> result = postRepository.findAllPosts(pageable);
        return PostDtoAssembler.assembleFrom(homeFeedRequest.getAppUser(), result);
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> readMyFeed(HomeFeedRequest homeFeedRequest) {
        return readFeed(homeFeedRequest, homeFeedRequest.getAppUser().getUsername());
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> readUserFeed(HomeFeedRequest homeFeedRequest, String username) {
        return readFeed(homeFeedRequest, username);
    }

    private List<PostResponseDto> readFeed(HomeFeedRequest homeFeedRequest, String username) {
        AppUser appUser = homeFeedRequest.getAppUser();
        User target = findUserByName(username);
        Pageable pageable = PageRequest.of(homeFeedRequest.getPage(), homeFeedRequest.getLimit());
        List<Post> result = postRepository.findAllPostsByUser(target, pageable);
        return PostDtoAssembler.assembleFrom(appUser, result);
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

    private Post findPostById(Long id) {
        return postRepository.findById(id)
            .orElseThrow(() -> new PostNotFoundException(
                "P0002",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "해당하는 게시물을 찾을 수 없습니다.")
            );
    }

    public PostUpdateResponseDto update(PostUpdateRequestDto updateRequestDto) {
        User user = findUserByName(updateRequestDto.getUsername());
        Post post = findPostByUser(user);

        List<Tag> tags = tagService.findOrCreateTags(new TagsDto(updateRequestDto.getTags()));

        post.updateContent(updateRequestDto.getContent());
        post.updateTags(tags);

        Post updatedPost = findPostByUser(user);

        return PostUpdateResponseDto.builder()
            .content(updatedPost.getContent())
            .tags(updatedPost.getTagNames())
            .build();
    }

    public void delete(PostDeleteRequestDto deleteRequestDto) {
        User user = findUserByName(deleteRequestDto.getUsername());
        Post post = findPostByUser(user);

        postRepository.delete(post);
    }

    private User findUserByName(String username) {
        return userRepository
            .findByBasicProfile_Name(username)
            .orElseThrow(() -> new UserNotFoundException(
                "U0001",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "해당하는 사용자를 찾을 수 없습니다."));
    }

    private Post findPostByUser(User user) {
        return postRepository.findByUser(user)
            .orElseThrow(PostNotBelongToUserException::new);
    }
}
