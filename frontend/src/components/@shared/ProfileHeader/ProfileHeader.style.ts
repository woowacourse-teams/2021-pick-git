import styled, { css } from "styled-components";
import {
  setDesktopMediaQuery,
  setLaptopAboveMediaQuery,
  setLaptopMediaQuery,
  setMobileMediaQuery,
} from "../../@styled/mediaQueries";
import Button from "../Button/Button";
import { Spinner } from "../Loader/Loader.style";

export const Container = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;

  ${setLaptopMediaQuery`
    flex-direction: column;
  `}

  ${setDesktopMediaQuery`
    flex-direction: column;
  `}
`;

export const AvatarWrapper = styled.div`
  min-width: 3.75rem;
  height: 5rem;

  ${setLaptopMediaQuery`
    min-width: 7rem;
    height: 8.25rem;
    margin-bottom: 2.5rem;
  `}

  ${setDesktopMediaQuery`
    min-width: 11rem;
    height: 12.25rem;
    margin-bottom: 3rem;
  `}
`;

export const IndicatorsWrapper = styled.div`
  width: 100%;
  max-width: 22.5rem;
  margin-left: 2.6875rem;

  ${setLaptopMediaQuery`
    margin-left: 0;
  `}

  ${setDesktopMediaQuery`
    margin-left: 0;
  `}
`;

export const Indicators = styled.div`
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.8125rem;
`;

export const ButtonsWrapper = styled.div`
  width: 100%;
  display: flex;
  justify-content: space-between;

  ${setLaptopAboveMediaQuery`
    margin-top: 1.4375rem;
  `}
`;

export const PortfolioButtonCSS = css`
  background-color: ${({ theme }) => theme.color.tertiaryColor};
  margin-left: 0.75rem;
`;

export const ButtonLoader = styled(Button)`
  opacity: 0.6;
  position: relative;
`;

export const ButtonSpinnerWrapper = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  top: 0;
  width: 100%;
  height: 100%;
`;

export const ButtonSpinner = styled(Spinner)``;
