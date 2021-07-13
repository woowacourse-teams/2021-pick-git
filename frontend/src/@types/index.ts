export interface Profile {
  userImageUrl: string;
  userName: string;
  followCount: number;
  followerCount: number;
  postCount: number;
  tmi: string;
  githubUrl: string;
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
