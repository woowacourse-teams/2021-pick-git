import { CancelIcon } from "../../../assets/icons";
import { Container, DeleteButton, Text } from "./Chip.style";

export interface Props extends React.HTMLAttributes<HTMLSpanElement> {
  backgroundColor?: string;
  onDelete?: React.MouseEventHandler<HTMLButtonElement>;
  children: React.ReactText;
}

const Chip = ({ backgroundColor, children, onDelete }: Props) => {
  return (
    <Container backgroundColor={backgroundColor}>
      <Text>{children}</Text>
      {onDelete && (
        <DeleteButton onClick={onDelete}>
          <CancelIcon />
        </DeleteButton>
      )}
    </Container>
  );
};

export default Chip;
