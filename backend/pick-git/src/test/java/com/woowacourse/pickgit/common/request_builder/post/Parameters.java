package com.woowacourse.pickgit.common.request_builder.post;

import static com.woowacourse.pickgit.common.PickgitHeaders.CONTENT;
import static com.woowacourse.pickgit.common.PickgitHeaders.GITHUB_REPO_URL;
import static com.woowacourse.pickgit.common.PickgitHeaders.IMAGES;
import static com.woowacourse.pickgit.common.PickgitHeaders.TAGS;

import com.woowacourse.pickgit.common.factory.FileFactory;
import com.woowacourse.pickgit.common.request_builder.ServiceBuilder;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.parsing.FailFastProblemReporter;

public class Parameters {

    public static Builder builder(ServiceBuilder serviceBuilder) {
        return new Builder(serviceBuilder);
    }

    public static Builder builderWithInitParams(ServiceBuilder serviceBuilder) {
        Builder builder = new Builder(serviceBuilder);
        builder.init();
        return builder;
    }

    public static class Builder {

        private final ServiceBuilder serviceBuilder;
        private final Map<String, Object> formParams;
        private final List<File> multiparts;

        public Builder(ServiceBuilder serviceBuilder) {
            this.serviceBuilder = serviceBuilder;
            this.formParams = new HashMap<>();
            this.multiparts = new ArrayList<>();
        }

        public void init() {
            githubRepoUrl("https://github.com/sample");
            content("testContent");
            tags("java", "c++");
            images(FileFactory.getTestImage1File(), FileFactory.getTestImage2File());
        }

        public Builder githubRepoUrl(String githubRepoUrl) {
            this.formParams.put(GITHUB_REPO_URL, githubRepoUrl);
            return this;
        }

        public Builder content(String content) {
            this.formParams.put(CONTENT, content);
            return this;
        }

        public Builder tags(String... tags) {
            tags(List.of(tags));
            return this;
        }

        public Builder tags(List<String> tags) {
            this.formParams.put(TAGS, String.join(",", tags));

            return this;
        }

        public Builder images(File... file) {
            multiparts.clear();
            this.multiparts.addAll(List.of(file));
            return this;
        }

        public ExtractableResponse<Response> extract() {
            var specWithParams = serviceBuilder
                .getSpec()
                .formParams(formParams);

            for (File file : multiparts) {
                specWithParams = specWithParams.multiPart(IMAGES, file);
            }

            return serviceBuilder.responseExtract(specWithParams);
        }
    }
}
