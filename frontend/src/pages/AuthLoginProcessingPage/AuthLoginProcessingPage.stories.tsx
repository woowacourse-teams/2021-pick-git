import { Story } from "@storybook/react";

import AuthLoginProcessingPage from "./AuthLoginProcessingPage";

export default {
  title: "Pages/AuthLoginProcessingPage",
  component: AuthLoginProcessingPage,
};

const Template: Story = () => <AuthLoginProcessingPage />;

export const Default = Template.bind({});
Default.args = {};
