import styled from "styled-components";

export const Container = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;

  width: 100vw;
  min-height: 100vh;
  background-color: ${({ theme }) => theme.color.darkerSecondaryColor};
`;

export const Heading = styled.h1`
  color: ${({ theme }) => theme.color.primaryColor};
  margin: 0;
  font-family: "jua", "Noto Sans KR", sans-serif;
  font-size: 3rem;
`;

export const Inner = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;

  width: 328px;
  height: 589px;
  padding: 5.5rem 1.5rem;
  background-color: ${({ theme }) => theme.color.white};
  border-radius: 4px;
  box-shadow: 6px 7px 20px 5px rgba(0, 0, 0, 0.1);
`;
