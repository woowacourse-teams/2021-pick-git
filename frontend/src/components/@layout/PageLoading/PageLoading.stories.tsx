import { Story } from "@storybook/react";

import PageLoading, { Props } from "./PageLoading";

export default {
  title: "Components/Shared/PageLoading",
  component: PageLoading,
};

const Template: Story<Props> = (args) => <PageLoading {...args}>깃들다</PageLoading>;

export const Default = Template.bind({});
Default.args = {};
