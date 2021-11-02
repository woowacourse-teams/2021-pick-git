import { Story } from "@storybook/react";

import Feed from "./Feed";
import PageLoading from "../@layout/PageLoading/PageLoading";
import { QUERY } from "../../constants/queries";
import { useHomeFeedAllPostsQuery } from "../../services/queries";

export default {
  title: "Components/Feed",
  component: Feed,
};

const Template: Story = (args) => {
  const { data: infinitePostsData, isLoading } = useHomeFeedAllPostsQuery();

  if (isLoading || !infinitePostsData) {
    return <PageLoading />;
  }

  return (
    <Feed
      {...args}
      infinitePostsData={infinitePostsData}
      onIntersect={() => {}}
      queryKeyList={[QUERY.GET_HOME_FEED_POSTS("all")]}
      isFetching={false}
    />
  );
};

export const Default = Template.bind({});
Default.args = {};
