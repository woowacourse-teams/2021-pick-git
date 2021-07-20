import { useContext } from "react";
import { Post } from "../../@types";
import { LIMIT } from "../../constants/limits";
import { FAILURE_MESSAGE } from "../../constants/message";
import SnackBarContext from "../../contexts/SnackbarContext";
import useFeed from "../../services/hooks/useFeed";
import PostItem from "../@shared/PostItem/PostItem";
import { Container, PostItemWrapper } from "./Feed.style";

interface Props {
  posts: Post[];
}

const Feed = ({ posts }: Props) => {
  const { pushMessage } = useContext(SnackBarContext);
  const { commentValue, setCommentValue, deletePostLike, addPostLike, setPosts, addComment } = useFeed();

  const handleCommentValueChange: React.ChangeEventHandler<HTMLTextAreaElement> = ({ target: { value } }) => {
    if (value.length > LIMIT.COMMENT_LENGTH) {
      pushMessage(FAILURE_MESSAGE.COMMENT_CONTENT_MAX_LENGTH_EXCEEDED);
      return;
    }

    setCommentValue(value);
  };

  const handleCommentValueSave = (postId: Post["postId"]) => {
    addComment(postId, commentValue);
  };

  const handleCommentLike = (commentId: string) => {
    pushMessage("아직 구현되지 않은 기능입니다.");
  };

  const handlePostLike = (postId: string) => {
    const newPosts = [...posts];
    const targetPost = newPosts.find((post) => post.postId === postId);

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
        <PostItemWrapper key={post.postId}>
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
            isEditable={true}
            isLiked={post.isLiked}
            likeCount={post.likesCount}
            tags={post.tags}
            commentValue={commentValue}
            onCommentValueChange={handleCommentValueChange}
            onCommentValueSave={() => handleCommentValueSave(post.postId)}
            onCommentLike={handleCommentLike}
            onPostLike={() => handlePostLike(post.postId)}
          />
        </PostItemWrapper>
      ))}
    </Container>
  );
};

export default Feed;
