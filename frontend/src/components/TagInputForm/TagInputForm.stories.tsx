import { Story } from "@storybook/react";

import TagInputForm from "./TagInputForm";

export default {
  title: "Components/TagInputForm",
  component: TagInputForm,
};

const Template: Story = (args) => <TagInputForm githubRepositoryName="test" tags={[]} setTags={() => {}} {...args} />;

export const Default = Template.bind({});
Default.args = {};
