import { Story } from "@storybook/react";

import SearchHeader from "./SearchHeader";

export default {
  title: "Components/Layout/SearchHeader",
  component: SearchHeader,
};

const Template: Story = (args) => <SearchHeader {...args} />;

export const Default = Template.bind({});
Default.args = {};
