import styled, { css } from "styled-components";
import { setLaptopAboveMediaQuery, setTabletAboveMediaQuery } from "../@styled/mediaQueries";

interface AvatarWrapperProps extends React.CSSProperties {
  avatarDiameter: string;
}

export const Container = styled.form`
  position: relative;
  width: 20.5rem;
  height: 32rem;
  padding: 2rem 1rem;
  overflow-y: hidden;

  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;

  ${setTabletAboveMediaQuery`
    width: 35rem;
    height: 50rem;
    padding: 3rem 2rem;
  `}
`;

export const Heading = styled.h2`
  font-size: 1.625rem;
  margin: 0;
  color: ${({ theme }) => theme.color.textColor};
  margin-bottom: 2rem;

  ${setTabletAboveMediaQuery`
    margin-bottom: 3rem;
  `}
`;

export const Label = styled.label<AvatarWrapperProps>`
  cursor: pointer;
  position: relative;
  margin-bottom: 2rem;

  > svg {
    position: absolute;
    right: -1px;
    top: ${({ avatarDiameter }) => avatarDiameter};

    transform: translateY(-100%);
  }

  ${setTabletAboveMediaQuery`
    margin-bottom: 3rem;
  `}
`;

export const TextEditorWrapper = styled.div`
  width: 100%;
  flex-grow: 1;
  padding: 0.75rem;
  background-color: ${({ theme }) => theme.color.secondaryColor};
  margin-bottom: 2rem;

  ${setTabletAboveMediaQuery`
    margin-bottom: 3rem;
  `}
`;

export const TextEditorCSS = css`
  height: 100%;
  font-size: 0.75rem;
  line-height: 1rem;

  ${setLaptopAboveMediaQuery`
    font-size: 1rem;
    line-height: 1.5rem;
  `}
`;
