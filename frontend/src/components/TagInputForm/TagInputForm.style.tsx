import styled from "styled-components";

export const Container = styled.div`
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 0 1.6875rem;
`;

export const Form = styled.form`
  margin-top: 3rem;
  margin-bottom: 1.8125rem;
`;

export const TagList = styled.ul`
  display: flex;
  flex-wrap: wrap;
  margin: 0;
`;

export const TagListItem = styled.li`
  margin: 0 0.625rem 0.5625rem 0;
`;

export const TextLengthIndicator = styled.div<React.CSSProperties>`
  margin-top: 0.5rem;
  color: ${({ theme }) => theme.color.lighterTextColor};
  font-size: 0.8rem;
  float: right;
  height: 1rem;
`;
