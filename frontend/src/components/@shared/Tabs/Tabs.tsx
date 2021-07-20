import { useContext, useState } from "react";

import { ThemeContext } from "styled-components";
import { Container, TabIndicator, TabButton, TabButtonWrapper, TabContentWrapper, TabContent } from "./Tabs.style";

export interface Props extends React.HTMLAttributes<HTMLDivElement> {
  tabItems: {
    name: string;
    content: React.ReactNode;
  }[];
  tabIndicatorColor?: string;
}

const Tabs = ({ tabItems, tabIndicatorColor, ...props }: Props) => {
  const { color } = useContext(ThemeContext);
  const [tabIndex, setTabIndex] = useState(0);

  const handleTabIndexChange = (index: number) => {
    setTabIndex(index);
  };

  const tabButtonList = tabItems.map((tabItem, index) => (
    <TabButton
      key={tabItem.name}
      textColor={index === tabIndex ? color.textColor : color.lighterTextColor}
      onClick={() => handleTabIndexChange(index)}
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
      <TabContentWrapper tabIndex={tabIndex} tabCount={tabItems.length}>
        {tabItems.map(({ name, content }) => (
          <TabContent key={name} tabCount={tabItems.length}>
            {content}
          </TabContent>
        ))}
      </TabContentWrapper>
    </Container>
  );
};

export default Tabs;
