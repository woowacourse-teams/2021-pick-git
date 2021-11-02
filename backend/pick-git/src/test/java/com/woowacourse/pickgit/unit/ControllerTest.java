package com.woowacourse.pickgit.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.presentation.OAuthController;
import com.woowacourse.pickgit.comment.application.CommentService;
import com.woowacourse.pickgit.comment.presentation.CommentController;
import com.woowacourse.pickgit.portfolio.application.PortfolioService;
import com.woowacourse.pickgit.portfolio.presentation.PortfolioController;
import com.woowacourse.pickgit.post.application.PostFeedService;
import com.woowacourse.pickgit.post.application.PostService;
import com.woowacourse.pickgit.post.presentation.PostController;
import com.woowacourse.pickgit.post.presentation.PostFeedController;
import com.woowacourse.pickgit.post.presentation.postfeed.AllFeedType;
import com.woowacourse.pickgit.post.presentation.postfeed.FollowingsFeedType;
import com.woowacourse.pickgit.tag.application.TagService;
import com.woowacourse.pickgit.tag.presentation.TagController;
import com.woowacourse.pickgit.user.application.UserService;
import com.woowacourse.pickgit.user.presentation.UserController;
import com.woowacourse.pickgit.user.presentation.UserSearchController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureRestDocs
@WebMvcTest({
    UserController.class,
    UserSearchController.class,
    TagController.class,
    PostFeedController.class,
    PostController.class,
    CommentController.class,
    OAuthController.class,
    PortfolioController.class,
    AllFeedType.class,
    FollowingsFeedType.class
})
@ActiveProfiles("test")
public abstract class ControllerTest {

    @MockBean
    protected CommentService commentService;

    @MockBean
    protected OAuthService oAuthService;

    @MockBean
    protected PostService postService;

    @MockBean
    protected PostFeedService postFeedService;

    @MockBean
    protected UserService userService;

    @MockBean
    protected TagService tagService;

    @MockBean
    protected PortfolioService portfolioService;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;
}

