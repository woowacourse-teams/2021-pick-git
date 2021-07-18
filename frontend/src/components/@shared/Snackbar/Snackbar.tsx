import { Container } from "./Snackbar.style";
import { SNACKBAR_GAP_REM, SNACKBAR_HEIGHT_REM, SnackBarOrder, SNACKBAR_DURATION } from "../../../constants/snackbar";

const getSnackBarBottom = (order: SnackBarOrder) =>
  `${order * (SNACKBAR_HEIGHT_REM + SNACKBAR_GAP_REM) - SNACKBAR_HEIGHT_REM}rem`;

export interface Props {
  children: string;
  order: SnackBarOrder;
}

const SnackBar = ({ children, order }: Props) => {
  return (
    <Container
      bottom={getSnackBarBottom(order)}
      snackbarDuration={SNACKBAR_DURATION}
      animationDuration={`${SNACKBAR_DURATION + 200}ms`}
    >
      {children}
    </Container>
  );
};

export default SnackBar;
