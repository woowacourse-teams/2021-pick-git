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

export type ErrorResponse = {
  errorCode:
    | "A0001"
    | "A0002"
    | "F0001"
    | "F0002"
    | "F0003"
    | "U0001"
    | "U0002"
    | "U0003"
    | "U0004"
    | "V0001"
    | "P0001"
    | "P0002"
    | "S0001";
};

export type TabIndicatorKind = "line" | "pill";
