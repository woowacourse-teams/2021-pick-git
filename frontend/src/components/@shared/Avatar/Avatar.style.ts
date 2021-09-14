import styled, { css, CSSProp } from "styled-components";

import defaultProfile from "../../../assets/images/default-profile.png";

export const Container = styled.div<{ cssProp?: CSSProp }>(
  ({ cssProp }) => css`
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: center;
    height: 100%;
    ${cssProp}
  `
);

export const CircleImage = styled.div<React.CSSProperties>`
  ${({ width, height, backgroundImage }) => `
    width: ${width};
    height: ${height};  
    background: no-repeat center/cover url(${backgroundImage ?? defaultProfile});
  `}

  border-radius: 50%;
`;

export const Name = styled.div<React.CSSProperties>`
  ${({ fontSize, theme }) => `
    font-size: ${fontSize ?? "1rem"};
    color: ${theme.color.textColor};
  `}

  margin-top: 0.375rem;
  font-weight: bold;
`;
