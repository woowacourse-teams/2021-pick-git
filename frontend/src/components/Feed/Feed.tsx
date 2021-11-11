import axios from "axios";
import { Dispatch, SetStateAction, useContext, useEffect, useState } from "react";
import { InfiniteData, QueryKey } from "react-query";
import { useHistory } from "react-router-dom";
import { Post } from "../../@types";
import { NOT_FOUND_MESSAGE, SUCCESS_MESSAGE, WARNING_MESSAGE } from "../../constants/messages";
import { PAGE_URL } from "../../constants/urls";
import SnackBarContext from "../../contexts/SnackbarContext";
import UserContext from "../../contexts/UserContext";
import useModal from "../../hooks/common/useModal";
import useFeedMutation from "../../hooks/service/useFeedMutation";
import usePostEdit from "../../hooks/service/usePostEdit";
import { getAPIErrorMessage } from "../../utils/error";
import { getItemsFromPages } from "../../utils/infiniteData";
import ConfirmPortal from "../@layout/ConfirmPortal/ConfirmPortal";
import PageLoadingWithCover from "../@layout/PageLoadingWithCover/PageLoadingWithCover";
import InfiniteScrollContainer from "../@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import NotFound from "../@shared/NotFound/NotFound";
import PostItem from "../@shared/PostItem/PostItem";
import { PostItemWrapper, NotFoundCSS } from "./Feed.style";

interface Props {
  infinitePostsData: InfiniteData<Post[] | null>;
  onIntersect: () => void;
  setCurrentPostId?: Dispatch<SetStateAction<number>>;
  queryKeyList: QueryKey[];
  isFetching: boolean;
  notFoundMessage?: string | null;
}

const Feed = ({
  infinitePostsData,
  onIntersect,
  setCurrentPostId,
  queryKeyList,
  isFetching,
  notFoundMessage,
}: Props) => {
  const [selectedPostId, setSelectedPostId] = useState<Post["id"]>();
  const { pushSnackbarMessage } = useContext(SnackBarContext);
  const { addPostLike, deletePost, deletePostLike, isDeletePostLoading } = useFeedMutation(queryKeyList);
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

    setCurrentPostId?.(selectedPost.id);
    history.push({
      pathname: PAGE_URL.POST_DETAIL,
      search: `id=${postId}`,
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

  if (infinitePostsData.pages.length === 0 || infinitePostsData.pages[0]?.length === 0) {
    return <NotFound type="post" message={notFoundMessage ?? NOT_FOUND_MESSAGE.POSTS.DEFAULT} cssProp={NotFoundCSS} />;
  }

  return (
    <InfiniteScrollContainer isLoaderShown={isFetching} onIntersect={onIntersect}>
      {posts?.map((post) => (
        <PostItemWrapper id={`post${post.id}`} key={post.id}>
          <PostItem
            post={post}
            currentUserName={currentUsername}
            isLoggedIn={isLoggedIn}
            isEditable={currentUsername === post.authorName && isLoggedIn}
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
    </InfiniteScrollContainer>
  );
};

export default Feed;
