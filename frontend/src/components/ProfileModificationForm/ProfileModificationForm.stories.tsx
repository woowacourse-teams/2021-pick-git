import { Story } from "@storybook/react";

import ProfileModificationForm, { Props } from "./ProfileModificationForm";

export default {
  title: "Components/ProfileModificationForm",
  component: ProfileModificationForm,
};

const Template: Story<Props> = (args) => <ProfileModificationForm {...args} />;

export const Default = Template.bind({});
Default.args = {
  username: "tanney-102",
  profileImageUrl:
    "https://images.unsplash.com/photo-1518574095400-c75c9b094daa?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=934&q=80",
  prevDescription: "으하하하",
};
