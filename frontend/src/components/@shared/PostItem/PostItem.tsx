import {
  Container,
  CommentSliderToggleLink,
  CommentsWrapper,
  CommentWrapper,
  IconLink,
  IconLinkButtonsWrapper,
  LikeCountText,
  PostAuthorInfoLink,
  PostAuthorName,
  PostContentAuthorLink,
  PostBody,
  PostContent,
  PostHeader,
  TagListWrapper,
  TagItemLinkButton,
  PostCreatedDateText,
  MoreContentLinkButton,
  CommentSliderToggleLinkText,
  MoreCommentExistIndicator,
} from "./PostItem.style";
import Avatar from "../Avatar/Avatar";
import CircleIcon from "../CircleIcon/CircleIcon";
import Comment from "../Comment/Comment";
import ImageSlider from "../ImageSlider/ImageSlider";
import Chip from "../Chip/Chip";
import { CommentData } from "../../../@types";
import {
  EditIcon,
  PostHeartIcon,
  PostHeartLineIcon,
  GithubIcon,
  TrashIcon,
  ArrowRightIcon,
} from "../../../assets/icons";
import { useContext, useState } from "react";
import { ThemeContext } from "styled-components";
import { PAGE_URL } from "../../../constants/urls";
import { LIMIT } from "../../../constants/limits";
import { getTimeDiffFromCurrent } from "../../../utils/date";
import EmptyPostImage from "../../../assets/images/empty-post-image.png";
import ButtonDrawer from "../ButtonDrawer/ButtonDrawer";

export interface Props {
  currentUserName: string;
  authorName: string;
  authorImageUrl: string;
  authorGithubUrl: string;
  isEditable: boolean;
  imageUrls: string[];
  likeCount: number;
  liked: boolean;
  content: string;
  comments: CommentData[];
  commenterImageUrl: string;
  tags: string[];
  createdAt: string;
  isLoggedIn: boolean;
  onMoreCommentClick: () => void;
  onCommentInputClick: () => void;
  onPostEdit: () => void;
  onPostDelete: () => void;
  onPostLike: () => void;
  onCommentLike: (commentId: CommentData["id"]) => void;
}

const timeDiffTextTable = {
  sec: () => "방금 전",
  min: (time: number) => `${time}분 전`,
  hour: (time: number) => `${time}시간 전`,
  day: (time: number) => `${time}일 전`,
};

const PostItem = ({
  currentUserName,
  authorName,
  authorImageUrl,
  authorGithubUrl,
  isEditable,
  imageUrls,
  likeCount,
  liked,
  content,
  comments,
  tags,
  createdAt,
  isLoggedIn,
  onMoreCommentClick,
  onCommentInputClick,
  onCommentLike,
  onPostEdit,
  onPostDelete,
  onPostLike,
}: Props) => {
  const [shouldHideContent, setShouldHideContent] = useState(content.length > LIMIT.POST_CONTENT_HIDE_LENGTH);
  const { color } = useContext(ThemeContext);

  const { min, hour, day } = getTimeDiffFromCurrent(createdAt);
  const currentTimeDiffText = day
    ? timeDiffTextTable.day(day)
    : hour
    ? timeDiffTextTable.hour(hour)
    : min
    ? timeDiffTextTable.min(min)
    : timeDiffTextTable.sec();

  const circleButtons = [
    { node: <EditIcon />, onClick: onPostEdit },
    { node: <TrashIcon />, onClick: onPostDelete },
  ];

  const commentList = comments.map((comment) => (
    <CommentWrapper key={comment.id}>
      <Comment
        content={comment.content}
        liked={comment.liked}
        authorName={comment.authorName}
        link={currentUserName === comment.authorName ? PAGE_URL.MY_PROFILE : PAGE_URL.USER_PROFILE(comment.authorName)}
        onCommentLike={() => onCommentLike(comment.id)}
      />
    </CommentWrapper>
  ));

  const tagList = tags.map((tag: string, index: number) => (
    // TODO: key prop 수정 => tag가 unique임이 보장된 후에!
    <TagItemLinkButton key={index} to={PAGE_URL.POSTS_WITH_TAG(tag)}>
      <Chip>{tag}</Chip>
    </TagItemLinkButton>
  ));

  const handleMoreContentShow = () => {
    setShouldHideContent(false);
  };

  const handleMoreContentHide = () => {
    setShouldHideContent(true);
  };

  return (
    <Container>
      <PostHeader>
        <PostAuthorInfoLink to={PAGE_URL.USER_PROFILE(authorName)}>
          <Avatar diameter="1.9375rem" imageUrl={authorImageUrl} />
          <PostAuthorName>{authorName}</PostAuthorName>
        </PostAuthorInfoLink>
        {isEditable && <ButtonDrawer circleButtons={circleButtons} />}
      </PostHeader>
      <ImageSlider imageUrls={imageUrls.length !== 0 ? imageUrls : [EmptyPostImage]} slideButtonKind="in-box" />
      <PostBody>
        <IconLinkButtonsWrapper>
          {isLoggedIn ? (
            <IconLink onClick={onPostLike}>{liked ? <PostHeartIcon /> : <PostHeartLineIcon />}</IconLink>
          ) : (
            <div></div>
          )}
          <IconLink href={authorGithubUrl} target="_blank">
            <CircleIcon diameter="1.625rem" backgroundColor={color.tertiaryColor}>
              <GithubIcon />
            </CircleIcon>
          </IconLink>
        </IconLinkButtonsWrapper>
        <LikeCountText>좋아요 {likeCount}개</LikeCountText>
        <PostContent>
          <PostContentAuthorLink to={PAGE_URL.USER_PROFILE(authorName)}>{authorName}</PostContentAuthorLink>
          {shouldHideContent ? content.slice(0, LIMIT.POST_CONTENT_HIDE_LENGTH).concat("...") : content}
          {shouldHideContent ? (
            <MoreContentLinkButton onClick={handleMoreContentShow}>더보기</MoreContentLinkButton>
          ) : (
            <MoreContentLinkButton onClick={handleMoreContentHide}>간략히</MoreContentLinkButton>
          )}
        </PostContent>
        <TagListWrapper>{shouldHideContent || tagList}</TagListWrapper>
        <CommentsWrapper>
          {commentList.length > 10
            ? commentList
                .slice(0, 10)
                .concat(<MoreCommentExistIndicator onClick={onMoreCommentClick}>...</MoreCommentExistIndicator>)
            : commentList}
        </CommentsWrapper>
      </PostBody>
      <PostCreatedDateText>{currentTimeDiffText}</PostCreatedDateText>
      <CommentSliderToggleLink onClick={onCommentInputClick}>
        <CommentSliderToggleLinkText>{isLoggedIn ? "댓글 작성" : "댓글 보기"}</CommentSliderToggleLinkText>
        <ArrowRightIcon />
      </CommentSliderToggleLink>
    </Container>
  );
};

export default PostItem;
