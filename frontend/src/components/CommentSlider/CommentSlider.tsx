import ImageSlider from "../@shared/ImageSlider/ImageSlider";
import Chip from "../@shared/Chip/Chip";
import Avatar from "../@shared/Avatar/Avatar";
import Tabs from "../@shared/Tabs/Tabs";

import { GoDownIcon, HeartIcon, HeartLineIcon, SendIcon } from "../../assets/icons";
import BottomSliderPortal from "../@layout/BottomSliderPortal/BottomSliderPortal";
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
  CloseButton,
  HorizontalSlider,
  HorizontalSliderItemWrapper,
  CommentText,
  CommentContent,
  CommentTextAreaWrapper,
} from "./CommentSlider.styles";
import { CommentData, Post, TabItem } from "../../@types";
import { COMMENT_SLIDE_STEPS } from "../../constants/steps";
import { useContext, useEffect, useRef, useState } from "react";
import { PAGE_URL } from "../../constants/urls";
import UserContext from "../../contexts/UserContext";

export interface Props {
  post?: Post;
  isSliderShown: boolean;
  onSliderClose: () => void;
  onCommentSave: (value: string) => void;
}

const CommentSlider = ({ post, isSliderShown, onSliderClose, onCommentSave }: Props) => {
  const { isLoggedIn } = useContext(UserContext);
  const [sliderPost, setSliderPost] = useState<Post>();
  const [stepIndex, setStepIndex] = useState(0);
  const commentTextAreaRef = useRef<HTMLTextAreaElement>(null);

  useEffect(() => {
    if (!post) {
      return;
    }

    setSliderPost(post);
  }, [post]);

  if (!sliderPost) {
    return (
      <BottomSliderPortal isSliderShown={isSliderShown}>
        <div>지정된 게시글이 없습니다</div>
      </BottomSliderPortal>
    );
  }

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

  const commentListItems = sliderPost.comments.map((comment) => (
    <CommentListItem key={comment.id}>
      <CommentContent>
        <Avatar
          diameter="2.5rem"
          imageUrl="https://images.unsplash.com/photo-1599566150163-29194dcaad36?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80"
        />
        <CommentText>
          <PostContentAuthorLink to={PAGE_URL.USER_PROFILE(sliderPost.authorName)}>
            {sliderPost.authorName}
          </PostContentAuthorLink>
          {comment.content}
        </CommentText>
      </CommentContent>
      {comment.isLiked ? <HeartIcon /> : <HeartLineIcon />}
    </CommentListItem>
  ));

  const tagListItems = JSON.parse(sliderPost.tags.join(",")).map((tag: string) => (
    <TagItemLinkButton key={tag} to={PAGE_URL.TAG_FEED(tag)}>
      <Chip>{tag}</Chip>
    </TagItemLinkButton>
  ));

  const horizontalSliderComponents = [
    <ImageSlider slideButtonKind="in-box" imageUrls={sliderPost.imageUrls} />,
    <PostContent>
      <PostContentAuthorLink to={PAGE_URL.USER_PROFILE(sliderPost.authorName)}>
        {sliderPost.authorName}
      </PostContentAuthorLink>
      {sliderPost.content}
    </PostContent>,
    <TagListWrapper>{tagListItems}</TagListWrapper>,
  ];

  const horizontalSliderItems = horizontalSliderComponents.map((component, index) => (
    <HorizontalSliderItemWrapper key={COMMENT_SLIDE_STEPS[index].title} stepCount={COMMENT_SLIDE_STEPS.length}>
      {component}
    </HorizontalSliderItemWrapper>
  ));

  const handleCommentSave = () => {
    commentTextAreaRef.current && onCommentSave(commentTextAreaRef.current?.value);
  };

  return (
    <BottomSliderPortal isSliderShown={isSliderShown}>
      <Container>
        <SliderHeader>
          <CloseButton>
            <GoDownIcon onClick={onSliderClose} />
          </CloseButton>
        </SliderHeader>
        <HorizontalSlider stepCount={COMMENT_SLIDE_STEPS.length} stepIndex={stepIndex}>
          {horizontalSliderItems}
        </HorizontalSlider>
        <TabsWrapper>
          <Tabs tabIndicatorKind="pill" tabItems={tabItems} />
        </TabsWrapper>
        <CommentList>{commentListItems}</CommentList>
        {isLoggedIn && (
          <CommentTextAreaWrapper>
            <CommentTextArea placeholder="댓글 입력..." ref={commentTextAreaRef} />
            <SendIcon onClick={handleCommentSave} />
          </CommentTextAreaWrapper>
        )}
      </Container>
    </BottomSliderPortal>
  );
};

export default CommentSlider;
