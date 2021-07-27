import styled from "styled-components";
import { TabIndicatorKind } from "../../../@types";

export const Container = styled.section`
  width: 100%;
  height: fit-content;
  overflow-x: hidden;
`;

export const TabButtonWrapper = styled.div`
  display: flex;
  position: relative;
  justify-content: space-between;
`;

export const TabButton = styled.button<{
  textColor?: string;
  tabIndicatorKind: TabIndicatorKind;
}>`
  width: 100%;
  flex-grow: 1;
  padding: 0.5rem 0.625rem 0.625rem 0.625rem;
  text-align: center;
  font-weight: 600;
  overflow: hidden;
  transition: background-color 0.5s, opacity 0.5s;

  ${({ theme, textColor, tabIndicatorKind }) => `
    color: ${textColor ? textColor : theme.color.textColor};

    :hover {
      background-color: ${tabIndicatorKind === "line" && "#eee"};
      opacity: ${tabIndicatorKind === "pill" && "0.5"};
    }
  `};
`;

export const TabIndicator = styled.div<{
  tabIndicatorKind: TabIndicatorKind;
  tabIndex: number;
  tabCount: number;
  tabIndicatorColor?: string;
}>`
  position: absolute;
  bottom: 0;
  transition: transform 0.5s;

  ${({ tabIndex, tabCount }) => `
    transform: translateX(${100 * tabIndex}%);
    width: ${100 / tabCount}%;
  `};

  ${({ theme, tabIndicatorKind, tabIndicatorColor }) =>
    tabIndicatorKind === "line"
      ? `
        border-bottom: 2px solid ${tabIndicatorColor ?? theme.color.primaryColor};
        z-index: 100;
      `
      : `
        background-color: ${tabIndicatorColor ?? theme.color.primaryColor};
        border-radius: 24px;
        height: 100%;
        z-index: -1;
      `}
`;
