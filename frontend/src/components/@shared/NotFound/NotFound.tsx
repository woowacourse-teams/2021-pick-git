import { Container, Image, Text } from "./NotFound.style";
import postNotFoundPNG from "../../../assets/images/post-not-found.png";
import userNotFoundPNG from "../../../assets/images/user-not-found.png";
import commentNotFoundJPG from "../../../assets/images/comment-not-found.jpg";
import { CSSProp } from "styled-components";

export interface Props {
  type: "post" | "user" | "comment";
  message?: string;
  cssProp?: CSSProp;
}

const NotFound = ({ type, message, cssProp }: Props) => {
  const notFoundImage = {
    post: postNotFoundPNG,
    user: userNotFoundPNG,
    comment: commentNotFoundJPG,
  };

  return (
    <Container cssProp={cssProp}>
      <Image src={notFoundImage[type]} alt="찾지 못함 이미지" />
      <Text>{message}</Text>
    </Container>
  );
};

export default NotFound;
