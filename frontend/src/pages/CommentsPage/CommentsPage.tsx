import { useRef, useState } from "react";
import { useHistory, useLocation } from "react-router-dom";

import MessageModalPortal from "../../components/@layout/MessageModalPortal/MessageModalPortal";
import PageLoading from "../../components/@layout/PageLoading/PageLoading";
import PageError from "../../components/@shared/PageError/PageError";
import Avatar from "../../components/@shared/Avatar/Avatar";
import SVGIcon from "../../components/@shared/SVGIcon/SVGIcon";
import Chip from "../../components/@shared/Chip/Chip";
import ImageSlider from "../../components/@shared/ImageSlider/ImageSlider";
import InfiniteScrollContainer from "../../components/@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import Tabs from "../../components/@shared/Tabs/Tabs";

import { FAILURE_MESSAGE, WARNING_MESSAGE } from "../../constants/messages";
import { COMMENT_SLIDE_STEPS } from "../../constants/steps";
import { PAGE_URL } from "../../constants/urls";

import useMessageModal from "../../hooks/common/useMessageModal";
import useSnackbar from "../../hooks/common/useSnackbar";
import useAuth from "../../hooks/common/useAuth";
import useComments from "../../hooks/service/useComments";

import { getItemsFromPages } from "../../utils/infiniteData";
import { getTextElementsWithWithBr } from "../../utils/text";

import {
  CloseLinkButton,
  CommentContentWrapper,
  CommentList,
  CommentListItem,
  CommentLoadingWrapper,
  CommentText,
  CommentTextArea,
  CommentTextAreaWrapper,
  Container,
  DeleteIconWrapper,
  GoBackLinkButton,
  HorizontalSlider,
  HorizontalSliderItemWrapper,
  HorizontalSliderWrapper,
  PostContent,
  PostContentAuthorLink,
  SendIconWrapper,
  SliderHeader,
  TabsWrapper,
  TagItemLinkButton,
  TagListWrapper,
} from "./CommentsPage.style";

import type { CommentData, Post, TabItem } from "../../@types";

const CommentsPage = () => {
  const commentTextAreaRef = useRef<HTMLTextAreaElement>(null);
  const containerRef = useRef<HTMLDivElement>(null);
  const [selectedCommentId, setSelectedCommentId] = useState<CommentData["id"]>(0);
  const [isPostShown, setIsPostShown] = useState(true);
  const [stepIndex, setStepIndex] = useState(0);
  const { state: selectedPost } = useLocation<Post>();
  const history = useHistory();

  const { pushSnackbarMessage } = useSnackbar();
  const { currentUsername, isLoggedIn } = useAuth();
  const { modalMessage, isModalShown, isCancelButtonShown, showConfirmModal, hideMessageModal } = useMessageModal();

  const {
    infiniteCommentsData,
    isFetching,
    isError,
    isLoading,
    isAddCommentLoading,
    isDeleteCommentLoading,
    getNextComments,
    addPostComment,
    deletePostComment,
  } = useComments(selectedPost.id);

  const comments = getItemsFromPages<CommentData>(infiniteCommentsData?.pages);

  const tabItems: TabItem[] = [
    {
      name: "사진/동영상",
      onTabChange: () => {
        setStepIndex(0);
      },
    },
    {
      name: "작성글",
      onTabChange: () => {
        setStepIndex(1);
      },
    },
    {
      name: "태그",
      onTabChange: () => {
        setStepIndex(2);
      },
    },
  ];

  const handleGoBack = () => {
    history.goBack();
  };

  const handleTogglePost = () => {
    setIsPostShown(!isPostShown);
  };

  const handleCommentDeleteClick = (commentId: CommentData["id"]) => {
    setSelectedCommentId(commentId);
    showConfirmModal(WARNING_MESSAGE.COMMENT_DELETE);
  };

  const handleCommentDelete = async () => {
    hideMessageModal();
    await deletePostComment(selectedPost.id, selectedCommentId);
  };

  const handleCommentSave = async () => {
    if (!commentTextAreaRef.current || !containerRef.current) {
      return;
    }

    const newComment = commentTextAreaRef.current.value;

    commentTextAreaRef.current.value = "";
    window.scroll({
      top: window.outerHeight,
      behavior: "smooth",
    });

    try {
      await addPostComment(selectedPost.id, newComment);
    } catch (error) {
      pushSnackbarMessage(FAILURE_MESSAGE.COMMENT_SAVE_FAILED);
    }
  };

  const commentListItems =
    comments?.map((comment) => (
      <CommentListItem key={comment.id}>
        <CommentContentWrapper>
          <Avatar diameter="2.5rem" imageUrl={comment.profileImageUrl} />
          <CommentText>
            <PostContentAuthorLink to={PAGE_URL.USER_PROFILE(comment.authorName)}>
              {comment.authorName}
            </PostContentAuthorLink>
            {comment.content}
          </CommentText>
        </CommentContentWrapper>
        {(currentUsername === comment.authorName || selectedPost.authorName === currentUsername) && (
          <DeleteIconWrapper onClick={() => handleCommentDeleteClick(comment.id)}>
            <SVGIcon icon="DeleteIcon" />
          </DeleteIconWrapper>
        )}
      </CommentListItem>
    )) ?? [];

  const tagListItems = selectedPost.tags.map((tag: string) => (
    <TagItemLinkButton key={tag} to={PAGE_URL.TAG_FEED(tag)}>
      <Chip>{tag}</Chip>
    </TagItemLinkButton>
  ));

  const horizontalSliderComponents = [
    <ImageSlider key="images" slideButtonKind="in-box" imageUrls={selectedPost.imageUrls} />,
    <PostContent key="contents">
      <PostContentAuthorLink to={PAGE_URL.USER_PROFILE(selectedPost.authorName)}>
        {selectedPost.authorName}
      </PostContentAuthorLink>
      {getTextElementsWithWithBr(selectedPost.content)}
    </PostContent>,
    <TagListWrapper key="tags">{tagListItems}</TagListWrapper>,
  ];

  const horizontalSliderItems = horizontalSliderComponents.map((component, index) => (
    <HorizontalSliderItemWrapper key={COMMENT_SLIDE_STEPS[index].title} stepCount={COMMENT_SLIDE_STEPS.length}>
      {component}
    </HorizontalSliderItemWrapper>
  ));

  if (isLoading) {
    return (
      <Container>
        <PageLoading />
      </Container>
    );
  }

  if (isError || !comments) {
<<<<<<< HEAD
    return <PageError errorMessage="댓글 정보를 불러오는데 실패했습니다" />;
=======
    return <div>에러!!</div>;
>>>>>>> b7fe88f (refactor: CommentsPage 리팩터링)
  }

  return (
    <Container ref={containerRef}>
      <SliderHeader>
        <GoBackLinkButton>
          <SVGIcon icon="GoBackIcon" onClick={handleGoBack} />
        </GoBackLinkButton>
        <CloseLinkButton isPostShown={isPostShown}>
          <SVGIcon icon="GoDownIcon" onClick={handleTogglePost} />
        </CloseLinkButton>
      </SliderHeader>
      {isPostShown && (
        <HorizontalSliderWrapper>
          <HorizontalSlider stepCount={COMMENT_SLIDE_STEPS.length} stepIndex={stepIndex}>
            {horizontalSliderItems}
          </HorizontalSlider>
          <TabsWrapper>
            <Tabs tabIndicatorKind="pill" tabItems={tabItems} />
          </TabsWrapper>
        </HorizontalSliderWrapper>
      )}
      {isAddCommentLoading || isDeleteCommentLoading ? (
        <CommentLoadingWrapper>
          <PageLoading />
        </CommentLoadingWrapper>
      ) : (
        <InfiniteScrollContainer isLoaderShown={isFetching} onIntersect={getNextComments}>
          <CommentList>{commentListItems}</CommentList>
        </InfiniteScrollContainer>
      )}
      {isLoggedIn && (
        <CommentTextAreaWrapper>
          <CommentTextArea placeholder="댓글 입력..." ref={commentTextAreaRef} />
          <SendIconWrapper>
            <SVGIcon icon="SendIcon" onClick={handleCommentSave} />
          </SendIconWrapper>
        </CommentTextAreaWrapper>
      )}
      {isModalShown && isCancelButtonShown && (
        <MessageModalPortal
          heading={modalMessage}
          onConfirm={handleCommentDelete}
          onClose={hideMessageModal}
          onCancel={hideMessageModal}
        />
      )}
    </Container>
  );
};

export default CommentsPage;
