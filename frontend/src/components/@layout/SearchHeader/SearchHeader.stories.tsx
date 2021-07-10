import { Story } from "@storybook/react";

import SearchHeader, { Props } from "./SearchHeader";

export default {
  title: "Components/Layout/SearchHeader",
  component: SearchHeader,
};

const Template: Story<Props> = (args) => <SearchHeader {...args} />;

export const Default = Template.bind({});
Default.args = {};
