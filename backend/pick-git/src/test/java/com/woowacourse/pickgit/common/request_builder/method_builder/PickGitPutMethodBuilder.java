package com.woowacourse.pickgit.common.request_builder.method_builder;

import com.woowacourse.pickgit.common.request_builder.LoginBuilder;
import com.woowacourse.pickgit.common.request_builder.parameters.put.Put_LikesAddParameters;
import com.woowacourse.pickgit.common.request_builder.parameters.put.Put_PostModificationParameters;
import com.woowacourse.pickgit.common.request_builder.parameters.put.Put_ProfileContentUpdateParameters;
import com.woowacourse.pickgit.common.request_builder.parameters.put.Put_ProfileImageUpdateParameters;

public class PickGitPutMethodBuilder extends MethodBuilder{

    public PickGitPutMethodBuilder(String url, Object... params) {
        super(url, params);
    }

    public LoginBuilder<Put_PostModificationParameters> api_posts_postId() {
        return getPutLoginBuilder(Put_PostModificationParameters.class);
    }

    public LoginBuilder<Put_LikesAddParameters> api_posts_postId_likes() {
        return getPutLoginBuilder(Put_LikesAddParameters.class);
    }

    public LoginBuilder<Put_ProfileImageUpdateParameters> api_profiles_me_image() {
        return getPutLoginBuilder(Put_ProfileImageUpdateParameters.class);
    }

    public LoginBuilder<Put_ProfileContentUpdateParameters> api_profiles_me_description() {
        return getPutLoginBuilder(Put_ProfileContentUpdateParameters.class);
    }
}
