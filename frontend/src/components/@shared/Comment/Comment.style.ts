import { Link } from "react-router-dom";
import styled from "styled-components";

export const Container = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

export const AuthorName = styled(Link)`
  font-weight: bold;
  font-size: 0.75rem;
  margin-right: 0.75rem;
`;

export const Content = styled.span`
  font-size: 0.625rem;
`;

export const LikeIconWrapper = styled.button`
  padding-top: 2px;
  transition: opacity 0.5s;

  :hover {
    opacity: 0.7;
  }
`;
