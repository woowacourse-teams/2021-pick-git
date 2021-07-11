import { Container, Name, Content, LikeIconWrapper } from "./Comment.style";
import { HeartIcon, HeartLineIcon } from "../../../assets/icons";

export interface Props extends React.HTMLAttributes<HTMLDivElement> {
  name: string;
  link?: string;
  content: string;
  isLiked: boolean;
}

const Comment = ({ name, link, content, isLiked, ...props }: Props) => {
  return (
    <Container {...props}>
      <div>
        <Name to={link ? link : ""}>{name}</Name>
        <Content>{content}</Content>
      </div>
      <LikeIconWrapper>{isLiked ? <HeartIcon /> : <HeartLineIcon />}</LikeIconWrapper>
    </Container>
  );
};

export default Comment;
