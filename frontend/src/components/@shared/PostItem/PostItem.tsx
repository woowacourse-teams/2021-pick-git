import {
  Container,
  MyComment,
  CommentInputWrapper,
  CommentWrapper,
  IconLinkButton,
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
} from "./PostItem.style";
import Avatar from "../Avatar/Avatar";
import CircleIcon from "../CircleIcon/CircleIcon";
import Comment from "../Comment/Comment";
import ImageSlider from "../ImageSlider/ImageSlider";
import Chip from "../Chip/Chip";
import { CommentData } from "../../../@types";
import { EditIcon, PostHeartIcon, PostHeartLineIcon, GithubIcon, SendIcon } from "../../../assets/icons";
import { useContext, useState } from "react";
import { ThemeContext } from "styled-components";
import { PAGE_URL } from "../../../constants/urls";
import { LIMIT } from "../../../constants/limits";
import TextEditor from "../TextEditor/TextEditor";
import { getTimeDiffFromCurrent } from "../../../utils/date";

export interface Props {
  authorName: string;
  authorImageUrl: string;
  authorGithubUrl: string;
  isEditable: boolean;
  imageUrls: string[];
  likeCount: number;
  isLiked: boolean;
  content: string;
  comments: CommentData[];
  commenterImageUrl: string;
  tags: string[];
  createdAt: string;
  commentValue: string;
  onCommentValueChange: React.ChangeEventHandler<HTMLTextAreaElement>;
  onCommentValueSave: () => void;
  onPostLike: () => void;
  onCommentLike: (commentId: string) => void;
}

const timeDiffTextTable = {
  sec: () => "방금 전",
  min: (time: number) => `${time}분 전`,
  hour: (time: number) => `${time}시간 전`,
  day: (time: number) => `${time}일 전`,
};

const PostItem = ({
  authorName,
  authorImageUrl,
  authorGithubUrl,
  isEditable,
  imageUrls,
  likeCount,
  isLiked,
  content,
  comments,
  commenterImageUrl,
  tags,
  createdAt,
  commentValue,
  onCommentValueChange,
  onCommentValueSave,
  onCommentLike,
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

  const commentList = comments.map((comment) => (
    <CommentWrapper key={comment.commentId}>
      <Comment
        content={comment.content}
        isLiked={comment.isLiked}
        authorName={comment.authorName}
        link={`/profile/${comment.authorName}`}
        onCommentLike={() => onCommentLike(comment.commentId)}
      />
    </CommentWrapper>
  ));

  const tagList = JSON.parse(tags.join(",")).map((tag: string) => (
    <TagItemLinkButton key={tag} to={PAGE_URL.POSTS_WITH_TAG(tag)}>
      <Chip>{tag}</Chip>
    </TagItemLinkButton>
  ));

  const onMoreContentShow = () => {
    setShouldHideContent(false);
  };

  return (
    <Container>
      <PostHeader>
        <PostAuthorInfoLink to={PAGE_URL.USER_PROFILE(authorName)}>
          <Avatar diameter="1.9375rem" imageUrl={authorImageUrl} />
          <PostAuthorName>{authorName}</PostAuthorName>
        </PostAuthorInfoLink>
        {isEditable && (
          <IconLinkButton to={PAGE_URL.EDIT_POST}>
            <EditIcon />
          </IconLinkButton>
        )}
      </PostHeader>
      <ImageSlider imageUrls={imageUrls} slideButtonKind="in-box" />
      <PostBody>
        <IconLinkButtonsWrapper>
          <IconLink onClick={onPostLike}>{isLiked ? <PostHeartIcon /> : <PostHeartLineIcon />}</IconLink>
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
          {shouldHideContent && <MoreContentLinkButton onClick={onMoreContentShow}>더보기</MoreContentLinkButton>}
        </PostContent>
        <TagListWrapper>{shouldHideContent || tagList}</TagListWrapper>
        {commentList}
      </PostBody>
      <MyComment>
        <Avatar diameter="1.9375rem" imageUrl={commenterImageUrl} />
        <CommentInputWrapper>
          <TextEditor
            placeholder="댓글 달기..."
            onChange={onCommentValueChange}
            value={commentValue}
            width="100%"
            height="0.8rem;"
            fontSize="0.625rem"
          />
        </CommentInputWrapper>
        <IconLink onClick={onCommentValueSave}>
          <SendIcon />
        </IconLink>
      </MyComment>
      <PostCreatedDateText>{currentTimeDiffText}</PostCreatedDateText>
    </Container>
  );
};

export default PostItem;
