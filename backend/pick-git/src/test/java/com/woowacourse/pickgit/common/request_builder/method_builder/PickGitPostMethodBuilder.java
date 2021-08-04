package com.woowacourse.pickgit.common.request_builder.method_builder;

import com.woowacourse.pickgit.common.request_builder.LoginBuilder;
import com.woowacourse.pickgit.common.request_builder.parameters.post.Post_CommentWriteParameters;
import com.woowacourse.pickgit.common.request_builder.parameters.post.Post_FollowingsParameters;
import com.woowacourse.pickgit.common.request_builder.parameters.post.Post_PostWriteParameters;

public class PickGitPostMethodBuilder extends MethodBuilder{

    public PickGitPostMethodBuilder(String url, Object... params) {
        super(url, params);
    }

    public LoginBuilder<Post_PostWriteParameters> api_posts() {
        return getPostLoginBuilder(Post_PostWriteParameters.class);
    }

    public LoginBuilder<Post_CommentWriteParameters> api_posts_postId_comments() {
        return getPostLoginBuilder(Post_CommentWriteParameters.class);
    }

    public LoginBuilder<Post_FollowingsParameters> api_profiles_userName_followings() {
        return getPostLoginBuilder(Post_FollowingsParameters.class);
    }
}
