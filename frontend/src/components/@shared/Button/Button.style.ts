import styled from "styled-components";

const Button = styled.button<React.CSSProperties>`
  ${({ theme, backgroundColor, color }) => `
    color: ${color ?? theme.color.white};
    background-color: ${backgroundColor ?? theme.color.primaryColor};

  `}

  text-align: center;
  transition: opacity 0.5s;

  :hover {
    opacity: 0.5;
  }
`;

const InlineButton = styled(Button)`
  display: inline-block;
  padding: 0.4375rem 0.875rem;
  font-size: 0.75rem;
`;

const BlockButton = styled(Button)`
  display: block;
  width: 100%;
  padding: 0.875rem;
  font-size: 1rem;
`;

export const SquaredInlineButton = styled(InlineButton)`
  border-radius: 4px;
`;

export const SquaredBlockButton = styled(BlockButton)`
  border-radius: 4px;
`;

export const RoundedInlineButton = styled(InlineButton)`
  border-radius: 24px;
`;

export const RoundedBlockButton = styled(BlockButton)`
  border-radius: 24px;
`;
