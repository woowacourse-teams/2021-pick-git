import { CircleImage, Container, Name } from "./Avatar.style";

export interface Props {
  diameter: string;
  fontSize?: string;
  imageUrl?: string;
  name?: string;
}

const Avatar = ({ diameter, fontSize, imageUrl, name }: Props) => (
  <Container>
    <CircleImage width={diameter} height={diameter} backgroundImage={imageUrl} />
    {name && <Name fontSize={fontSize}>{name}</Name>}
  </Container>
);

export default Avatar;
