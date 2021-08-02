import { Story } from "@storybook/react";

import RepositorySelector from "./RepositorySelector";

export default {
  title: "Components/RepositorySelector",
  component: RepositorySelector,
};

const Template: Story = (args) => (
  <RepositorySelector currentUsername="swon3210" goNextStep={() => {}} setGithubRepositoryName={() => {}} {...args} />
);

export const Default = Template.bind({});
Default.args = {};
