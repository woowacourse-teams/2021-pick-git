import styled, { css } from "styled-components";

interface AvatarWrapperProps extends React.CSSProperties {
  avatarDiameter: string;
}

export const Container = styled.form`
  width: 20.5rem;
  height: 39rem;
  padding: 2rem 0;

  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
`;

export const Heading = styled.h2`
  font-size: 1.625rem;
  margin: 0;
  color: ${({ theme }) => theme.color.textColor};
`;

export const Label = styled.label<AvatarWrapperProps>`
  cursor: pointer;
  position: relative;

  > svg {
    position: absolute;
    right: -1px;
    top: ${({ avatarDiameter }) => avatarDiameter};

    transform: translateY(-100%);
  }
`;

export const TextEditorWrapper = styled.div`
  width: 100%;
  height: 14.125rem;
  padding: 0.75rem;
  background-color: ${({ theme }) => theme.color.secondaryColor};
`;

export const TextEditorCSS = css`
  height: 100%;
  font-size: 1rem;
`;
