import { Container, Count, Name } from "./CountIndicator.style";

export interface Props {
  name: string;
  count: number;
}

const CountIndicator = ({ name, count }: Props) => (
  <Container>
    <Count>{count}</Count>
    <Name>{name}</Name>
  </Container>
);

export default CountIndicator;
