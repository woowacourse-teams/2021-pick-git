import styled, { css, CSSProp } from "styled-components";
import { NoneStyledTextarea } from "../../@styled/layout";
import { customScrollbarCSS } from "../../@styled/scrollbar";

export const TextArea = styled(NoneStyledTextarea)<{ cssProp?: CSSProp; autoGrow: boolean }>(
  ({ theme, cssProp, autoGrow }) => css`
    ${autoGrow
      ? `
      ::-webkit-scrollbar {
        width: 0px;
      }
      `
      : customScrollbarCSS(theme.color.textColor)};

    color: ${theme.color.textColor};
    ${cssProp}
  `
);
