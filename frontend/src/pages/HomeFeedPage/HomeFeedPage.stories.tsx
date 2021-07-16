import { Story } from "@storybook/react";

import HomeFeedPage from "./HomeFeedPage";

export default {
  title: "Pages/HomeFeedPage",
  component: HomeFeedPage,
};

const Template: Story = (args) => <HomeFeedPage {...args} />;

export const Default = Template.bind({});
Default.args = {};
