import styled from "styled-components";
import { setMobileMediaQuery } from "../@styled/mediaQueries";

export const Container = styled.div<React.CSSProperties>``;

export const PostItemWrapper = styled.div`
  margin-bottom: 2rem;
  border-radius: 5px;
  background-color: ${({ theme }) => theme.color.white};
  box-shadow: 1px 2px 6px ${({ theme }) => `rgba(0,0,0, 0.2)`};
  ${setMobileMediaQuery`
    box-shadow: none;
  `};
`;
