export interface Profile {
  name: string;
  image: string;
  description: string;
  followerCount: number;
  followingCount: number;
  postCount: number;
  githubUrl: string;
  company: string;
  location: string;
  website: string;
  twitter: string;
  following?: boolean;
}

export interface CommentData {
  commentId: string;
  authorName: string;
  content: string;
  isLiked: boolean;
}

export interface CommentAddData {
  postId: Post["postId"];
  commentContent: CommentData["content"];
}

export interface Post {
  postId: string;
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
