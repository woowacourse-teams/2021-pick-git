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

public class PostWriteParameters extends Parameters {

    public PostWriteParameters(
        RequestSpecification spec,
        HttpMethod httpMethod,
        String url,
        Object... params
    ) {
        super(spec, httpMethod, url, params);
    }

    public PostWriteParameters initAllParams() {
        githubRepoUrl("https://github.com/sample");
        content("testContent");
        tags("java", "c++");
        images(FileFactory.getTestImage1File(), FileFactory.getTestImage2File());

        return this;
    }

    public PostWriteParameters githubRepoUrl(String githubRepoUrl) {
        formParam(GITHUB_REPO_URL, githubRepoUrl);
        return this;
    }

    public PostWriteParameters content(String content) {
        formParam(CONTENT, content);
        return this;
    }

    public PostWriteParameters tags(String... tags) {
        tags(List.of(tags));
        return this;
    }

    public PostWriteParameters tags(List<String> tags) {
        formParam(TAGS, String.join(",", tags));

        return this;
    }

    public PostWriteParameters images(File... file) {
        multiparts(List.of(file));
        return this;
    }

    public PostWriteParameters param(String key, Object value) {
        formParam(key, value);
        return this;
    }
}
