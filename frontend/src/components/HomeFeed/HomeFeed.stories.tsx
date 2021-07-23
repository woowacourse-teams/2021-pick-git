import { Story } from "@storybook/react";

import HomeFeed from "./HomeFeed";
import { useHomeFeedPostsQuery } from "../../services/queries";
import PageLoading from "../@layout/PageLoading/PageLoading";

export default {
  title: "Components/HomeFeed",
  component: HomeFeed,
};

const Template: Story = (args) => {
  const { data, isLoading } = useHomeFeedPostsQuery();

  const allPosts = data?.pages?.reduce((acc, postPage) => acc.concat(postPage), []);

  if (isLoading || !allPosts) {
    return <PageLoading />;
  }

  return <HomeFeed {...args} posts={allPosts} />;
};

export const Default = Template.bind({});
Default.args = {};
