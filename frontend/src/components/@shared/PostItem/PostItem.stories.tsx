import { Story } from "@storybook/react";

import PostItem, { Props } from "./PostItem";

export default {
  title: "Components/Shared/PostItem",
  component: PostItem,
};

const Template: Story<Props> = (args) => <PostItem {...args}>깃들다</PostItem>;

export const Default = Template.bind({});
Default.args = {
  post: {
    id: 0,
    githubRepoUrl: "https://github.com/Tanney-102",
    authorName: "Tanney102",
    profileImageUrl: "https://avatars.githubusercontent.com/u/57767891?v=4",
    imageUrls: [
      "https://images.unsplash.com/photo-1587620962725-abab7fe55159?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1489&q=80",
      "https://images.unsplash.com/photo-1534665482403-a909d0d97c67?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
      "https://images.unsplash.com/photo-1521185496955-15097b20c5fe?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=947&q=80",
    ],
    content:
      "가는 그들에게 너의 설산에서 것은 곳으로 힘차게 그러므로 사막이다. 피어나는 않는 영원히 우리의 용기가 풍부하게 교향악이다. 예가 황금시대를 열락의 같이, 있다. 소금이라 피가 황금시대의 때에, 것이다. 생생하며, 원질이 찬미를 주는 고동을 튼튼하며, 그들은 이상의 피다. 살았으며, 행복스럽고 방황하였으며, 쓸쓸하랴? 가는 생생하며, 되는 싸인 불러 인간에 소금이라 방황하여도, 사랑의 힘있다. 인도하겠다는 대한 청춘을 봄바람이다. 불어 끓는 쓸쓸한 거친 무엇을 내려온 장식하는 것이다. 열매를 끓는 끓는 착목한는 쓸쓸한 봄바람이다. 원대하고, 사랑의 살 부패를 없으면, 그들은 이것을 그들의 고동을 것이다. 타오르고 공자는 천고에 미묘한 듣기만 것이다. 것은 이상을 피어나기 피다. 방황하였으며, 돋고, 이상은 이상 귀는 길지 내는 발휘하기 풍부하게 아니다. 끝에 그러므로 밝은 하였으며, 것이다. 옷을 공자는 있으며, 놀이 이상을 끓는다. 새 그들은 청춘이 없는 어디 사는가 힘차게 위하여, 있다. 원질이 오아이스도 그들의 철환하였는가? 끓는 만천하의 듣기만 없으면 이상의 노래하며 이것을 것이다. 현저하게 살았으며, 대한 같지 아니다.",
    comments: [
      {
        id: 1,
        profileImageUrl:
          "https://images.unsplash.com/photo-1543610892-0b1f7e6d8ac1?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=634&q=80",
        authorName: "swon3210",
        content: "이게 댓글이지",
        liked: true,
      },
      {
        id: 2,
        profileImageUrl:
          "https://images.unsplash.com/photo-1543610892-0b1f7e6d8ac1?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=634&q=80",
        authorName: "swon3210",
        content: "이게 댓글이지",
        liked: true,
      },
    ],
    tags: [],
    createdAt: "3일 전",
    updatedAt: "3일 전",
    liked: true,
    likesCount: 32,
  },
  isEditable: true,
};
