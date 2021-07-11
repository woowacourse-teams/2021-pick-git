import styled from "styled-components";

export const Container = styled.div<React.CSSProperties>`
  ${({ width }) => `
    width: ${width};
  `}

  overflow-x: hidden;
  position: relative;
`;

export const ImageListSlider = styled.ul<React.CSSProperties>`
  ${({ width }) => `
    width: ${width};
  `}
  height: fit-content;
  background-color: #000000;

  display: flex;
  align-items: center;
  transition: transform 0.5s ease-in-out;
`;

export const ImageList = styled.li<React.CSSProperties>`
  width: 100%;

  img {
    width: 100%;
  }
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
