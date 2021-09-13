import styled from "styled-components";
import Button from "../../components/@shared/Button/Button";
import { Page } from "../../components/@styled/layout";
import { setLaptopMediaQuery, setDesktopMediaQuery, setTabletMediaQuery } from "../../components/@styled/mediaQueries";

export const Container = styled(Page)`
  display: flex;
  justify-content: center;
  align-items: center;
  position: fixed;
  padding-top: 0;
  width: 100%;
  height: 100%;
  background-color: ${({ theme }) => theme.color.darkerSecondaryColor};
`;

export const Heading = styled.h1`
  color: ${({ theme }) => theme.color.primaryColor};
  margin: 0;
  font-family: "Jua", "Noto Sans KR", sans-serif;
  font-size: 3rem;
`;

export const Inner = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  height: 589px;
  width: 328px;
  padding: 5.5rem 1.5rem;

  ${setTabletMediaQuery`
    width:368px;
  `}

  ${setLaptopMediaQuery`
    width:528px;
    padding: 5.5rem 3.5rem;
  `}

  ${setDesktopMediaQuery`
    width:568px;
    padding: 5.5rem 3.5rem;
  `}

  background-color: ${({ theme }) => theme.color.white};
  border-radius: 4px;
  box-shadow: 6px 7px 20px 5px rgba(0, 0, 0, 0.1);
`;

export const HomeLinkText = styled.span`
  font-size: 0.9rem;
  color: ${({ theme }) => theme.color.darkBorderColor};
`;

export const ButtonLoader = styled(Button)`
  height: 2.86rem;
  opacity: 0.6;
  position: relative;
`;

export const ButtonSpinnerWrapper = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
`;
