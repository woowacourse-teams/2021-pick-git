import { keyframes } from "styled-components";

export const fadeIn = keyframes`
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
`;

export const bottomToBottom = keyframes`
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(50%);
  }
  
  25% {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }

  75% {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }

  to {
    opacity: 0;
    transform: translateX(-50%) translateY(50%);
  }
`;

export const bounceAnimation = keyframes`
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
`;

export const spinAnimation = keyframes`
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
`;

export const breathingAnimation = keyframes`
  from {
    opacity: 1;
  }

  50% {
    opacity: 0.3;
  }

  to {
    opacity: 1;
  }
`;
