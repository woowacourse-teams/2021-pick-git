import { CSSProp } from "styled-components";
import { CircleImage, Container, Name } from "./Avatar.style";

export interface Props {
  diameter: string;
  fontSize?: string;
  imageUrl?: string;
  name?: string;
  cssProp?: CSSProp;
}

const Avatar = ({ diameter, fontSize, imageUrl, name, cssProp }: Props) => (
  <Container cssProp={cssProp}>
    <CircleImage width={diameter} height={diameter} backgroundImage={imageUrl} />
    {name && <Name fontSize={fontSize}>{name}</Name>}
  </Container>
);

export default Avatar;
