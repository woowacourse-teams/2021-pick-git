import { Story } from "@storybook/react";

import Comment, { Props } from "./Comment";

export default {
  title: "Components/Shared/Comment",
  component: Comment,
};

const Template: Story<Props> = (args) => <Comment {...args} />;

export const Default = Template.bind({});
Default.args = {
  authorName: "Tanney",
  content: "개발 너무 재미있어 미치겠어",
};
