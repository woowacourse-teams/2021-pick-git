import { Container, AuthorName, Content } from "./Comment.style";

export interface Props {
  authorName: string;
  content: React.ReactNode;
  link?: string;
  onCommentLike: () => void;
}

const Comment = ({ authorName, link, content, onCommentLike }: Props) => {
  return (
    <Container>
      <AuthorName to={link ? link : ""}>{authorName}</AuthorName>
      <Content>{content}</Content>
    </Container>
  );
};

export default Comment;
