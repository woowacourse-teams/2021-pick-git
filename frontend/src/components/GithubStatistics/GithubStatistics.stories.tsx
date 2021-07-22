import { Story } from "@storybook/react";

import GithubStatistics from "./GithubStatistics";

export default {
  title: "Components/GithubStatistics",
  component: GithubStatistics,
};

const Template: Story = (args) => <GithubStatistics {...args} />;

export const Default = Template.bind({});
Default.args = {
  username: "tanney-102",
};
