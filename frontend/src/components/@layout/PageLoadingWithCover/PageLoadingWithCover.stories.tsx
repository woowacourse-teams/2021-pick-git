import { Story } from "@storybook/react";

import PageLoadingWithCover, { Props } from "./PageLoadingWithCover";

export default {
  title: "Components/Layout/PageLoadingWithCover",
  component: PageLoadingWithCover,
};

const Template: Story<Props> = (args) => (
  <div style={{ backgroundColor: "black", position: "relative", height: "100vh" }}>
    <PageLoadingWithCover {...args} />;
  </div>
);

export const Default = Template.bind({});
Default.args = {
  description: "로딩중",
};
