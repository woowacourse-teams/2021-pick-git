import styled from "styled-components";
import { Layout } from "../../constants/layout";

export const Header = styled.header`
  position: fixed;
  top: 0;
  width: 100%;
  height: ${Layout.HEADER_HEIGHT};
  background-color: ${({ theme }) => theme.color.white};
  z-index: 100;
  border-bottom: 1px solid #cfcfcf;
`;

export const Page = styled.main`
  width: 100%;
  max-width: 500px;
  margin: ${Layout.PAGE_MARGIN_TOP} auto 0;
  background-color: #ffffff;
`;
