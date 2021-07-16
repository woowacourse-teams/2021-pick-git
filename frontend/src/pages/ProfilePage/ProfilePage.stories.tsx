import { Story } from "@storybook/react";
import { MemoryRouter, Route } from "react-router-dom";

import ProfilePage from "./ProfilePage";

export default {
  title: "Pages/ProfilePage",
  component: ProfilePage,
};

const Template: Story = () => <ProfilePage />;

export const Default = Template.bind({});
Default.args = {};
