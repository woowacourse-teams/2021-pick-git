import styled from "styled-components";

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

export const TabButton = styled.button<{ textColor?: string }>`
  width: 100%;
  flex-grow: 1;
  padding: 0.625rem;
  text-align: center;
  font-weight: 600;
  overflow: hidden;
  color: ${({ theme, textColor }) => (textColor ? textColor : theme.color.textColor)};

  transition: background-color 0.5s;
  :hover {
    background-color: #eee;
  }
`;

export const TabIndicator = styled.div<{ tabIndex: number; tabCount: number; tabIndicatorColor?: string }>`
  position: absolute;
  border-bottom: 2px solid ${({ theme, tabIndicatorColor }) => tabIndicatorColor ?? theme.color.primaryColor};
  bottom: 0;
  transition: transform 0.5s;

  ${({ tabIndex, tabCount }) => `
    transform: translateX(${100 * tabIndex}%);
    width: ${100 / tabCount}%;
  `};
`;
