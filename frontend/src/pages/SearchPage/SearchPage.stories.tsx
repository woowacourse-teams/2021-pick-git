import { Story } from "@storybook/react";

import SearchPage from "./SearchPage";

export default {
  title: "Pages/SearchPage",
  component: SearchPage,
};

const Template: Story = (args) => <SearchPage {...args} />;

export const Default = Template.bind({});
Default.args = {};
