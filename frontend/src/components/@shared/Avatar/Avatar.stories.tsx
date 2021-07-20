import { Story } from "@storybook/react";

import Avatar, { Props } from "./Avatar";

export default {
  title: "Components/Shared/Avatar",
  component: Avatar,
};

const Template: Story<Props> = (args) => <Avatar {...args} />;

export const Default = Template.bind({});
Default.args = {
  diameter: "3rem",
  fontSize: "0.75rem",
  imageUrl:
    "https://images.unsplash.com/photo-1518574095400-c75c9b094daa?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=934&q=80",
  name: "깃들다계정",
};

export const NoName = Template.bind({});
NoName.args = {
  diameter: "3rem",
  imageUrl:
    "https://images.unsplash.com/photo-1518574095400-c75c9b094daa?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=934&q=80",
};

export const NoProfileImage = Template.bind({});
NoProfileImage.args = {
  diameter: "3rem",
};
