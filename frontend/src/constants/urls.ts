import { FeedFilterOption } from "../@types";
import { POST_ADD_STEPS, POST_EDIT_STEPS } from "./steps";

const isProduction = process.env.NODE_ENV === "production";

export const URL_PARAMS = {
  ME: "me",
  USER: "user",
};

export const PAGE_URL = {
  HOME: "/",
  HOME_FEED: "/posts",
  USER_FEED_BASE: "/posts-user",
  POST_DETAIL: "/post",
  POST: (postId: number) => `/post?id=${postId}`,
  POST_SHARE: (postId: number) =>
    `${isProduction ? "https://pick-git.com" : "http://dev.pick-git.com"}/post?id=${postId}`,
  USER_FEED: (username: string) => `/posts-user?username=${username}`,
  SEARCH_RESULT_FEED_BASE: "/search-posts",
  SEARCH_RESULT_FEED: (type: string, keyword: string) => `/search-posts?type=${type}&keyword=${keyword}`,
  LOGIN: "/login",
  AUTH_PROCESSING: "/auth",
  ADD_POST: "/add-post",
  ADD_POST_FIRST_STEP: `/add-post#${POST_ADD_STEPS[0].hash}`,
  EDIT_POST: "/edit-post",
  EDIT_POST_FIRST_STEP: `/edit-post#${POST_EDIT_STEPS[0].hash}`,
  SEARCH: "/search",
  SEARCH_POST_BY_TAG: (tag: string) => `/search?type=tags&keyword=${tag}`,
  PROFILE: "/profile",
  MY_PROFILE: "/my-profile",
  POST_LIKE_PEOPLE: "/post-like-people",
  TAG_FEED: (tag: string) => `/posts/tag?tag=${tag}`,
  USER_PROFILE: (username: string) => `/profile?username=${username}`,
  FOLLOWINGS_BASE: "/followings",
  FOLLOWERS_BASE: "/followers",
  FOLLOWINGS: (username: string) => `/followings?username=${username}`,
  FOLLOWERS: (username: string) => `/followers?username=${username}`,
  PORTFOLIO: "/portfolio",
  USER_PORTFOLIO_SHARE: (username: string) =>
    `${isProduction ? "https://pick-git.com" : "http://dev.pick-git.com"}/portfolio?username=${username}`,
  MY_PORTFOLIO: "/my-portfolio",
};

export const API_URL = {
  AUTH: {
    GITHUB: "/authorization/github",
  },
  SELF_PROFILE: "/profiles/me",
  SELF_PROFILE_IMAGE: "/profiles/me/image",
  SELF_PROFILE_DESCRIPTION: "/profiles/me/description",
  ADD_POST: "/posts",
  EDIT_POST: (postId: number) => `/posts/${postId}`,
  DELETE_POST: (postId: number) => `/posts/${postId}`,
  PORTFOLIO: "/portfolios",
  USER_PORTFOLIO: (username: string) => `/portfolios/${username}`,
  GITHUB_REPOSITORIES: (keyword: string, page: number, limit: number) =>
    keyword === ""
      ? `/github/repositories?&page=${page}&limit=${limit}`
      : `/github/search/repositories?keyword=${keyword}&page=${page}&limit=${limit}`,
  USER_PROFILE: (username: string) => `/profiles/${username}`,
  USER_PROFILE_FOLLOW: (username: string, githubFollowing: boolean) =>
    `/profiles/${username}/followings?githubFollowing=${githubFollowing}`,
  USER_PROFILE_UNFOLLOW: (username: string, githubUnfollowing: boolean) =>
    `/profiles/${username}/followings?githubUnfollowing=${githubUnfollowing}`,
  USER_PROFILE_FOLLOWINGS: (username: string, pageParam: number, limit: number) =>
    `/profiles/${username}/followings?page=${pageParam}&limit=${limit}`,
  USER_PROFILE_FOLLOWERS: (username: string, pageParam: number, limit: number) =>
    `/profiles/${username}/followers?page=${pageParam}&limit=${limit}`,
  MY_POSTS: (page: number, limit: number) => `/posts/me?page=${page}&limit=${limit}`,
  USER_POSTS: (username: string, page: number, limit: number) => `/posts/${username}?page=${page}&limit=${limit}`,
  SEARCH_USER: (keyword: string, page: number, limit: number) =>
    `/search/users?keyword=${keyword}&page=${page}&limit=${limit}`,
  SEARCH_POST: (type: string, keyword: string, page: number, limit: number) =>
    `/search/posts?type=${type}&keyword=${keyword}&page=${page}&limit=${limit}`,
  AFTER_LOGIN: (code: string) => `afterlogin?code=${code}`,
  POST: (postId: number) => `/posts?id=${postId}`,
  POSTS: (page: number, limit: number, type?: FeedFilterOption) =>
    `/posts?page=${page}&limit=${limit}` + (type !== undefined ? `&type=${type}` : ""),
  POST_LIKE_PEOPLE: (postId: number) => `/posts/${postId}/likes`,
  POST_LIKES: (postId: number) => `/posts/${postId}/likes`,
  POST_COMMENT: (postId: number, commentId?: number) => `/posts/${postId}/comments${commentId ? `/${commentId}` : ""}`,
  POST_COMMENTS: (postId: number, page: number, limit: number) =>
    `/posts/${postId}/comments?page=${page}&limit=${limit}`,
  GITHUB_STATS: (username: string) => `/profiles/${username}/contributions`,
  GITHUB_TAGS: (repositoryName: string) => `/github/repositories/${repositoryName}/tags/languages`,
};

export const GITHUB_URL = {
  REPOSITORY: (username: string, repositoryName: string) => `https://github.com/${username}/${repositoryName}`,
};
