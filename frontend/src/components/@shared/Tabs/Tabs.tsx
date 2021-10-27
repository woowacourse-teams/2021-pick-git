import { useEffect, useState } from "react";
import { CSSProp } from "styled-components";

import { TabIndicatorKind, TabItem } from "../../../@types";
import { getTabTextColor } from "../../../utils/tabs";
import { Container, TabIndicator, TabButton, TabButtonWrapper } from "./Tabs.style";

export interface Props extends React.HTMLAttributes<HTMLDivElement> {
  tabItems: TabItem[];
  defaultTabIndex?: number;
  tabIndicatorColor?: string;
  tabIndicatorKind: TabIndicatorKind;
  cssProp?: CSSProp;
}

const Tabs = ({ tabIndicatorKind, defaultTabIndex = 0, tabItems, tabIndicatorColor, ...props }: Props) => {
  const [tabIndex, setTabIndex] = useState(defaultTabIndex);

  const handleTabIndexChange = (index: number, onTabChange: () => void) => {
    setTabIndex(index);
    onTabChange();
  };

  const tabButtonList = tabItems.map((tabItem, index) => (
    <TabButton
      tabIndicatorKind={tabIndicatorKind}
      key={tabItem.name}
      textColor={getTabTextColor(tabIndicatorKind, index === tabIndex)}
      onClick={() => handleTabIndexChange(index, tabItem.onTabChange)}
    >
      {tabItem.name}
    </TabButton>
  ));

  useEffect(() => {
    setTabIndex(defaultTabIndex);
  }, [defaultTabIndex]);

  return (
    <Container {...props}>
      <TabButtonWrapper>
        {tabButtonList}
        <TabIndicator
          tabIndicatorKind={tabIndicatorKind}
          tabIndex={tabIndex}
          tabCount={tabItems.length}
          tabIndicatorColor={tabIndicatorColor}
        />
      </TabButtonWrapper>
    </Container>
  );
};

export default Tabs;
