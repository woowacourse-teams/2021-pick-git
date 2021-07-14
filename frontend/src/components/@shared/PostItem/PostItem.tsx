import {
  Container,
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
  PostCreatedDateText,
} from "./PostItem.style";
import Avatar from "../Avatar/Avatar";
import CircleIcon from "../CircleIcon/CircleIcon";
import Comment from "../Comment/Comment";
import ImageSlider from "../ImageSlider/ImageSlider";
import { CommentData } from "../../../@types";
import { EditIcon, PostHeartIcon, PostHeartLineIcon, GithubIcon } from "../../../assets/icons";
import { useContext } from "react";
import { ThemeContext } from "styled-components";
import { PAGE_URL } from "../../../constants/urls";
import TextEditor from "../TextEditor/TextEditor";

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
  createdAt: string;
  commentValue: string;
  onCommentValueChange: (value: string) => void;
  onPostLike: () => void;
  onCommentLike: (commentId: string) => void;
}

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
  createdAt,
  commentValue,
  onCommentValueChange,
  onCommentLike,
}: Props) => {
  const { color } = useContext(ThemeContext);

  const commentList = comments.map((comment) => (
    <CommentWrapper key={comment.commentId}>
      <Comment
        commentId={comment.commentId}
        content={comment.content}
        isLiked={comment.isLiked}
        authorName={comment.authorName}
        link={`/profile/${comment.authorName}`}
        onCommentLike={() => onCommentLike(comment.authorName)}
      />
    </CommentWrapper>
  ));

  const handleCommentValueChange: React.ChangeEventHandler<HTMLTextAreaElement> = (event) => {
    const { value } = event.target;

    onCommentValueChange(value);
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
          <IconLink>{isLiked ? <PostHeartIcon /> : <PostHeartLineIcon />}</IconLink>
          <IconLink href={authorGithubUrl} target="_blank">
            <CircleIcon diameter="1.625rem" backgroundColor={color.tertiaryColor}>
              <GithubIcon />
            </CircleIcon>
          </IconLink>
        </IconLinkButtonsWrapper>
        <LikeCountText>좋아요 {likeCount}개</LikeCountText>
        <PostContent>
          <PostContentAuthorLink to={PAGE_URL.USER_PROFILE(authorName)}>{authorName}</PostContentAuthorLink>
          {content}
        </PostContent>
        {commentList}
      </PostBody>
      <CommentInputWrapper>
        <Avatar diameter="1.9375rem" imageUrl={commenterImageUrl} />
        <TextEditor
          placeholder="댓글 달기..."
          backgroundColor="transparent"
          onChange={handleCommentValueChange}
          value={commentValue}
          width="100%"
          height="0.8rem;"
          fontSize="0.625rem"
        />
      </CommentInputWrapper>
      <PostCreatedDateText>{createdAt}</PostCreatedDateText>
    </Container>
  );
};

export default PostItem;
