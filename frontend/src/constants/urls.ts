import { STEPS } from "./steps";

export const URL_PARAMS = {
  ME: "me",
  USER: "user",
};

export const PAGE_URL = {
  HOME: "/",
  HOME_FEED: "/posts",
  TAG_FEED: (tag: string) => `/posts?tag=${tag}`,
  USER_FEED: (username: string) => `/posts?username=${username}`,
  LOGIN: "/login",
  AUTH_PROCESSING: "/auth",
  ADD_POST: "/add-post",
  ADD_POST_FIRST_STEP: `/add-post/${STEPS[0]}`,
  EDIT_POST: "/edit-post",
  SEARCH: "/search",
  PROFILE: "/profile",
  MY_PROFILE: "/profile/me",
  USER_PROFILE: (username: string) => `/profile?username=${username}`,
  POSTS_WITH_TAG: (tag: string) => `/posts/${tag}`,
};

export const API_URL = {
  AUTH: {
    GITHUB: "/authorization/github",
  },
  SELF_PROFILE: "/profiles/me",
  ADD_POSTS: "/posts",
  USER_PROFILE: (username: string) => `/profiles/${username}`,
  USER_PROFILE_FOLLOW: (username: string) => `/profiles/${username}/followings`,
  POSTS: (page: number, limit: number) => `/posts?page=${page}&limit=${limit}`,
  MY_POSTS: (page: number, limit: number) => `/posts/me?page=${page}&limit=${limit}`,
  USER_POSTS: (username: string, page: number, limit: number) => `/posts/${username}?page=${page}&limit=${limit}`,
  AFTER_LOGIN: (code: string) => `afterlogin?code=${code}`,
  POSTS_LIKES: (postId: string) => `/posts/${postId}/likes`,
  POSTS_COMMENTS: (postId: string) => `/posts/${postId}/comments`,
  GITHUB_REPOSITORIES: (userName: string) => `/github/${userName}/repositories`,
  GITHUB_TAGS: (repositoryName: string) => `/github/repositories/${repositoryName}/tags/languages`,
};

export const GITHUB_URL = {
  REPOSITORY: (username: string, repositoryName: string) => `https://github.com/${username}/${repositoryName}`,
};
