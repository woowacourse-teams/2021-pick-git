import { useEffect, useRef, useState } from "react";
import { useHistory, useLocation } from "react-router-dom";

import ConfirmPortal from "../../components/@layout/ConfirmPortal/ConfirmPortal";
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

import useSnackbar from "../../hooks/common/useSnackbar";
import useAuth from "../../hooks/common/useAuth";
import useComments from "../../hooks/service/useComments";

import { getItemsFromPages } from "../../utils/infiniteData";
import { getTextElementsWithWithBr } from "../../utils/text";

import {
  CloseLinkButton,
  CloseLinkButtonWrapper,
  CloseLinkText,
  CommentContentWrapper,
  CommentList,
  CommentListItem,
  CommentText,
  CommentTextArea,
  CommentTextAreaWrapper,
  Container,
  ContentWrapper,
  DeleteIconWrapper,
  GoBackLinkButton,
  HorizontalSlider,
  HorizontalSliderItemWrapper,
  HorizontalSliderWrapper,
  LoaderCSS,
  LoaderWrapper,
  NotFoundCSS,
  PostContent,
  PostContentAuthorLink,
  SendIconWrapper,
  SliderHeader,
  TabsWrapper,
  TagItemLinkButton,
  TagListWrapper,
} from "./CommentsPage.style";

import type { CommentData, Post, TabItem } from "../../@types";
import useModal from "../../hooks/common/useModal";
import Loader from "../../components/@shared/Loader/Loader";
import NotFound from "../../components/@shared/NotFound/NotFound";

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
  const {
    modalMessage: confirmMessage,
    isModalShown: isConfirmShown,
    showModal: showConfirm,
    hideModal: hideConfirm,
  } = useModal();

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

  const comments = getItemsFromPages<CommentData>(infiniteCommentsData?.pages) ?? [];

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
    showConfirm(WARNING_MESSAGE.COMMENT_DELETE);
  };

  const handleCommentDelete = async () => {
    hideConfirm();
    await deletePostComment(selectedPost.id, selectedCommentId);
  };

  const handleCommentSave = async () => {
    if (!commentTextAreaRef.current || !containerRef.current) {
      return;
    }

    if (commentTextAreaRef.current.value === "") {
      return;
    }

    const newComment = commentTextAreaRef.current.value;

    commentTextAreaRef.current.value = "";

    try {
      await addPostComment(selectedPost.id, newComment);
    } catch (error) {
      pushSnackbarMessage(FAILURE_MESSAGE.COMMENT_SAVE_FAILED);
    }
  };

  const handleCommentTextInput: React.KeyboardEventHandler<HTMLTextAreaElement> = (event) => {    
    if (event.ctrlKey && event.code === "Enter") {
      event.preventDefault();
      event.currentTarget.value += '\n';
      return;
    }

    if (event.code === "Enter") {
      event.preventDefault();
      handleCommentSave();
      return;
    }
  }

  useEffect(() => {
    if (!containerRef.current) {
      return;
    }

    containerRef.current.scroll({
      top: window.outerHeight,
      behavior: "smooth",
    });
  }, [comments.length]);

  const commentListItems = comments.length === 0 ? 
    <NotFound type="comment" message="작성된 댓글이 없습니다" cssProp={NotFoundCSS} /> : comments.map((comment) => (
      <CommentListItem key={comment.id}>
        <CommentContentWrapper>
          <Avatar diameter="2.5rem" imageUrl={comment.profileImageUrl} />
          <CommentText>
            <PostContentAuthorLink to={PAGE_URL.USER_PROFILE(comment.authorName)}>
              {comment.authorName}
            </PostContentAuthorLink>
            <span>
              {comment.content}
            </span>
          </CommentText>
        </CommentContentWrapper>
        {(currentUsername === comment.authorName || selectedPost.authorName === currentUsername) && (
          <DeleteIconWrapper onClick={() => handleCommentDeleteClick(comment.id)}>
            <SVGIcon icon="DeleteIcon" />
          </DeleteIconWrapper>
        )}
      </CommentListItem>
    ));

  const tagListItems = selectedPost.tags.map((tag: string) => (
    <TagItemLinkButton key={tag} to={PAGE_URL.SEARCH_POST_BY_TAG(tag)}>
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
    return <PageError errorMessage="댓글 정보를 불러오는데 실패했습니다" />;
  }

  return (
    <ContentWrapper ref={containerRef}>
      <Container>
        <SliderHeader>
          <GoBackLinkButton>
            <SVGIcon icon="GoBackIcon" onClick={handleGoBack} />
          </GoBackLinkButton>
          <CloseLinkButtonWrapper onClick={handleTogglePost}>
            <CloseLinkText>{isPostShown ? "숨기기" : "게시글 보기"}</CloseLinkText>
            <CloseLinkButton isPostShown={isPostShown}>
              <SVGIcon icon="GoDownIcon" />
            </CloseLinkButton>
          </CloseLinkButtonWrapper>
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

        <InfiniteScrollContainer isLoaderShown={isFetching} onIntersect={getNextComments}>
          <CommentList>{commentListItems}</CommentList>
          {(isAddCommentLoading || isDeleteCommentLoading) && (
            <LoaderWrapper>
              <Loader kind="dots" size="1rem" cssProp={LoaderCSS} />
            </LoaderWrapper>
          )}
        </InfiniteScrollContainer>
        {isLoggedIn && (
          <CommentTextAreaWrapper>
            <CommentTextArea placeholder="댓글 입력..." ref={commentTextAreaRef} onKeyPress={handleCommentTextInput} />
            <SendIconWrapper>
              <SVGIcon icon="SendIcon" onClick={handleCommentSave} />
            </SendIconWrapper>
          </CommentTextAreaWrapper>
        )}
        {isConfirmShown && (
          <ConfirmPortal heading={confirmMessage} onConfirm={handleCommentDelete} onCancel={hideConfirm} />
        )}
      </Container>
    </ContentWrapper>
  );
};

export default CommentsPage;
