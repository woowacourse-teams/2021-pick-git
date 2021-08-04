import { Link } from "react-router-dom";
import styled from "styled-components";

export const Container = styled.div`
  display: flex;
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

