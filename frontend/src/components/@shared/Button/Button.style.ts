import styled from "styled-components";

const Button = styled.button<React.CSSProperties>`
  ${({ theme, backgroundColor, color }) => `
    color: ${color ?? theme.color.white};
    background-color: ${backgroundColor ?? theme.color.primaryColor};

  `}

  padding: 0.4375rem 0.875rem;
  font-size: 0.75rem;
  text-align: center;
  transition: opacity 0.5s;

  :hover {
    opacity: 0.5;
  }
`;

export const SquaredInlineButton = styled(Button)`
  display: inline-block;
  border-radius: 4px;
`;

export const SquaredBlockButton = styled(Button)`
  display: block;
  width: 100%;
  border-radius: 4px;
`;

export const RoundedInlineButton = styled(Button)`
  display: inline-block;
  border-radius: 24px;
`;

export const RoundedBlockButton = styled(Button)`
  display: block;
  width: 100%;
  border-radius: 24px;
`;
