import styled, { css } from "styled-components";
import { setDesktopMediaQuery, setLaptopMediaQuery } from "../@styled/mediaQueries";

export const Container = styled.div`
  padding: 1.2rem 1.5625rem;
  background-color: ${({ theme }) => theme.color.white};

  h2 {
    font-size: 1rem;
    font-weight: bold;
    color: ${({ theme }) => theme.color.textColor};
  }
`;

export const GithubStatsWrapper = styled.div`
  display: flex;
  justify-content: space-between;
  margin-bottom: 3rem;
`;

export const StatsWrapper = styled.div`
  width: 100%;
  display: flex;
  justify-content: space-between;

  ${setLaptopMediaQuery`
    padding: 0 1rem;
  `}

  ${setDesktopMediaQuery`
    padding: 0 2rem;
  `}
`;

export const Stat = styled.div(
  ({ theme }) => css`
    display: flex;
    flex-direction: column;
    align-items: center;
    font-size: 0.625rem;
    color: ${theme.color.textColor};
    ${setLaptopMediaQuery`
      font-size: 0.75rem;
    `}
    ${setDesktopMediaQuery`
      font-size: 0.9rem;
    `};
  `
);

export const Heading = styled.h2`
  ${setLaptopMediaQuery`
    margin-bottom: 2rem;
  `}

  ${setDesktopMediaQuery`
    margin-bottom: 2rem;
  `}
`;

export const ContributionGraphWrapper = styled.div`
  width: 100%;
  overflow-x: auto;
  display: flex;
  justify-content: center;
  align-items: center;
`;

export const Empty = styled.div`
  width: 100%;
  height: 23.5625rem;

  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
`;

export const ContributionGraph = styled.img`
  width: 100%;
`;
