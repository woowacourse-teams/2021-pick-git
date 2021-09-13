import { Story } from "@storybook/react";
import { ChangeEventHandler, useState } from "react";

import PostTextEditor, { Props } from "./PostTextEditor";
import { TextEditorWrapper } from "../../../.storybook/utils/components";

type ContainerProps = Omit<Props, "value" | "onChange">;

const Container = (args: ContainerProps) => {
  const [value, setValue] = useState("");
  const onChange: ChangeEventHandler<HTMLTextAreaElement> = ({ target: { value } }) => setValue(value);

  return (
    <TextEditorWrapper>
      <PostTextEditor value={value} onChange={onChange} {...args} />
    </TextEditorWrapper>
  );
};

const TransparentContainer = (args: ContainerProps) => {
  const [value, setValue] = useState("");
  const onChange: ChangeEventHandler<HTMLTextAreaElement> = ({ target: { value } }) => setValue(value);

  return <PostTextEditor value={value} onChange={onChange} {...args} />;
};

export default {
  title: "Components/Shared/PostTextEditor",
  component: Container,
};

const Template: Story<ContainerProps> = (args) => <Container {...args} />;

export const Default = Template.bind({});
Default.args = {};
