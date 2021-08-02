import styled from "styled-components";

export const Container = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 1.6875rem;
  height: 100%;
`;

export const ImageUploaderWrapper = styled.div`
  margin-top: 1.5625rem;
`;

export const ImageChangeIconLink = styled.a`
  transition: opacity 0.5s;

  :hover {
    opacity: 0.5;
  }
`;

export const TextEditorWrapper = styled.div`
  width: 100%;
  flex-grow: 1;
  margin-top: 1.5625rem;
`;
