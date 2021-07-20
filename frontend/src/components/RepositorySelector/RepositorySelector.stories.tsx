import { Story } from "@storybook/react";

import RepositorySelector from "./RepositorySelector";

export default {
  title: "Components/RepositorySelector",
  component: RepositorySelector,
};

const Template: Story = (args) => <RepositorySelector {...args} />;

export const Default = Template.bind({});
Default.args = {};
