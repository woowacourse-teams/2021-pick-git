import { useState } from "react";

import { TabIndicatorKind, TabItem } from "../../../@types";
import { getTabTextColor } from "../../../utils/tabs";
import { Container, TabIndicator, TabButton, TabButtonWrapper } from "./Tabs.style";

export interface Props extends React.HTMLAttributes<HTMLDivElement> {
  tabItems: TabItem[];
  tabIndicatorColor?: string;
  tabIndicatorKind: TabIndicatorKind;
}

const Tabs = ({ tabIndicatorKind, tabItems, tabIndicatorColor, ...props }: Props) => {
  const [tabIndex, setTabIndex] = useState(0);

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
