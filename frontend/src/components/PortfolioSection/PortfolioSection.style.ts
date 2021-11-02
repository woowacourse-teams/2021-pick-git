import styled, { css } from "styled-components";
import { setLaptopAboveMediaQuery } from "../@styled/mediaQueries";

export const Container = styled.div`
  position: relative;
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  color: ${({ theme }) => theme.color.textColor};
  padding: 0 1.2rem;
`;

export const SectionContentWrapper = styled.div`
  display: flex;
`;

export const CategoriesWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  width: 7.25rem;
  height: 100%;
  padding: 2.5rem 1rem 2.5rem 0rem;
  font-size: 0.875rem;
  border-right: 2px solid ${({ theme }) => theme.color.secondaryColor};

  ${setLaptopAboveMediaQuery`
    border-right-width: 3px;
    padding-right: 6.25rem;
    padding: 2.5rem 1rem;
    font-size: 1.5rem;
    width: 31.25rem;
  `}
`;

export const Category = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-end;
`;

export const CategoryDeleteIconWrapper = styled.div`
  margin-top: 0.75rem;
`;

export const CategoryAddIconWrapper = styled.div`
  margin-top: 5rem;
`;

export const CategoryTextareaCSS = css`
  width: 100%;
  height: 1.5rem;
  text-align: right;
  transition: opacity 0.5s;
  line-height: 2rem;

  :focus {
    opacity: 0.7;
  }

  ${setLaptopAboveMediaQuery`
    height: 2.2rem;
  `}
`;

export const DescriptionsWrapper = styled.div(
  () => css`
    width: 100%;
    padding: 2.5rem 0rem 2.5rem 1rem;

    ${setLaptopAboveMediaQuery`
      font-size: 1.125rem;
      padding-left: 6.25rem;
    `}
  `
);

export const Description = styled.div(
  () => css`
    display: flex;
    align-items: center;
    margin-bottom: 0.75rem;
  `
);

export const DescriptionDeleteIconWrapper = styled.div`
  margin-left: 0.75rem;

  ${setLaptopAboveMediaQuery`
    margin-right: 7rem;
  `}
`;

export const DescriptionItemTextareaCSS = css`
  width: 100%;
  transition: opacity 0.5s;
  color: ${({ theme }) => theme.color.lighterTextColor};
  height: 1.5rem;
  line-height: 1.5rem;

  :focus {
    opacity: 0.7;
  }

  ${setLaptopAboveMediaQuery`
    height: 1.8rem;
  `}
`;

export const DescriptionAddIconWrapper = styled.div`
  margin-top: 1rem;
`;
