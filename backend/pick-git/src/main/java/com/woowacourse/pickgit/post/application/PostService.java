package com.woowacourse.pickgit.post.application;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.post.application.dto.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.PostResponseDto;
import com.woowacourse.pickgit.post.application.dto.RepositoryDto;
import com.woowacourse.pickgit.post.application.dto.TokenDto;
import com.woowacourse.pickgit.post.domain.PlatformExtractor;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.PostContent;
import com.woowacourse.pickgit.post.domain.PostRepository;
import com.woowacourse.pickgit.post.domain.comment.Comment;
import com.woowacourse.pickgit.post.domain.content.Image;
import com.woowacourse.pickgit.post.domain.content.Images;
import com.woowacourse.pickgit.post.infrastructure.dto.RepositoryResponse;
import com.woowacourse.pickgit.post.presentation.PickGitStorage;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PickGitStorage pickgitStorage;
    private final PlatformExtractor platformExtractor;

    public PostService(
        UserRepository userRepository,
        PostRepository postRepository,
        PickGitStorage pickgitStorage,
        PlatformExtractor platformExtractor) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.pickgitStorage = pickgitStorage;
        this.platformExtractor = platformExtractor;
    }

    public PostResponseDto write(PostRequestDto postRequestDto) {
        PostContent postContent = new PostContent(postRequestDto.getContent());

        User user = userRepository
            .findByBasicProfile_Name(postRequestDto.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("해당하는 사용자가 없습니다."));

        Post post = postRepository.save(
            new Post(postContent, getImages(postRequestDto),
                postRequestDto.getGithubRepoUrl(), user));

        return new PostResponseDto(post.getId(), post.getImageUrls());
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

    public CommentResponseDto addComment(CommentRequestDto commentRequestDto) {
        User user = userRepository.findByBasicProfile_Name(commentRequestDto.getUserName())
            .orElseThrow(IllegalArgumentException::new);
        Post post = postRepository.findById(commentRequestDto.getPostId())
            .orElseThrow(IllegalArgumentException::new);
        Comment comment = new Comment(commentRequestDto.getContent());
        user.addComment(post, comment);
        return new CommentResponseDto(user.getName(), comment.getContent());
    }

    public RepositoryDto getRepositories(TokenDto tokenDto) {
        List<RepositoryResponse> repositories = platformExtractor
            .getRepositories(tokenDto.getAccessToken());

        return new RepositoryDto(repositories);
    }
}
