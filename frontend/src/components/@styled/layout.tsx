import styled from "styled-components";
import { Layout } from "../../constants/layout";

export const Header = styled.header`
  position: fixed;
  top: 0;
  width: 100%;
  height: ${Layout.HEADER_HEIGHT};
  background-color: ${({ theme }) => theme.color.white};
  z-index: 100;

  @media (min-width: 425px) {
    border-bottom: 1px solid ${({ theme }) => theme.color.borderColor};
  }
`;

export const Page = styled.main`
  width: 100%;
  height: 100%;
  max-width: 425px;
  padding-top: ${Layout.HEADER_HEIGHT};
  margin: 0 auto;

  @media (min-width: 425px) {
    padding-top: ${Layout.PAGE_MARGIN_TOP};
  }
`;
