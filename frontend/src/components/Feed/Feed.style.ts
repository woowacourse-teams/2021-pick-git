import styled, { css } from "styled-components";
import { setMobileMediaQuery } from "../@styled/mediaQueries";

export const Container = styled.div<React.CSSProperties>``;

export const PostItemWrapper = styled.div`
  position: relative;
  margin-bottom: 2rem;
  border-radius: 5px;
  background-color: ${({ theme }) => theme.color.white};
  box-shadow: 1px 2px 6px rgba(0, 0, 0, 0.2);
  ${setMobileMediaQuery`
    box-shadow: none;
  `};
`;

export const NotFoundCSS = css`
  margin-top: 3rem;
  width: 100%;
`;
