import { Story } from "@storybook/react";

import PageLoadingWithLogo from "./PageLoadingWithLogo";

export default {
  title: "Components/Layout/PageLoadingWithLogo",
  component: PageLoadingWithLogo,
};

const Template: Story = () => <PageLoadingWithLogo />;

export const Default = Template.bind({});
