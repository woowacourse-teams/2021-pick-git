import styled, { css, CSSProp } from "styled-components";

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

const InlineButton = styled(Button)<React.CSSProperties>`
  display: inline-block;
  padding: ${({ padding }) => `${padding ?? "0.4375rem 0.875rem"}`};
  font-size: 0.75rem;
`;

const BlockButton = styled(Button)`
  display: block;
  width: 100%;
  padding: ${({ padding }) => `${padding ?? "0.5rem"}`};
  font-size: 1rem;
`;

export const SquaredInlineButton = styled(InlineButton)<{ cssProp?: CSSProp }>(
  ({ cssProp }) => css`
    border-radius: 4px;
    ${cssProp}
  `
);

export const SquaredBlockButton = styled(BlockButton)<{ cssProp?: CSSProp }>(
  ({ cssProp }) => css`
    border-radius: 4px;
    ${cssProp}
  `
);

export const RoundedInlineButton = styled(InlineButton)<{ cssProp?: CSSProp }>(
  ({ cssProp }) => css`
    border-radius: 24px;
    ${cssProp}
  `
);

export const RoundedBlockButton = styled(BlockButton)<{ cssProp?: CSSProp }>(
  ({ cssProp }) => css`
    border-radius: 24px;
    ${cssProp}
  `
);
