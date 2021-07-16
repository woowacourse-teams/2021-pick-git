import styled from "styled-components";
import { Layout } from "../../constants/layout";

export const Header = styled.header`
  position: fixed;
  top: 0;
  width: 100%;
  height: ${Layout.HEADER_HEIGHT};
  background-color: ${({ theme }) => theme.color.white};
  z-index: 100;

  @media (min-width: 375px) {
    border-bottom: 1px solid ${({ theme }) => theme.color.borderColor};
  }
`;

export const Page = styled.main`
  width: 100%;
  max-width: 375px;
  background-color: ${({ theme }) => theme.color.white};
  margin: ${Layout.HEADER_HEIGHT} auto 0;

  @media (min-width: 375px) {
    margin: ${Layout.PAGE_MARGIN_TOP} auto 0;
  }
`;
