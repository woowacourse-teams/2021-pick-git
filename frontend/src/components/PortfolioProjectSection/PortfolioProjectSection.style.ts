import styled, { css } from "styled-components";
import { setDesktopMediaQuery, setLaptopAboveMediaQuery, setLaptopMediaQuery } from "../@styled/mediaQueries";

export const Container = styled.div`
  display: flex;
  width: 100%;
  height: 100%;
  flex-direction: column;
  color: ${({ theme }) => theme.color.textColor};
`;

export const ProjectPeriods = styled.h3`
  text-align: right;
  align-self: flex-end;
  padding: 0 1.09375rem 0.375rem;
  margin: 0;
  font-size: 0.75rem;
  border-bottom: 2px solid ${({ theme }) => theme.color.primaryColor};

  ${setLaptopMediaQuery`
    padding: 0 4.0625rem 0.625rem 3.125rem;
    font-size: 1.3125rem;
  `}

  ${setDesktopMediaQuery`
    padding: 0 6.25rem 0.625rem 5rem;
    font-size: 1.3125rem;
  `}
`;

export const ProjectTypeCSS = css`
  text-align: right;
  align-self: flex-end;
  padding: 0.375rem 0;
  margin: 0 1.09375rem 0 0;
  font-size: 0.75rem;

  ${setLaptopMediaQuery`
    padding: 0.625rem 0;
    margin: 0 4.0625rem 0 0;
    font-size: 1.3125rem;
  `}

  ${setDesktopMediaQuery`
    padding: 0.625rem 0;
    margin: 0 6.25rem 0 0;
    font-size: 1.3125rem;
  `}
`;

export const ProjectType = styled.span(() => ProjectTypeCSS);

export const ProjectDateCSS = css`
  width: 7rem;
  text-align: center;
  background: none;
  border: none;

  ${setLaptopMediaQuery`
    width: 10rem;
  `}

  ${setDesktopMediaQuery`
    width: 12rem;
  `}
`;

export const ProjectDateSeparator = styled.span`
  margin: 0 0.9375rem 0 0.9375rem;

  ${setDesktopMediaQuery`
    margin: 0 0 0 0.9375rem;
  `}
`;

export const ProjectNameCSS = css`
  font-size: 1.5rem;
  padding: 0 1.2rem;
  min-height: 2rem;

  ${setLaptopAboveMediaQuery`
    font-size: 2rem;
    min-height: 2.5rem;
  `}

  ${setLaptopMediaQuery`
    padding-left: 65px;
    margin-bottom: 2rem;
  `}
  ${setDesktopMediaQuery`
    padding-left: 100px;
    margin-bottom: 3rem;
  `}
`;

export const ProjectBody = styled.div`
  display: flex;
  width: 100%;
  flex-direction: column;

  ${setLaptopAboveMediaQuery`
    flex-direction: row;
    justify-content: center;
    align-items: center;
  `}

  ${setLaptopMediaQuery`
    padding: 0 65px;
  `}
  ${setDesktopMediaQuery`
    padding: 0 100px;
  `}
`;

export const ProjectImage = styled.img`
  order: 1;
  width: 100%;
  aspect-ratio: 4 / 3;
  align-self: flex-start;

  ${setLaptopAboveMediaQuery`
    order: 2;
    width: 50%;
  `}
`;

export const ProjectInfo = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 100%;
  order: 2;
  padding: 1rem 1.2rem;

  ${setLaptopAboveMediaQuery`
    order: 1;
    width: 50%;
    padding: 0.5rem 0 0 0;
  `}
`;

export const ProjectContentCSS = css`
  font-size: 0.7rem;
  margin: 0 1.5rem 0 0;
  height: 100%;
  line-height: 1.5rem;
  margin-bottom: 1rem;
  min-height: 10rem;

  color: ${({ theme }) => theme.color.textColor};

  ${setLaptopMediaQuery`
    font-size: 0.8rem;
    margin-right: 2rem;

  `}
  ${setDesktopMediaQuery`
    font-size: 0.9rem;
    margin-right: 3rem;
  `}
`;

export const TagListWrapper = styled.div`
  display: flex;
  flex-wrap: wrap;
`;

export const TagItemCSS = css`
  margin-right: 0.625rem;
  margin-bottom: 0.5625rem;

  :hover {
    transition: opacity 0.5s;
    opacity: 0.7;
  }

  :active {
    opacity: 0.7;
    filter: brightness(1.1);
  }
`;
