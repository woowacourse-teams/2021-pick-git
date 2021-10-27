import styled from "styled-components";
import { breathingAnimation } from "../../@styled/keyframes";

export const Container = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
`;

export const LogoIconWrapper = styled.div`
  position: relative;
  bottom: 0.4rem;
  margin-right: 0.5rem;
  animation: ${breathingAnimation} 2s linear infinite;
`;
