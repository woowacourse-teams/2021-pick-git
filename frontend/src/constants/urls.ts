export const PAGE_URL = {
  HOME: "/",
  LOGIN: "/login",
  ADD_POST: "/add-post",
  EDIT_POST: "/edit-post",
  SEARCH: "/search",
  MY_PROFILE: "/profile/me",
  USER_PROFILE: (userName: string) => `/profile/${userName}`,
  POSTS_WITH_TAG: (tag: string) => `/posts/${tag}`,
};

export const API_URL = {
  SELF_PROFILE: "/profiles/me",
  AFTER_LOGIN: (code: string) => `afterlogin?code=${code}`,
  AUTH: {
    GITHUB: "/authorization/github",
  },
  POSTS: "/posts",
  POSTS_LIKES: (postId: string) => `/posts/${postId}/likes`,
  POSTS_COMMENTS: (postId: string) => `/posts/${postId}/comments`,
};
