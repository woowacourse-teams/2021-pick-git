import { Story } from "@storybook/react";

import HomeFeedPage, { Props } from "./HomeFeedPage";

export default {
  title: "Pages/HomeFeedPage",
  component: HomeFeedPage,
};

const Template: Story<Props> = (args) => <HomeFeedPage {...args} />;

export const Default = Template.bind({});
Default.args = {};
