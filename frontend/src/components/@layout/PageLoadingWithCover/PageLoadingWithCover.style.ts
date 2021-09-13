import styled from "styled-components";

export const Container = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;

  ${({ theme }) => `
    background-color: ${theme.color.white};
    color: ${theme.color.tertiaryColor};
  `}
  font-size: 2rem;
  font-family: "jua", "Noto Sans KR", sans-serif;
  opacity: 0.8;
`;
