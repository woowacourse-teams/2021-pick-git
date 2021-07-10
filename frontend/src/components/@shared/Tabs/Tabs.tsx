import { useContext } from "react";
import { useState } from "react";
import { ThemeContext } from "styled-components";
import { Container, TabIndicator, TabItem } from "./Tabs.style";

export interface Props extends React.HTMLAttributes<HTMLDivElement> {
  tabItems: string[];
  tabIndicatorColor?: string;
}

const Tabs = ({ tabItems, tabIndicatorColor, ...props }: Props) => {
  const { color } = useContext(ThemeContext);
  const [tabIndex, setTabIndex] = useState(0);

  const handleTabIndexChange = (index: number) => {
    setTabIndex(index);
  };

  const tabButtonList = tabItems.map((tabItem, index) => (
    <TabItem
      textColor={index === tabIndex ? color.textColor : color.lighterTextColor}
      onClick={() => handleTabIndexChange(index)}
    >
      {tabItem}
    </TabItem>
  ));

  return (
    <Container {...props}>
      {tabButtonList}
      <TabIndicator tabIndex={tabIndex} tabCount={tabItems.length} />
    </Container>
  );
};

export default Tabs;
