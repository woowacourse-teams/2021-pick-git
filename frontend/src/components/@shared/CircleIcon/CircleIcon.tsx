import { CircleBackground, Container, Name } from "./CircleIcon.style";

export interface Props extends React.HTMLAttributes<HTMLDivElement> {
  diameter: string;
  fontSize?: string;
  backgroundColor?: string;
  name?: string;
}

const CircleIcon = ({ diameter, fontSize, backgroundColor, name, children }: Props) => (
  <Container>
    <CircleBackground width={diameter} height={diameter} backgroundColor={backgroundColor}>
      {children}
    </CircleBackground>
    {name && <Name fontSize={fontSize}>{name}</Name>}
  </Container>
);

export default CircleIcon;
