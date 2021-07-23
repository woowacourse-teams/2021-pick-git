import { Story } from "@storybook/react";

import ProfileFeed, { Props } from "./ProfileFeed";

export default {
  title: "Components/ProfileFeed",
  component: ProfileFeed,
};

const Template: Story<Props> = (args) => <ProfileFeed {...args} />;

export const Default = Template.bind({});
Default.args = {
  allPosts: [
    {
      id: "1",
      imageUrls: [
        "https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
        "https://images.unsplash.com/photo-1503376780353-7e6692767b70?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
        "https://images.unsplash.com/photo-1502161254066-6c74afbf07aa?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1051&q=80",
      ],
      githubRepoUrl: "https://github.com/swon3210",
      content: "안녕 난 크리스, 개발의 왕이지",
      authorName: "Chris",
      profileImageUrl: "https://avatars.githubusercontent.com/u/32982670?v=4",
      likesCount: 24,
      tags: [],
      createdAt: "3일 전",
      updatedAt: "2일 전",
      comments: [
        {
          commentId: "4",
          authorName: "브콜",
          content: "하하하하",
          isLiked: true,
        },
      ],
      isLiked: true,
    },
    {
      id: "2",
      imageUrls: [
        "https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
        "https://images.unsplash.com/photo-1502161254066-6c74afbf07aa?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1051&q=80",
      ],
      githubRepoUrl: "https://github.com/swon3210",
      content: "안녕 난 크리스, 개발의 왕이지",
      authorName: "Chris",
      profileImageUrl: "https://avatars.githubusercontent.com/u/32982670?v=4",
      likesCount: 24,
      tags: [],
      createdAt: "3일 전",
      updatedAt: "2일 전",
      comments: [
        {
          commentId: "5",
          authorName: "브콜",
          content: "하하하하",
          isLiked: true,
        },
      ],
      isLiked: true,
    },
  ],
  isLoading: false,
  isError: false,
  isFetchingNextPage: false,
  handleIntersect: () => {},
};
