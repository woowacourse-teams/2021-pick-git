import styled from "styled-components";
import { LAYOUT, Z_INDEX } from "../../constants/layout";

export const Header = styled.header`
  position: fixed;
  top: 0;
  width: 100%;
  height: ${LAYOUT.HEADER_HEIGHT};
  background-color: ${({ theme }) => theme.color.white};
  z-index: ${Z_INDEX.LOWER};

  @media (min-width: 425px) {
    border-bottom: 1px solid ${({ theme }) => theme.color.borderColor};
  }
`;

export const Page = styled.main`
  width: 100%;
  height: fit-content;
  max-width: 425px;
  padding-top: ${LAYOUT.HEADER_HEIGHT};
  margin: 0 auto;

  @media (min-width: 425px) {
    padding-top: ${LAYOUT.PAGE_MARGIN_TOP};
  }
`;
