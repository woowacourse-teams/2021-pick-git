import styled from "styled-components";

export const Container = styled.div<React.CSSProperties>`
  display: flex;
  flex-direction: column;
  align-items: flex-end;

  ${({ height, minHeight }) => `
    height: ${height === "" ? minHeight : `calc(${height} + 1.5rem)`};
    min-height: ${minHeight};
  `}
`;

export const TextArea = styled.textarea<React.CSSProperties>`
  ${({ width, height, fontSize }) => `
    width: ${width ?? "100%"};
    height: ${height === "" ? "100%" : height};
    font-size: ${fontSize ?? "1rem"};
`}

  height: 100%;
  border: none;
  outline: none;
  background-color: transparent;
`;

export const TextLengthIndicator = styled.div<React.CSSProperties>`
  margin-top: 0.5rem;
  color: ${({ theme }) => theme.color.lighterTextColor};
  font-size: 0.8rem;
  float: right;

  display: flex;
  justify-content: flex-end;
  width: 3.5rem;
  height: 1rem;
`;
