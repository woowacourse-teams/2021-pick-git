import styled from "styled-components";
import { bounceAnimation, spinAnimation } from "../../@styled/keyframes";

export const LoadingDots = styled.div<React.CSSProperties>``;

const LoadingDot = styled.div<{ size: string }>`
  ${({ theme, size }) => `
    width: ${size};
    height: ${size};  
    background-color: ${theme.color.tagItemColor}
  `};
  border-radius: 50%;
  display: inline-block;
  animation: 1.5s ${bounceAnimation} infinite ease-in-out both;
`;

export const FirstLoadingDot = styled(LoadingDot)`
  margin-right: 0.3rem;
`;

export const SecondLoadingDot = styled(LoadingDot)`
  margin-right: 0.3rem;
  animation-delay: 0.15s;
`;

export const ThirdLoadingDot = styled(LoadingDot)`
  animation-delay: 0.3s;
`;

export const Spinner = styled.div<{ size: string }>`
  ${({ theme, size }) => `
    width: ${size};
    height: ${size};  
    border: 3px solid ${theme.color.tagItemColor};
    border-top-color: ${theme.color.primaryColor};
  `};

  display: inline-block;
  border-radius: 50%;
  animation: 1s ${spinAnimation} infinite ease-in-out;
`;
