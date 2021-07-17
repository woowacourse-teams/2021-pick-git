import { STEPS } from "./steps";

export const URL_PARAMS = {
  ME: "me",
  USER: "user",
};

export const PAGE_URL = {
  HOME: "/",
  HOME_FEED: "/posts",
  TAG_FEED: (tag: string) => `/posts?tag=${tag}`,
  USER_FEED: (userName: string) => `/posts?userName=${userName}`,
  LOGIN: "/login",
  AUTH_PROCESSING: "/auth",
  ADD_POST: `/add-post`,
  ADD_POST_FIRST_STEP: `/add-post/${STEPS[0]}`,
  EDIT_POST: "/edit-post",
  SEARCH: "/search",
  PROFILE: "/profile",
  MY_PROFILE: "/profile/me",
  USER_PROFILE: (userName: string) => `/profile?userName=${userName}`,
  POSTS_WITH_TAG: (tag: string) => `/posts/${tag}`,
};

export const API_URL = {
  AUTH: {
    GITHUB: "/authorization/github",
  },
  SELF_PROFILE: "/profiles/me",
  ADD_POSTS: "/posts",
  USER_PROFILE: (userName: string) => `/profiles/${userName}`,
  USER_PROFILE_FOLLOW: (userName: string) => `/profiles/${userName}/followings`,
  POSTS: (page: number, limit: number) => `/posts?page=${page}&limit=${limit}`,
  MY_POSTS: (page: number, limit: number) => `/posts/me?page=${page}&limit=${limit}`,
  USER_POSTS: (userName: string, page: number, limit: number) => `/posts/${userName}?page=${page}&limit=${limit}`,
  AFTER_LOGIN: (code: string) => `afterlogin?code=${code}`,
  POSTS_LIKES: (postId: string) => `/posts/${postId}/likes`,
  POSTS_COMMENTS: (postId: string) => `/posts/${postId}/comments`,
  GITHUB_REPOSITORIES: (userName: string) => `/github/${userName}/repositories`,
  GITHUB_TAGS: (userName: string, repositoryName: string) =>
    `/github/${userName}/repositories/${repositoryName}/tags/languages`,
};

export const GITHUB_URL = {
  REPOSITORY: (userName: string, repositoryName: string) => `https://github.com/${userName}/${repositoryName}`,
};
