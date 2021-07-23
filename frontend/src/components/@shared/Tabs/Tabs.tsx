import { useContext, useState } from "react";

import { ThemeContext } from "styled-components";
import { TabItem } from "../../../@types";
import { Container, TabIndicator, TabButton, TabButtonWrapper } from "./Tabs.style";

export interface Props extends React.HTMLAttributes<HTMLDivElement> {
  tabItems: TabItem[];
  tabIndicatorColor?: string;
}

const Tabs = ({ tabItems, tabIndicatorColor, ...props }: Props) => {
  const { color } = useContext(ThemeContext);
  const [tabIndex, setTabIndex] = useState(0);

  const handleTabIndexChange = (index: number, onTabChange: () => void) => {
    setTabIndex(index);
    onTabChange();
  };

  const tabButtonList = tabItems.map((tabItem, index) => (
    <TabButton
      key={tabItem.name}
      textColor={index === tabIndex ? color.textColor : color.lighterTextColor}
      onClick={() => handleTabIndexChange(index, tabItem.onTabChange)}
    >
      {tabItem.name}
    </TabButton>
  ));

  return (
    <Container {...props}>
      <TabButtonWrapper>
        {tabButtonList}
        <TabIndicator tabIndex={tabIndex} tabCount={tabItems.length} tabIndicatorColor={tabIndicatorColor} />
      </TabButtonWrapper>
    </Container>
  );
};

export default Tabs;
