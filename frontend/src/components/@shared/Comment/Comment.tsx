import { Container, AuthorName, Content, LikeIconWrapper } from "./Comment.style";
import { HeartIcon, HeartLineIcon } from "../../../assets/icons";
import { CommentData } from "../../../@types";

export interface Props extends CommentData {
  link?: string;
  onCommentLike: () => void;
}

const Comment = ({ authorName, link, content, isLiked, onCommentLike }: Props) => {
  return (
    <Container>
      <div>
        <AuthorName to={link ? link : ""}>{authorName}</AuthorName>
        <Content>{content}</Content>
      </div>
      <LikeIconWrapper onClick={onCommentLike}>{isLiked ? <HeartIcon /> : <HeartLineIcon />}</LikeIconWrapper>
    </Container>
  );
};

export default Comment;
