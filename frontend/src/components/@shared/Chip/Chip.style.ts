import styled from "styled-components";

export const Container = styled.span<React.CSSProperties>`
  ${({ backgroundColor, theme }) => `
      background-color: ${backgroundColor ?? theme.color.tagItemColor};
      color: ${theme.color.white};
    `};
  padding: 0.4375rem 0.9375rem;
  border-radius: 24px;
  display: inline-flex;
  align-items: center;
`;

export const Text = styled.span`
  display: inline-flex;
  align-items: center;
  font-size: 0.875rem;
  font-weight: bold;
`;

export const DeleteButton = styled.button`
  margin-left: 0.625rem;
  display: inline-flex;
  align-items: center;
`;
