import { Story } from "@storybook/react";

import HomeFeed from "./HomeFeed";
import { useHomeFeedPostsQuery } from "../../services/queries";
import PageLoading from "../@layout/PageLoading/PageLoading";
import { QUERY } from "../../constants/queries";

export default {
  title: "Components/HomeFeed",
<<<<<<< HEAD:frontend/src/components/HomeFeed/HomeFeed.stories.tsx
  component: HomeFeed,
=======
  component: Feed,
>>>>>>> bb9e4cb646e6b34e858232f43f912f46fd4f9ed5:frontend/src/components/Feed/Feed.stories.tsx
};

const Template: Story = (args) => {
  const { data, isLoading } = useHomeFeedPostsQuery();

  const allPosts = data?.pages?.reduce((acc, postPage) => acc.concat(postPage), []);

  if (isLoading || !allPosts) {
    return <PageLoading />;
  }

<<<<<<< HEAD:frontend/src/components/HomeFeed/HomeFeed.stories.tsx
  return <HomeFeed {...args} posts={allPosts} />;
=======
  return <Feed {...args} posts={allPosts} queryKey={QUERY.GET_HOME_FEED_POSTS} />;
>>>>>>> bb9e4cb646e6b34e858232f43f912f46fd4f9ed5:frontend/src/components/Feed/Feed.stories.tsx
};

export const Default = Template.bind({});
Default.args = {};
