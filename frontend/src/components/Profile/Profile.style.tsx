import styled from "styled-components";
import { setLaptopMediaQuery, setDesktopMediaQuery } from "../@styled/mediaQueries";

export const Container = styled.section`
  padding: 1.4219rem;

  ${setLaptopMediaQuery`
    margin-bottom: 2rem;
  `}

  ${setDesktopMediaQuery`
    margin-bottom: 3rem;
  `}
`;

export const Description = styled.p`
  text-align: left;
  font-size: 0.7rem;
  margin: 1.25rem 0;
  padding: 0.2rem 0.5rem;
  line-height: 1.2rem;
  width: 100%;
  line-break: auto;

  ${({ theme }) => `
    color: ${theme.color.textColor};
    border-left: 3px solid ${theme.color.primaryColor};
  `}

  ${setLaptopMediaQuery`
    margin: 2.5rem 0;
    text-align: center;
    font-size: 0.8rem;
    border: none;
  `}

  ${setDesktopMediaQuery`
    margin: 4rem 0;
    text-align: center;
    font-size: 0.9rem;
    border: none;
  `}
`;

export const DetailInfo = styled.div`
  display: flex;
  align-items: center;
  width: 100%;
  color: ${({ theme }) => theme.color.textColor};
  line-height: 2.5;
  font-size: 0.875rem;

  ${setLaptopMediaQuery`
    font-size: 0.92rem;
    padding: 0 1rem;
  `}

  ${setDesktopMediaQuery`
    font-size: 1rem;
    padding: 0 3rem;
  `}

  svg {
    margin-right: 1rem;
  }
`;
