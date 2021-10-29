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
import { getTextElementsWithBr } from "../../utils/text";

import {
  CloseLinkButton,
  CloseLinkButtonWrapper,
  CloseLinkText,
  CommentContent,
  CommentContentWrapper,
  CommentList,
  CommentListItem,
  CommentTextWrapper,
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
import usePostDetail from "../../hooks/service/usePostDetail";

const CommentsPage = () => {
  const postIdSearchParam = new URLSearchParams(location.search).get("id");
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

  const { post, isLoading: isPostLoading } = usePostDetail(Number(postIdSearchParam), !selectedPost);

  const postId = post?.id ?? selectedPost?.id;

  const {
    infiniteCommentsData,
    isFetching,
    isError,
    isLoading: isCommentsLoading,
    isAddCommentLoading,
    isDeleteCommentLoading,
    getNextComments,
    addPostComment,
    deletePostComment,
  } = useComments(postId);

  const targetPost = selectedPost ? selectedPost : post;

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
    if (history.length < 3) {
      history.push(PAGE_URL.HOME);
      return;
    }

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
    await deletePostComment(postId, selectedCommentId);
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
      await addPostComment(postId, newComment);
    } catch (error) {
      pushSnackbarMessage(FAILURE_MESSAGE.COMMENT_SAVE_FAILED);
    }
  };

  const handleCommentTextInput: React.KeyboardEventHandler<HTMLTextAreaElement> = (event) => {
    if (event.ctrlKey && event.code === "Enter") {
      event.preventDefault();
      event.currentTarget.value += "\n";
      return;
    }

    if (event.shiftKey && event.code === "Enter") {
      event.preventDefault();
      event.currentTarget.value += "\n";
      return;
    }

    if (event.code === "Enter") {
      event.preventDefault();
      handleCommentSave();
      return;
    }
  };

  useEffect(() => {
    if (!containerRef.current) {
      return;
    }

    containerRef.current.scroll({
      top: window.outerHeight,
      behavior: "smooth",
    });
  }, [comments.length]);

  if (isPostLoading || isCommentsLoading) {
    return <PageLoading />;
  }

  if (!targetPost) {
    return <PageError errorMessage="게시글을 찾을 수가 없습니다" />;
  }

  if (isError || !comments) {
    return <PageError errorMessage="댓글 정보를 불러오는데 실패했습니다" />;
  }

  const commentListItems =
    comments.length === 0 ? (
      <NotFound type="comment" message="작성된 댓글이 없습니다" cssProp={NotFoundCSS} />
    ) : (
      comments.map((comment) => (
        <CommentListItem key={comment.id}>
          <CommentContentWrapper>
            <Avatar diameter="2.5rem" imageUrl={comment.profileImageUrl} />
            <CommentTextWrapper>
              <PostContentAuthorLink to={PAGE_URL.USER_PROFILE(comment.authorName)}>
                {comment.authorName}
              </PostContentAuthorLink>
              <CommentContent>{comment.content}</CommentContent>
            </CommentTextWrapper>
          </CommentContentWrapper>
          {(currentUsername === comment.authorName || targetPost.authorName === currentUsername) && (
            <DeleteIconWrapper onClick={() => handleCommentDeleteClick(comment.id)}>
              <SVGIcon icon="DeleteIcon" />
            </DeleteIconWrapper>
          )}
        </CommentListItem>
      ))
    );

  const tagListItems = targetPost.tags.map((tag: string) => (
    <TagItemLinkButton key={tag} to={PAGE_URL.SEARCH_POST_BY_TAG(tag)}>
      <Chip>{tag}</Chip>
    </TagItemLinkButton>
  ));

  const horizontalSliderComponents = [
    <ImageSlider key="images" slideButtonKind="in-box" imageUrls={targetPost.imageUrls} />,
    <PostContent key="contents">
      <PostContentAuthorLink to={PAGE_URL.USER_PROFILE(targetPost.authorName)}>
        {targetPost.authorName}
      </PostContentAuthorLink>
      {getTextElementsWithBr(targetPost.content)}
    </PostContent>,
    <TagListWrapper key="tags">{tagListItems}</TagListWrapper>,
  ];

  const horizontalSliderItems = horizontalSliderComponents.map((component, index) => (
    <HorizontalSliderItemWrapper key={COMMENT_SLIDE_STEPS[index].title} stepCount={COMMENT_SLIDE_STEPS.length}>
      {component}
    </HorizontalSliderItemWrapper>
  ));

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
