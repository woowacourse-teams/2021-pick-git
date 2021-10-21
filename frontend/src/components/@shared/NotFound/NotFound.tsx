import { Container, Image, Text } from "./NotFound.style";
import postNotFoundPNG from "../../../assets/images/post-not-found.png";
import userNotFoundPNG from "../../../assets/images/user-not-found.png";
import { CSSProp } from "styled-components";

export interface Props {
  type: "post" | "user";
  message?: string;
  cssProp?: CSSProp;
}

const NotFound = ({ type, message, cssProp }: Props) => {
  const notFoundImage = type === "post" ? postNotFoundPNG : userNotFoundPNG;

  return (
    <Container cssProp={cssProp}>
      <Image src={notFoundImage} alt="찾지 못함 이미지" />
      <Text>{message}</Text>
    </Container>
  );
};

export default NotFound;
