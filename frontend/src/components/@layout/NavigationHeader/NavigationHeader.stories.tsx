import { Story } from "@storybook/react";
import LoggedInWrapper from "../../../../.storybook/utils/LoggedInWrapper";

import NavigationHeader from "./NavigationHeader";

export default {
  title: "Components/Layout/NavigationHeader",
  component: NavigationHeader,
};

const DefaultTemplate: Story = (args) => <NavigationHeader {...args} />;
const LoggedInTemplate: Story = (args) => (
  <LoggedInWrapper>
    <NavigationHeader {...args} />
  </LoggedInWrapper>
);

export const Default = DefaultTemplate.bind({});
Default.args = {};

export const LoggedIn = LoggedInTemplate.bind({});
LoggedIn.args = {};
