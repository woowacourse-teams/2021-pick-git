import ImageSlider from "../../components/@shared/ImageSlider/ImageSlider";
import Chip from "../../components/@shared/Chip/Chip";
import Avatar from "../../components/@shared/Avatar/Avatar";
import Tabs from "../../components/@shared/Tabs/Tabs";

import { DeleteIcon, GoBackIcon, GoDownIcon, SendIcon } from "../../assets/icons";
import {
  CommentList,
  CommentListItem,
  CommentTextArea,
  Container,
  SliderHeader,
  PostContent,
  PostContentAuthorLink,
  TagListWrapper,
  TagItemLinkButton,
  TabsWrapper,
  GoBackLinkButton,
  CloseLinkButton,
  HorizontalSlider,
  HorizontalSliderItemWrapper,
  CommentText,
  CommentTextAreaWrapper,
  SendIconWrapper,
  DeleteIconWrapper,
  CommentContentWrapper,
} from "./CommentsPage.style";
import { CommentData, Post, TabItem } from "../../@types";
import { COMMENT_SLIDE_STEPS } from "../../constants/steps";
import { useContext, useRef, useState } from "react";
import { PAGE_URL } from "../../constants/urls";
import UserContext from "../../contexts/UserContext";
import InfiniteScrollContainer from "../../components/@shared/InfiniteScrollContainer/InfiniteScrollContainer";
import { useHistory, useLocation } from "react-router-dom";
import useComments from "../../services/hooks/useComments";
import { getItemsFromPages } from "../../utils/infiniteData";
import PageLoading from "../../components/@layout/PageLoading/PageLoading";
import SnackBarContext from "../../contexts/SnackbarContext";
import { FAILURE_MESSAGE, WARNING_MESSAGE } from "../../constants/messages";
import useMessageModal from "../../services/hooks/@common/useMessageModal";
import MessageModalPortal from "../../components/@layout/MessageModalPortal/MessageModalPortal";

export interface Props {}

const CommentsPage = ({}: Props) => {
  const [selectedCommentId, setSelectedCommentId] = useState<CommentData["id"]>(0);
  const [isPostShown, setIsPostShown] = useState(true);
  const [stepIndex, setStepIndex] = useState(0);
  const { state: selectedPost } = useLocation<Post>();
  const { pushSnackbarMessage } = useContext(SnackBarContext);
  const history = useHistory();
  const { currentUsername } = useContext(UserContext);
  const { modalMessage, isModalShown, isCancelButtonShown, showConfirmModal, hideMessageModal } = useMessageModal();

  const { infiniteCommentsData, isFetching, isError, isLoading, getNextComments, addPostComment, deletePostComment } =
    useComments(selectedPost.id);

  const { isLoggedIn } = useContext(UserContext);
  const commentTextAreaRef = useRef<HTMLTextAreaElement>(null);
  const containerRef = useRef<HTMLDivElement>(null);

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
      {selectedPost.content}
    </PostContent>,
    <TagListWrapper key="tags">{tagListItems}</TagListWrapper>,
  ];

  const horizontalSliderItems = horizontalSliderComponents.map((component, index) => (
    <HorizontalSliderItemWrapper key={COMMENT_SLIDE_STEPS[index].title} stepCount={COMMENT_SLIDE_STEPS.length}>
      {component}
    </HorizontalSliderItemWrapper>
  ));

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
    await deletePostComment(selectedCommentId);
    hideMessageModal();
  };

  const handleCommentSave = async () => {
    if (!commentTextAreaRef.current || !containerRef.current) {
      return;
    }

    try {
      await addPostComment(selectedPost.id, commentTextAreaRef.current.value);
    } catch (error) {
      pushSnackbarMessage(FAILURE_MESSAGE.COMMENT_SAVE_FAILED);
    }

    commentTextAreaRef.current.value = "";
    containerRef.current.scroll({
      top: containerRef.current.offsetHeight,
      behavior: "smooth",
    });
  };

  if (isLoading) {
    return (
      <Container>
        <PageLoading />
      </Container>
    );
  }

  if (isError || !infiniteCommentsData) {
    return <div>에러!!</div>;
  }

  const comments = getItemsFromPages<CommentData>(infiniteCommentsData.pages);

  const commentListItems = comments.map((comment) => (
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
      {currentUsername === comment.authorName && (
        <DeleteIconWrapper onClick={() => handleCommentDeleteClick(comment.id)}>
          <DeleteIcon />
        </DeleteIconWrapper>
      )}
    </CommentListItem>
  ));

  return (
    <Container ref={containerRef}>
      <SliderHeader>
        <GoBackLinkButton>
          <GoBackIcon onClick={handleGoBack} />
        </GoBackLinkButton>
        <CloseLinkButton isPostShown={isPostShown}>
          <GoDownIcon onClick={handleTogglePost} />
        </CloseLinkButton>
      </SliderHeader>
      {isPostShown && (
        <>
          <HorizontalSlider stepCount={COMMENT_SLIDE_STEPS.length} stepIndex={stepIndex}>
            {horizontalSliderItems}
          </HorizontalSlider>
          <TabsWrapper>
            <Tabs tabIndicatorKind="pill" tabItems={tabItems} />
          </TabsWrapper>
        </>
      )}
      <InfiniteScrollContainer isLoaderShown={isFetching} onIntersect={getNextComments}>
        <CommentList>{commentListItems}</CommentList>
      </InfiniteScrollContainer>
      {isLoggedIn && (
        <CommentTextAreaWrapper>
          <CommentTextArea placeholder="댓글 입력..." ref={commentTextAreaRef} />
          <SendIconWrapper>
            <SendIcon onClick={handleCommentSave} />
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
