import { Story } from "@storybook/react";

import EditPostPage from "./EditPostPage";

export default {
  title: "Pages/EditPostPage",
  component: EditPostPage,
};

const Template: Story = (args) => <EditPostPage {...args} />;

export const Default = Template.bind({});
Default.args = {};
