import { Link } from "react-router-dom";
import styled, { css } from "styled-components";
import { fadeIn } from "../@styled/keyframes";

export const ListItem = styled.li`
  display: flex;
  justify-content: space-between;
  align-items: center;

  height: 3rem;
  border-bottom: 1px solid ${({ theme }) => theme.color.borderColor};

  animation: ${fadeIn} 1s forwards;
`;

export const Empty = styled.div`
  width: 100%;
  height: 100vh;

  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
`;

export const Button = styled.button<{ follow: boolean }>`
  color: ${({ follow, theme }) => (follow ? theme.color.tertiaryColor : theme.color.primaryColor)};
  font-size: 0.8rem;
  font-weight: bold;
`;

export const NameTag = styled(Link)(
  ({ theme }) => css`
    display: flex;
    align-items: center;

    span {
      font-size: 1rem;
      font-weight: bold;
      line-height: 0.9;
      display: inline-block;
      margin-left: 1rem;
      color: ${theme.color.textColor};
    }
  `
);

export const ButtonLoader = styled(Button)`
  height: 2rem;
  opacity: 0.6;
  position: relative;
`;

export const ButtonSpinnerWrapper = styled.div`
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;

  display: flex;
  justify-content: center;
  align-items: center;
`;
