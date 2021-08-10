import { Container, AuthorName, Content } from "./Comment.style";

export interface Props {
  authorName: string;
  content: React.ReactNode;
  link?: string;
}

const Comment = ({ authorName, link, content }: Props) => {
  return (
    <Container>
      <AuthorName to={link ? link : ""}>{authorName}</AuthorName>
      <Content>{content}</Content>
    </Container>
  );
};

export default Comment;
