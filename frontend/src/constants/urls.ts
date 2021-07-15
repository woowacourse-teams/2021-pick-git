export const URL_PARAMS = {
  ME: "me",
};

export const PAGE_URL = {
  HOME: "/",
  LOGIN: "/login",
  ADD_POST: "/add-post",
  EDIT_POST: "/edit-post",
  SEARCH: "/search",
  PROFILE: "profile/:id",
  MY_PROFILE: `/profile/${URL_PARAMS.ME}`,
  USER_PROFILE: (userName: string) => `/profile/${userName}`,
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
  POSTS_LIKES: (postId: string) => `/posts/${postId}/likes`,
  POSTS_COMMENTS: (postId: string) => `/posts/${postId}/comments`,
};
