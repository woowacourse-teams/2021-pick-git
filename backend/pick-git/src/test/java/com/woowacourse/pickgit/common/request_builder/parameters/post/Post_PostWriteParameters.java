package com.woowacourse.pickgit.common.request_builder.parameters.post;

import static com.woowacourse.pickgit.common.PickgitHeaders.CONTENT;
import static com.woowacourse.pickgit.common.PickgitHeaders.GITHUB_REPO_URL;
import static com.woowacourse.pickgit.common.PickgitHeaders.TAGS;

import com.woowacourse.pickgit.common.factory.FileFactory;
import com.woowacourse.pickgit.common.request_builder.parameters.Parameters;
import io.restassured.specification.RequestSpecification;
import java.io.File;
import java.util.List;
import org.springframework.http.HttpMethod;

public class Post_PostWriteParameters extends Parameters {

    public Post_PostWriteParameters(
        RequestSpecification spec,
        HttpMethod httpMethod,
        String url,
        Object... params
    ) {
        super(spec, httpMethod, url, params);
    }

    public Post_PostWriteParameters initAllParams() {
        githubRepoUrl("https://github.com/sample");
        content("testContent");
        tags("java", "c++");
        images(FileFactory.getTestImage1File(), FileFactory.getTestImage2File());

        return this;
    }

    public Post_PostWriteParameters githubRepoUrl(String githubRepoUrl) {
        setParam(GITHUB_REPO_URL, githubRepoUrl);
        return this;
    }

    public Post_PostWriteParameters content(String content) {
        setParam(CONTENT, content);
        return this;
    }

    public Post_PostWriteParameters tags(String... tags) {
        tags(List.of(tags));
        return this;
    }

    public Post_PostWriteParameters tags(List<String> tags) {
        setParam(TAGS, String.join(",", tags));

        return this;
    }

    public Post_PostWriteParameters images(File... file) {
        setMultiparts(List.of(file));
        return this;
    }
}
