import styled from "styled-components";

export const Container = styled.section`
  width: 100%;
  overflow: hidden;
`;

export const TabButtonWrapper = styled.div`
  display: flex;
  position: relative;
  justify-content: space-between;
`;

export const TabButton = styled.a<{ textColor?: string }>`
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
  z-index: 100;
  transition: transform 0.5s;

  ${({ tabIndex, tabCount }) => `
    transform: translateX(${100 * tabIndex}%);
    width: ${100 / tabCount}%;
  `};
`;

export const TabContentWrapper = styled.div<{ tabIndex: number; tabCount: number }>`
  ${({ tabCount, tabIndex }) => `
    width: ${tabCount * 100}%;
    transform: translateX(-${(100 / tabCount) * tabIndex}%);
  `}

  display: flex;
  padding-top: 0.3rem;
  transition: transform 0.5s;
`;

export const TabContent = styled.div<{ tabCount: number }>`
  width: ${({ tabCount }) => 100 / tabCount}%;
`;
