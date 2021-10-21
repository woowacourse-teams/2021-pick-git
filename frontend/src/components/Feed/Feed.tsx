import { useContext, useEffect, useState } from "react";

import { Post } from "../../@types";
import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";
import PostItem from "../@shared/PostItem/PostItem";
import { Container, PostItemWrapper } from "./Feed.style";
import useFeedMutation from "../../hooks/service/useFeedMutation";
import { SUCCESS_MESSAGE, WARNING_MESSAGE } from "../../constants/messages";
import { getAPIErrorMessage } from "../../utils/error";
import { useHistory } from "react-router-dom";
import { PAGE_URL } from "../../constants/urls";
import usePostEdit from "../../hooks/service/usePostEdit";
import { InfiniteData, QueryKey } from "react-query";
import { getItemsFromPages } from "../../utils/infiniteData";
import ConfirmPortal from "../@layout/ConfirmPortal/ConfirmPortal";
import PageLoadingWithCover from "../@layout/PageLoadingWithCover/PageLoadingWithCover";
import useModal from "../../hooks/common/useModal";
import axios from "axios";

interface Props {
  infinitePostsData: InfiniteData<Post[] | null>;
  queryKey: QueryKey;
  isFetching: boolean;
}

const Feed = ({ infinitePostsData, queryKey, isFetching }: Props) => {
  const [selectedPostId, setSelectedPostId] = useState<Post["id"]>();
  const { pushSnackbarMessage } = useContext(SnackBarContext);
  const { addPostLike, deletePost, deletePostLike, isDeletePostLoading } = useFeedMutation(queryKey);
  const [posts, setPosts] = useState<Post[]>([]);
  const { setPostEditData } = usePostEdit();
  const {
    isModalShown: isConfirmShown,
    modalMessage: confirmMessage,
    showModal: showConfirm,
    hideModal: hideConfirm,
  } = useModal();
  const { isLoggedIn, currentUsername } = useContext(UserContext);
  const history = useHistory();

  const handlePostEdit = async (post: Post) => {
    setPostEditData({ content: post.content, postId: post.id, tags: post.tags });
    history.push(PAGE_URL.EDIT_POST_FIRST_STEP);
  };

  const handlePostDeleteButtonClick = (postId: Post["id"]) => {
    setSelectedPostId(postId);
    showConfirm(WARNING_MESSAGE.POST_DELETE);
  };

  const handlePostDelete = async () => {
    if (!selectedPostId) {
      return;
    }

    hideConfirm();

    try {
      await deletePost(selectedPostId);
      pushSnackbarMessage(SUCCESS_MESSAGE.POST_DELETED);
    } catch (error) {
      if (!axios.isAxiosError(error)) {
        return;
      }

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

  const handleCommentsClick = (postId: Post["id"]) => {
    const selectedPost = posts.find((post) => post.id === postId);
    if (!selectedPost) {
      return;
    }

    history.push({
      pathname: PAGE_URL.POST_COMMENTS,
      state: selectedPost,
    });
  };

  const handlePostLikeCountClick = (postId: Post["id"]) => {
    const selectedPost = posts.find((post) => post.id === postId);
    if (!selectedPost) {
      return;
    }

    if (selectedPost.likesCount === 0) {
      pushSnackbarMessage(WARNING_MESSAGE.NO_ONE_LIKE_POST);
      return;
    }

    history.push({
      pathname: PAGE_URL.POST_LIKE_PEOPLE,
      state: selectedPost.id,
    });
  };

  useEffect(() => {
    if (isFetching) {
      return;
    }

    setPosts(getItemsFromPages<Post>(infinitePostsData.pages) ?? []);
  }, [infinitePostsData, isFetching]);

  if (!infinitePostsData.pages) {
    return <div>게시물이 존재하지 않습니다.</div>;
  }

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
            handlePostLikeCountClick={() => handlePostLikeCountClick(post.id)}
            onMoreCommentClick={() => handleCommentsClick(post.id)}
            onCommentInputClick={() => handleCommentsClick(post.id)}
          />
          {isDeletePostLoading && <PageLoadingWithCover description="삭제중" />}
        </PostItemWrapper>
      ))}
      {isConfirmShown && <ConfirmPortal heading={confirmMessage} onConfirm={handlePostDelete} onCancel={hideConfirm} />}
    </Container>
  );
};

export default Feed;
