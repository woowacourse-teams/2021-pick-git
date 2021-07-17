import { Story } from "@storybook/react";

import Feed from "./Feed";
import { useHomeFeedPostsQuery } from "../../services/queries";

export default {
  title: "Components/Feed",
  component: Feed,
};

const Template: Story = (args) => {
  const queryResult = useHomeFeedPostsQuery();

  return <Feed {...args} queryResult={queryResult} />;
};

export const Default = Template.bind({});
Default.args = {};
