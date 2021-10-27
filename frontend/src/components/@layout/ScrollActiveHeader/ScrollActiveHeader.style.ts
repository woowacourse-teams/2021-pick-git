import styled from "styled-components";
import { Z_INDEX } from "../../../constants/layout";

export const Container = styled.div<{ isHeaderShown: boolean }>`
  position: fixed;
  top: 0;
  width: 100%;
  z-index: ${Z_INDEX.LOW};
  transition: transform 0.5s;

  transform: ${({ isHeaderShown }) => (isHeaderShown ? "translateY(0)" : "translateY(-100%)")};
`;
