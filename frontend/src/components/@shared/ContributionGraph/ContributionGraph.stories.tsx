import { Story } from "@storybook/react";

import ContributionGraph, { Props } from "./ContributionGraph";

export default {
  title: "Components/Shared/ContributionGraph",
  component: ContributionGraph,
};

const Template: Story<Props> = (args) => (
  <div style={{ width: "333px", height: "77px" }}>
    <ContributionGraph {...args} />
  </div>
);

export const Default = Template.bind({});
Default.args = {
  contributionItems: [
    "dense",
    "empty",
    "normal",
    "dense",
    "empty",
    "normal",
    "dense",
    "empty",
    "normal",
    "dense",
    "empty",
    "normal",
    "dense",
    "dense",
    "dense",
    "dense",
    "empty",
    "normal",
    "normal",
    "normal",
    "normal",
    "dense",
    "empty",
    "normal",
    "dense",
    "empty",
    "normal",
    "dense",
    "empty",
    "normal",
    "dense",
    "empty",
    "normal",
    "dense",
    "dense",
    "dense",
    "dense",
    "empty",
    "normal",
    "normal",
    "normal",
    "normal",
    "dense",
    "empty",
    "normal",
    "dense",
    "empty",
    "normal",
    "dense",
    "empty",
    "normal",
    "dense",
    "empty",
    "normal",
    "dense",
    "dense",
    "dense",
    "dense",
    "empty",
    "normal",
    "normal",
    "normal",
    "normal",
    "dense",
  ],
};
