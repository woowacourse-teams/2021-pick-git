package com.woowacourse.pickgit.post.application;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.RepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.RepositoriesResponseDto;
import com.woowacourse.pickgit.post.domain.PlatformRepositoryExtractor;
import com.woowacourse.pickgit.post.application.dto.CommentDto;
import com.woowacourse.pickgit.post.application.dto.PostDto;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.PostContent;
import com.woowacourse.pickgit.post.domain.PostRepository;
import com.woowacourse.pickgit.post.domain.comment.Comment;
import com.woowacourse.pickgit.post.domain.content.Image;
import com.woowacourse.pickgit.post.domain.content.Images;
import com.woowacourse.pickgit.post.domain.dto.RepositoryResponseDto;
import com.woowacourse.pickgit.post.presentation.PickGitStorage;
import com.woowacourse.pickgit.post.presentation.dto.HomeFeedRequest;
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
import javax.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PickGitStorage pickgitStorage;
    private final PlatformRepositoryExtractor platformRepositoryExtractor;
    private final TagService tagService;
    private final EntityManager entityManager;

    public PostService(UserRepository userRepository,
        PostRepository postRepository,
        PickGitStorage pickgitStorage,
        PlatformRepositoryExtractor platformRepositoryExtractor,
        TagService tagService, EntityManager entityManager) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.pickgitStorage = pickgitStorage;
        this.platformRepositoryExtractor = platformRepositoryExtractor;
        this.tagService = tagService;
        this.entityManager = entityManager;
    }

    public PostResponseDto write(PostRequestDto postRequestDto) {
        PostContent postContent = new PostContent(postRequestDto.getContent());

        User user = userRepository
            .findByBasicProfile_Name(postRequestDto.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("해당하는 사용자가 없습니다."));

        Post post =
            new Post(postContent, getImages(postRequestDto), postRequestDto.getGithubRepoUrl(), user);

        List<Tag> tags = tagService.findOrCreateTags(new TagsDto(postRequestDto.getTags()));
        post.addTags(tags);

        Post findPost = postRepository.save(post);
        return new PostResponseDto(findPost.getId(), findPost.getImageUrls());
    }

    private Images getImages(PostRequestDto postRequestDto) {
        List<File> files = filesOf(postRequestDto);

        return new Images(getImages(postRequestDto, files));
    }

    private List<Image> getImages(PostRequestDto postRequestDto, List<File> files) {
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
            throw new IllegalArgumentException("업로드에 실패했습니다");
        }
    }

    public CommentDto addComment(CommentRequestDto commentRequestDto) {
        User user = userRepository.findByBasicProfile_Name(commentRequestDto.getUserName())
            .orElseThrow(IllegalArgumentException::new);
        Post post = postRepository.findById(commentRequestDto.getPostId())
            .orElseThrow(IllegalArgumentException::new);
        Comment comment = new Comment(commentRequestDto.getContent());
        user.addComment(post, comment);
        entityManager.flush();
        return CommentDto.from(comment);
    }

    @Transactional(readOnly = true)
    public RepositoriesResponseDto showRepositories(RepositoryRequestDto repositoryRequestDto) {
            List<RepositoryResponseDto> repositories = platformRepositoryExtractor
                .extract(repositoryRequestDto.getToken(), repositoryRequestDto.getUsername());

            return new RepositoriesResponseDto(repositories);
     }

    public List<PostDto> readHomeFeed(HomeFeedRequest homeFeedRequest) {
        int page = Math.toIntExact(homeFeedRequest.getPage());
        int limit = Math.toIntExact(homeFeedRequest.getLimit());
        List<Post> result = entityManager
            .createQuery("select p from Post p order by p.id", Post.class)
            .setFirstResult(page * limit)
            .setMaxResults(limit)
            .getResultList();
        return PostDtoAssembler.assembleFrom(homeFeedRequest.getAppUser(), result);
    }
}
