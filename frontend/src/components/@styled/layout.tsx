import styled from "styled-components";
import { Layout } from "../../constants/layout";

export const Header = styled.header`
  position: fixed;
  top: 0;
  width: 100%;
  height: ${Layout.HEADER_HEIGHT};
  background-color: ${({ theme }) => theme.color.white};
  z-index: 100;
`;

export const Page = styled.main`
  padding-top: ${Layout.HEADER_HEIGHT};
`;
