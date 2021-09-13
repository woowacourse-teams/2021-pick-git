import styled, { css, CSSProp } from "styled-components";
import { NoneStyledTextarea } from "../../@styled/layout";

export const TextArea = styled(NoneStyledTextarea)<{ cssProp?: CSSProp; autoGrow: boolean }>(
  ({ cssProp, autoGrow }) => css`
    ${cssProp}
    ${autoGrow &&
    `
      ::-webkit-scrollbar {
        width: 0px;
      }
    `};
  `
);
