import { forwardRef, useContext } from "react";
import { Post } from "../../@types";
import { LIMIT } from "../../constants/limits";
import { FAILURE_MESSAGE } from "../../constants/messages";
import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";
import useFeed from "../../services/hooks/useFeed";
import PostItem from "../@shared/PostItem/PostItem";
import CommentInputPortal from "../CommentInputPortal/CommentInputPortal";
import { Container, PostItemWrapper } from "./HomeFeed.style";

interface Props {
  posts: Post[];
}

const HomeFeed = ({ posts }: Props) => {
  const { pushSnackbarMessage } = useContext(SnackBarContext);
  const { commentValue, setCommentValue, deletePostLike, addPostLike, setPosts, addComment } = useFeed();
  const { isLoggedIn, currentUsername } = useContext(UserContext);

  const handleCommentValueChange: React.ChangeEventHandler<HTMLTextAreaElement> = ({ target: { value } }) => {
    if (value.length > LIMIT.COMMENT_LENGTH) {
      pushSnackbarMessage(FAILURE_MESSAGE.COMMENT_CONTENT_MAX_LENGTH_EXCEEDED);
      return;
    }

    setCommentValue(value);
  };

  const handleCommentValueSave = (postId: Post["id"]) => {
    addComment(postId, commentValue);
  };

  const handleCommentLike = (commentId: string) => {
    pushSnackbarMessage("아직 구현되지 않은 기능입니다.");
  };

  const handlePostLike = (postId: string) => {
    const newPosts = [...posts];
    const targetPost = newPosts.find((post) => post.id === postId);

    if (!targetPost) {
      return;
    }

    if (targetPost.isLiked) {
      deletePostLike(targetPost);
      targetPost.isLiked = false;
      setPosts(newPosts);
      return;
    }

    if (!targetPost.isLiked) {
      addPostLike(targetPost);
      targetPost.isLiked = true;
      setPosts(newPosts);
    }
  };

  return (
    <Container>
      {posts?.map((post) => (
        <PostItemWrapper id={`post${post.id}`} key={post.id}>
          <PostItem
            authorName={post.authorName}
            authorGithubUrl={post.githubRepoUrl}
            authorImageUrl={post.profileImageUrl}
            imageUrls={post.imageUrls}
            commenterImageUrl={
              "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80"
            }
            createdAt={post.createdAt}
            comments={post.comments}
            content={post.content}
            isEditable={currentUsername === post.authorName && isLoggedIn}
            isLiked={post.isLiked}
            likeCount={post.likesCount}
            tags={post.tags}
            commentValue={commentValue}
            onCommentValueChange={handleCommentValueChange}
            onCommentValueSave={() => handleCommentValueSave(post.id)}
            onCommentLike={handleCommentLike}
            onPostLike={() => handlePostLike(post.id)}
          />
        </PostItemWrapper>
      ))}
      <CommentInputPortal />
    </Container>
  );
};

export default HomeFeed;
