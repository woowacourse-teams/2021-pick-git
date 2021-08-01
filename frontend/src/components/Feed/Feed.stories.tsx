import { Story } from "@storybook/react";

import Feed from "./Feed";
import { useHomeFeedPostsQuery } from "../../services/queries";
import PageLoading from "../@layout/PageLoading/PageLoading";
import { QUERY } from "../../constants/queries";

export default {
  title: "Components/Feed",
  component: Feed,
};

const Template: Story = (args) => {
  const { data, isLoading } = useHomeFeedPostsQuery();

  const allPosts = data?.pages?.reduce((acc, postPage) => acc.concat(postPage), []);

  if (isLoading || !allPosts) {
    return <PageLoading />;
  }

  return <Feed {...args} posts={allPosts} queryKey={QUERY.GET_HOME_FEED_POSTS} />;
};

export const Default = Template.bind({});
Default.args = {};
