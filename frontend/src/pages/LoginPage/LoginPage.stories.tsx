import { Story } from "@storybook/react";

import LoginPage from "./LoginPage";

export default {
  title: "Pages/LoginPage",
  component: LoginPage,
};

const Template: Story = () => <LoginPage />;

export const Default = Template.bind({});
Default.args = {};
