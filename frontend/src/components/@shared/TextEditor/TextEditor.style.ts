import styled from "styled-components";

const TextArea = styled.textarea<React.CSSProperties>`
  ${({ width, height, minHeight, fontSize }) => `
    width: ${width ?? "100%"};
    min-height: ${minHeight ?? "fit-content"};
    height: ${height ?? "fit-content"};
    font-size: ${fontSize ?? "1rem"};
  `}

  border: none;
  outline: none;
`;

export default TextArea;
