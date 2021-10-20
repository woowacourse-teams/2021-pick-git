import styled, { css } from "styled-components";
import { Page } from "../../components/@styled/layout";
import { setMobileMediaQuery, setTabletMediaQuery } from "../../components/@styled/mediaQueries";

export const Container = styled(Page)<React.CSSProperties>``;

export const PostTabWrapper = styled.div`
  display: flex;
  justify-content: flex-end;
  margin-bottom: 1.25rem;
`;

export const postTabCSS = css`
  width: 40%;
  min-width: 10.625rem;

  ${setMobileMediaQuery`
    font-size: 0.8rem;
  `}

  ${setTabletMediaQuery`
    font-size:0.8rem;
  `};
`;
