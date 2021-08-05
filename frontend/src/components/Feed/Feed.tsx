import { useContext, useEffect, useState } from "react";
import { Post } from "../../@types";
import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";
import PostItem from "../@shared/PostItem/PostItem";
import { Container, PostItemWrapper } from "./Feed.style";
import useBottomSlider from "../../services/hooks/@common/useBottomSlider";
import CommentSlider from "../CommentSlider/CommentSlider";
import useFeedMutation from "../../services/hooks/useFeedMutation";
import { FAILURE_MESSAGE, SUCCESS_MESSAGE, WARNING_MESSAGE } from "../../constants/messages";
import { getAPIErrorMessage } from "../../utils/error";
import { useHistory } from "react-router-dom";
import { PAGE_URL } from "../../constants/urls";
import usePostEdit from "../../services/hooks/usePostEdit";
import { InfiniteData, QueryKey } from "react-query";
import { getPostsFromPages } from "../../utils/feed";
import useMessageModal from "../../services/hooks/@common/useMessageModal";
import MessageModalPortal from "../@layout/MessageModalPortal/MessageModalPortal";

interface Props {
  infinitePostsData: InfiniteData<Post[] | null>;
  queryKey: QueryKey;
}

const Feed = ({ infinitePostsData, queryKey }: Props) => {
  const [selectedPostId, setSelectedPostId] = useState<Post["id"]>();
  const { pushSnackbarMessage } = useContext(SnackBarContext);
  const { addPostComment, addPostLike, deletePost, deletePostLike } = useFeedMutation(queryKey);
  const { setPostEditData } = usePostEdit();
  const { modalMessage, isModalShown, isCancelButtonShown, showConfirmModal, hideMessageModal } = useMessageModal();
  const { isBottomSliderShown, showBottomSlider, hideBottomSlider, removeSlideEventHandler, setSlideEventHandler } =
    useBottomSlider();
  const { isLoggedIn, currentUsername } = useContext(UserContext);
  const history = useHistory();

  const posts = getPostsFromPages(infinitePostsData.pages);

  const selectedPost = posts.find((post) => post.id === selectedPostId);

  useEffect(() => {
    setSlideEventHandler();
    return removeSlideEventHandler;
  }, []);

  const handlePostEdit = async (post: Post) => {
    setPostEditData({ content: post.content, postId: post.id, tags: post.tags });
    history.push(PAGE_URL.EDIT_POST_FIRST_STEP);
  };

  const handlePostDeleteButtonClick = (postId: Post["id"]) => {
    setSelectedPostId(postId);
    showConfirmModal(WARNING_MESSAGE.POST_DELETE);
  };

  const handlePostDelete = async () => {
    if (!selectedPostId) {
      return;
    }

    hideMessageModal();

    try {
      await deletePost(selectedPostId);
      pushSnackbarMessage(SUCCESS_MESSAGE.POST_DELETED);
    } catch (error) {
      pushSnackbarMessage(getAPIErrorMessage(error.response?.data.errorCode));
    }
  };

  const handlePostLike = async (postId: Post["id"]) => {
    const newPosts = [...posts];
    const targetPost = newPosts.find((post) => post.id === postId);

    if (!targetPost) {
      return;
    }

    if (targetPost.liked) {
      await deletePostLike(targetPost.id);
    } else {
      await addPostLike(targetPost.id);
    }
  };

  const handleCommentLike = (commentId: number) => {
    pushSnackbarMessage("아직 구현되지 않은 기능입니다.");
  };

  const handleCommentsClick = (postId: Post["id"]) => {
    showBottomSlider();
    setSelectedPostId(postId);
  };

  const handleCommentSliderClose = () => {
    hideBottomSlider();
  };

  const handleCommentSave = async (value: string) => {
    if (!selectedPostId) {
      return;
    }

    try {
      await addPostComment(selectedPostId, value);
    } catch (error) {
      pushSnackbarMessage(FAILURE_MESSAGE.COMMENT_SAVE_FAILED);
    }
  };

  return (
    <Container>
      {posts?.map((post) => (
        <PostItemWrapper id={`post${post.id}`} key={post.id}>
          <PostItem
            currentUserName={currentUsername}
            isLoggedIn={isLoggedIn}
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
            liked={post.liked}
            likeCount={post.likesCount}
            tags={post.tags}
            onPostDelete={() => handlePostDeleteButtonClick(post.id)}
            onPostEdit={() => handlePostEdit(post)}
            onPostLike={() => handlePostLike(post.id)}
            onMoreCommentClick={() => handleCommentsClick(post.id)}
            onCommentInputClick={() => handleCommentsClick(post.id)}
            onCommentLike={handleCommentLike}
          />
        </PostItemWrapper>
      ))}
      <CommentSlider
        onCommentSave={handleCommentSave}
        post={selectedPost}
        isSliderShown={isBottomSliderShown}
        onSliderClose={handleCommentSliderClose}
      />
      {isModalShown && isCancelButtonShown && (
        <MessageModalPortal
          heading={modalMessage}
          onConfirm={handlePostDelete}
          onClose={hideMessageModal}
          onCancel={hideMessageModal}
        />
      )}
    </Container>
  );
};

export default Feed;
