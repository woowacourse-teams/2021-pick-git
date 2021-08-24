import { Link } from "react-router-dom";
import styled from "styled-components";
import { setDesktopMediaQuery, setMobileMediaQuery } from "../../@styled/mediaQueries";

export const Container = styled.div`
  display: flex;
  align-items: center;
  color: ${({ theme }) => theme.color.textColor};
`;

export const AuthorName = styled(Link)`
  font-weight: bold;

  font-size: 0.8rem;
  ${setMobileMediaQuery`
    font-size: 0.75rem;
  `}
  ${setDesktopMediaQuery`
    font-size: 0.9rem;
  `}

  margin-right: 0.75rem;
`;

export const Content = styled.span`
  font-size: 0.7rem;
  ${setMobileMediaQuery`
    font-size: 0.625rem;
  `}
  ${setDesktopMediaQuery`
    font-size: 0.8rem;
  `}
`;
