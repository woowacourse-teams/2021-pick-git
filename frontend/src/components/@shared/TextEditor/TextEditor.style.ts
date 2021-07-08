import styled from "styled-components";

const TextArea = styled.textarea<React.CSSProperties>`
  ${({ width, height, minHeight, backgroundColor }) => `
    width: ${width ?? "100%"};
    min-height: ${minHeight ?? "fit-content"};
    height: ${height ?? "fit-content"};
    background-color: ${backgroundColor ?? "transparent"};
  `}

  border: none;
  outline: none;
  border-radius: 4px;
  padding: 1.1rem 1rem;
`;

export default TextArea;
