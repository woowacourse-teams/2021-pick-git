import { Container, AuthorName, Content, LikeIconWrapper } from "./Comment.style";
import { HeartIcon, HeartLineIcon } from "../../../assets/icons";

export interface Props {
  authorName: string;
  content: string;
  liked: boolean;
  link?: string;
  onCommentLike: () => void;
}

const Comment = ({ authorName, link, content, liked, onCommentLike }: Props) => {
  return (
    <Container>
      <div>
        <AuthorName to={link ? link : ""}>{authorName}</AuthorName>
        <Content>{content}</Content>
      </div>
      <LikeIconWrapper onClick={onCommentLike}>{liked ? <HeartIcon /> : <HeartLineIcon />}</LikeIconWrapper>
    </Container>
  );
};

export default Comment;
