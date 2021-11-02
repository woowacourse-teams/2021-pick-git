import { IconType } from "../components/@shared/SVGIcon/SVGIcon";
import { clientErrorCodeName, httpErrorStatus, httpErrorStatusName } from "../constants/error";
import { API_ERROR_MESSAGE, CLIENT_ERROR_MESSAGE } from "../constants/messages";

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

export interface UserItem {
  imageUrl: string;
  username: string;
  following: boolean | null;
}

export interface SearchResultTag {}

export interface CommentData {
  id: number;
  profileImageUrl: string;
  authorName: string;
  content: string;
  liked: boolean;
}

export interface CommentAddData {
  postId: Post["id"];
  commentContent: CommentData["content"];
}

export interface CommentDeleteData {
  postId: Post["id"];
  commentId: CommentData["id"];
}

export interface Post {
  id: number;
  imageUrls: string[];
  githubRepoUrl: string;
  content: string;
  authorName: string;
  profileImageUrl: string;
  likesCount: number;
  tags: string[];
  createdAt: string;
  updatedAt: string;
  comments: CommentData[];
  liked: boolean;
}

export interface PostUploadData {
  files: File[];
  githubRepositoryName: string;
  tags: string[];
  content: string;
}

export interface PostEditData {
  postId: Post["id"];
  tags: string[];
  content: string;
}

export type FeedFilterOption = "followings" | "all";

export interface GithubStats {
  starsCount: number;
  commitsCount: number;
  prsCount: number;
  issuesCount: number;
  reposCount: number;
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
  hash: string;
};

export type TabIndicatorKind = "line" | "pill";

export type APIErrorCode = keyof typeof API_ERROR_MESSAGE;

export type ErrorResponse = {
  errorCode: APIErrorCode;
};

export type HTTPErrorStatus = keyof typeof httpErrorStatus;

export type HTTPErrorStatusName = typeof httpErrorStatusName[number];

export type HTTPErrorHandler = {
  [V in HTTPErrorStatusName]?: () => void;
};

export type ClientErrorCode = keyof typeof CLIENT_ERROR_MESSAGE;

export type ClientErrorCodeName = typeof clientErrorCodeName[number];

export type ClientErrorHandler = {
  [V in ClientErrorCodeName]?: () => void;
};

export type PortfolioContact = {
  id: number | null;
  category: string;
  value: string;
};

export type PortfolioIcon =
  | "GithubLineIcon"
  | "EmailIcon"
  | "BlogIcon"
  | "WebsiteIcon"
  | "PhoneIcon"
  | "LocationLineIcon"
  | "CompanyLineIcon"
  | "LinkIcon";

export type PortfolioIntro = {
  name: string;
  description: string;
  profileImageUrl: string;
  isProfileShown: boolean;
};

export type PortfolioProject = {
  id: number | string;
  name: string;
  startDate: string;
  endDate: string;
  type: "team" | "personal";
  imageUrl: string;
  content: string;
  tags: string[];
};

export type IdProcessedPortfolioProject = {
  id: number | null;
  name: string;
  startDate: string;
  endDate: string;
  type: "team" | "personal";
  imageUrl: string;
  content: string;
  tags: string[];
};

export type PortfolioProjectUploadData = {
  id: number | null;
  name: string;
  startDate: string;
  endDate: string;
  type: "team" | "personal";
  imageUrl: string;
  content: string;
  tags: string[];
};

export type PortfolioSectionItem = {
  id: string | number;
  category: string;
  descriptions: {
    id: string | number;
    value: string;
  }[];
};

export type IdProcessedPortfolioSectionItem = {
  id: number | null;
  category: string;
  descriptions: {
    id: number | null;
    value: string;
  }[];
};

export type PortfolioSection = {
  id: string | number;
  name: string;
  items: PortfolioSectionItem[];
};

export type IdProcessedPortfolioSection = {
  id: number | null;
  name: string;
  items: IdProcessedPortfolioSectionItem[];
};

export type Portfolio = {
  id: number | null;
  intro: PortfolioIntro;
  projects: PortfolioProject[];
  sections: PortfolioSection[];
};

export type PortfolioData = {
  id: number | null;
  name: string;
  profileImageShown: boolean;
  profileImageUrl: string;
  introduction: string;
  updatedAt?: string;
  createdAt?: string;
  contacts: PortfolioContact[];
  projects: PortfolioProject[];
  sections: PortfolioSection[];
};

export type PortfolioUploadData = {
  id: number | null;
  name: string;
  profileImageShown: boolean;
  profileImageUrl: string;
  introduction: string;
  updatedAt?: string;
  createdAt?: string;
  contacts: PortfolioContact[];
  projects: IdProcessedPortfolioProject[];
  sections: IdProcessedPortfolioSection[];
};

export type PortfolioSectionType = "project" | "custom";

export type PortfolioSectionList = string[];

export interface ChildFabItem {
  color?: string;
  backgroundColor?: string;
  icon: IconType;
  text?: string;
  onClick: () => void;
}

export type CircleButtonItem = {
  icon: IconType;
  backgroundColor?: string;
  onClick?: () => void;
};
