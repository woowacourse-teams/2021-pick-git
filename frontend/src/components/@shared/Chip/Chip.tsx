import { CSSProp } from "styled-components";
import { DeleteIcon } from "../../../assets/icons";
import { Container, DeleteButton, Text } from "./Chip.style";

export interface Props extends React.HTMLAttributes<HTMLSpanElement> {
  backgroundColor?: string;
  onDelete?: React.MouseEventHandler<HTMLButtonElement>;
  children: React.ReactText;
  cssProp?: CSSProp;
}

const Chip = ({ backgroundColor, children, cssProp, onDelete }: Props) => {
  return (
    <Container backgroundColor={backgroundColor} cssProp={cssProp}>
      <Text>{children}</Text>
      {onDelete && (
        <DeleteButton onClick={onDelete}>
          <DeleteIcon />
        </DeleteButton>
      )}
    </Container>
  );
};

export default Chip;
