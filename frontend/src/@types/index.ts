import { httpErrorStatus, httpErrorStatusName } from "../constants/error";
import { API_ERROR_MESSAGE } from "../constants/messages";

export interface ProfileData {
  name: string;
  imageUrl: string;
  description: string;
  followerCount: number;
  followingCount: number;
  postCount: number;
  githubUrl: string;
  company: string;
  location: string;
  website: string;
  twitter: string;
  following: boolean | null;
}

export interface MutateResponseFollow {
  followerCount: number;
  following: boolean;
}

export interface SearchResultUser {
  imageUrl: string;
  username: string;
  following: boolean | null;
}

export interface SearchResultTag {}

export interface SearchResult {
  users: SearchResultUser[];
  tags: SearchResultTag[];
}

export interface CommentData {
  id: number;
  profileImageUrl: string;
  authorName: string;
  content: string;
  isLiked: boolean;
}

export interface CommentAddData {
  postId: Post["id"];
  commentContent: CommentData["content"];
}

export interface Post {
  id: number;
  imageUrls: string[];
  githubRepoUrl: string;
  content: string;
  authorName: string;
  profileImageUrl: string;
  likesCount: number;
  tags: [];
  createdAt: string;
  updatedAt: string;
  comments: CommentData[];
  isLiked: boolean;
}

export interface PostUploadData {
  files: File[];
  githubRepositoryName: string;
  tags: string[];
  content: string;
}

export interface GithubStats {
  stars: string;
  commits: string;
  prs: string;
  issues: string;
  contributes: string;
}

export interface GithubRepository {
  url: string;
  name: string;
}

export interface TabItem {
  name: string;
  onTabChange: () => void;
}

export type Tags = string[];

export type Step = {
  title: string;
  path: string;
};

export type TabIndicatorKind = "line" | "pill";

export type ErrorCode = keyof typeof API_ERROR_MESSAGE;

export type ErrorResponse = {
  errorCode: ErrorCode;
};

export type HTTPErrorStatus = keyof typeof httpErrorStatus;

export type HTTPErrorStatusName = typeof httpErrorStatusName[number];

export type HTTPErrorHandler = {
  [V in HTTPErrorStatusName]?: () => void;
};
