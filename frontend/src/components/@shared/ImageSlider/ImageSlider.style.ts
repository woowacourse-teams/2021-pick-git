import styled, { css, CSSProp } from "styled-components";
import { setLaptopAboveMediaQuery } from "../../@styled/mediaQueries";

export const Container = styled.div<React.CSSProperties & { cssProp?: CSSProp }>(
  ({ cssProp }) => css`
    overflow-x: hidden;
    position: relative;
    background-color: white;

    ::-webkit-scrollbar {
      width: 0;
    }

    ${cssProp};
  `
);

export const ImageListSlider = styled.ul<React.CSSProperties>(
  ({ theme, width }) => css`
    width: ${width};
    height: 100%;
    background-color: ${theme.color.postBackgroundColor};
    pointer-events: none;

    display: flex;
    align-items: center;
    transition: transform 0.5s ease-in-out;
  `
);

export const ImageListItem = styled.li<React.CSSProperties>`
  width: 100%;
  height: 100%;
  line-height: 0;
`;

export const Image = styled.img(
  () => css`
    width: 100%;
    height: 100%;
    object-fit: contain;
  `
);

export const ImageView = styled.div<{ imageUrl: string }>`
  width: 100%;
  background-image: url(${({ imageUrl }) => imageUrl});
  background-size: contain;
  background-position: center;
  background-repeat: no-repeat;
`;

export const SlideButton = styled.button<{
  direction: "left" | "right";
}>`
  display: flex;
  justify-content: center;
  align-items: center;

  position: absolute;
  top: 50%;
  ${({ direction }) => `
    ${direction}: 0.25rem;
  `}

  transform: translateY(-50%);

  width: 2.2rem;
  height: 2.2rem;
  border-radius: 50%;
  opacity: 0.4;

  ${({ theme }) => `
    color: ${theme.color.white};
    background-color: ${theme.color.primaryColor};
  `}

  :hover {
    opacity: 0.7;
  }

  :active {
    filter: brightness(1.2);
  }
`;

export const Indicator = styled.div`
  position: absolute;
  left: 50%;
  bottom: 0.8125rem;
  transform: translateX(-50%);

  display: flex;
  justify-content: center;
  width: 2.5rem;
  font-size: 0.6rem;
  line-height: 0.9;
  color: ${({ theme }) => theme.color.white};
  background-color: rgba(0, 0, 0, 0.4);
  padding: 0.3rem;
  border-radius: 10px;
`;
