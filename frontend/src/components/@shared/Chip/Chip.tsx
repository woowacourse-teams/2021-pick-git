import cancelIcon from "../../../assets/icons/cancel.svg";
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
          <img src={cancelIcon} alt="태그 삭제 아이콘" />
        </DeleteButton>
      )}
    </Container>
  );
};

export default Chip;
