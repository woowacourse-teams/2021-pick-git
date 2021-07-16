export const URL_PARAMS = {
  ME: "me",
  USER: "user",
};

export const PAGE_URL = {
  HOME: "/",
  LOGIN: "/login",
  ADD_POST: "/add-post",
  EDIT_POST: "/edit-post",
  SEARCH: "/search",
  PROFILE: "/profile/:userType/:userName",
  MY_PROFILE: `/profile/${URL_PARAMS.ME}`,
  USER_PROFILE: (userName: string) => `/profile/${URL_PARAMS.USER}/${userName}`,
  POSTS_WITH_TAG: (tag: string) => `/posts/${tag}`,
};

export const API_URL = {
  AFTER_LOGIN: (code: string) => `afterlogin?code=${code}`,
  AUTH: {
    GITHUB: "/authorization/github",
  },
  SELF_PROFILE: "/profiles/me",
  USER_PROFILE: (userName: string) => `/profiles/${userName}`,
  POSTS: "/posts",
  MY_POSTS: "/posts/me",
  USER_POSTS: (userName: string) => `/posts/${userName}`,
  POSTS_LIKES: (postId: string) => `/posts/${postId}/likes`,
  POSTS_COMMENTS: (postId: string) => `/posts/${postId}/comments`,
};
