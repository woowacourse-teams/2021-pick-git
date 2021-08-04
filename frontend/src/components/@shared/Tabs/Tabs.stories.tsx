import { Story } from "@storybook/react";

import Tabs, { Props } from "./Tabs";

export default {
  title: "Components/Shared/Tabs",
  component: Tabs,
};

const Template: Story<Props> = (args) => <Tabs {...args} />;

export const Default = Template.bind({});
Default.args = {
  tabItems: [
    {
      name: "포스트",
      onTabChange: () => {},
    },
    {
      name: "Github 통계",
      onTabChange: () => {},
    },
  ],
};
