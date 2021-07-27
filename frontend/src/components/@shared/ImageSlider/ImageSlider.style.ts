import styled from "styled-components";

export const Container = styled.div<React.CSSProperties>`
  ${({ width }) => `
    width: ${width};
  `}

  overflow: hidden;
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

export const Indicator = styled.div`
  position: absolute;
  left: 50%;
  bottom: 0.8125rem;
  transform: translateX(-50%);

  width: fit-content;
  font-size: 0.75rem;
  line-height: 0.9;
  color: ${({ theme }) => theme.color.white};
  background-color: rgba(0, 0, 0, 0.5);
  padding: 0.3rem 0.8rem;
  border-radius: 10px;
`;
