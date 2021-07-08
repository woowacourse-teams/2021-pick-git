import styled from "styled-components";

export const Container = styled.div`
  display: inline-flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
`;

export const CircleBackground = styled.div<React.CSSProperties>`
  ${({ width, height, backgroundColor, theme }) => `
    width: ${width};
    height: ${height};
    background-color: ${backgroundColor ?? theme.color.secondaryColor};
  `}

  display: flex;
  justify-content: center;
  align-items: center;

  border-radius: 50%;
`;

export const Name = styled.div<React.CSSProperties>`
  ${({ fontSize, theme }) => `
    font-size: ${fontSize ?? "0.8rem"};
    color: ${theme.color.textColor};
  `}

  margin-top: 0.375rem;
  font-weight: 300;
`;
