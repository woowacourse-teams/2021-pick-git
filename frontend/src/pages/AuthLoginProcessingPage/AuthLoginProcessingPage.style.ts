import styled from "styled-components";
import { Page } from "../../components/@styled/layout";

export const Container = styled(Page)`
  display: flex;
  justify-content: center;
  align-items: center;

  min-height: 100vh;
`;

export const Text = styled.p`
  margin-left: 1rem;
  font-size: 3.5rem;
  font-family: "jua", "Noto Sans KR", sans-serif;
  color: ${({ theme }) => theme.color.lighterTextColor};
`;

export const DotWrapper = styled.div`
  width: 3.5rem;
`;

export const Dot = styled.span`
  display: inline-block;
  width: 0.6rem;
  height: 0.6rem;
  margin-left: 0.4rem;
  border-radius: 50%;
  background-color: ${({ theme }) => theme.color.lighterTextColor};
  position: relative;
  bottom: -10px;
`;
