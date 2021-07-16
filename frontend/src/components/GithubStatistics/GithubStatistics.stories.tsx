import { Story } from "@storybook/react";

import GithubStatistics, { Props } from "./GithubStatistics";

export default {
  title: "Components/GithubStatistics",
  component: GithubStatistics,
};

const Template: Story<Props> = (args) => <GithubStatistics {...args} />;

export const Default = Template.bind({});
Default.args = {
  userName: "tanney-102",
};
