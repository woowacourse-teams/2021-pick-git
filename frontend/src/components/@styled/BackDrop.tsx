import styled from "styled-components";
import { Z_INDEX } from "../../constants/layout";

const BackDrop = styled.div<React.CSSProperties>`
  position: fixed;
  z-index: ${({ zIndex }) => zIndex ?? Z_INDEX.UNDER_ROOT};
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
`;

export default BackDrop;
