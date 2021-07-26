import { Story } from "@storybook/react";
import { ChangeEventHandler, useState } from "react";

import TextEditor, { Props } from "./TextEditor";
import { TextEditorWrapper } from "../../../../.storybook/utils/components";

type ContainerProps = Omit<Props, "value" | "onChange">;

const Container = (args: ContainerProps) => {
  const [value, setValue] = useState("");
  const onChange: ChangeEventHandler<HTMLTextAreaElement> = ({ target: { value } }) => setValue(value);

  return (
    <TextEditorWrapper>
      <TextEditor value={value} onChange={onChange} {...args} />
    </TextEditorWrapper>
  );
};

const TransparentContainer = (args: ContainerProps) => {
  const [value, setValue] = useState("");
  const onChange: ChangeEventHandler<HTMLTextAreaElement> = ({ target: { value } }) => setValue(value);

  return <TextEditor value={value} onChange={onChange} {...args} />;
};

export default {
  title: "Components/Shared/TextEditor",
  component: Container,
};

const Template: Story<ContainerProps> = (args) => <Container {...args} />;

const TransparentTemplate: Story<ContainerProps> = (args) => <TransparentContainer {...args} />;

export const Default = Template.bind({});
Default.args = {
  width: "100%",
  height: "200px",
  placeholder: "내용을 입력해주세요",
};

export const AutoGrow = Template.bind({});
AutoGrow.args = {
  width: "100%",
  height: "200px",
  placeholder: "내용을 입력해주세요",
  autoGrow: true,
};

export const Transparent = TransparentTemplate.bind({});
Transparent.args = {
  width: "100%",
  height: "200px",
  placeholder: "내용을 입력해주세요",
  autoGrow: true,
};

export const WithIndicator = Template.bind({});
WithIndicator.args = {
  width: "100%",
  height: "200px",
  placeholder: "내용을 입력해주세요",
  maxLength: 50,
};
