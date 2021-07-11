import styled from "styled-components";

const Button = styled.button`
  text-align: center;
  transition: opacity 0.5s;
  ${({ theme }) => `
    color: ${theme.color.white};
    background-color: ${theme.color.primaryColor};

    :hover {
      opacity: 0.5;
    }
  `}
`;

export const RoundedBlockButton = styled(Button)`
  display: block;
  width: 100%;
  border-radius: 24px;
  padding: 0.875rem;
`;

export const SquareInlineButton = styled(Button)`
  display: inline-block;
  border-radius: 4px;
  padding: 0.4375rem 0.875rem;
  font-size: 0.75rem;
`;
