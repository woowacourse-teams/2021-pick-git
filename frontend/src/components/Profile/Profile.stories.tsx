import { Story } from "@storybook/react";
import { useContext, useEffect } from "react";
import UserContext, { UserContextProvider } from "../../contexts/UserContext";

import Profile, { Props } from "./Profile";

export default {
  title: "Components/Profile",
  component: Profile,
};

const LoggedInWrapper = ({ children }: { children: React.ReactElement }) => {
  const Inner = () => {
    const { login } = useContext(UserContext);

    useEffect(() => login("test", "Tanney"), []);

    return <></>;
  };

  return (
    <UserContextProvider>
      <Inner />
      {children}
    </UserContextProvider>
  );
};

const Template: Story<Props> = (args) => (
  <LoggedInWrapper>
    <Profile {...args} />
  </LoggedInWrapper>
);

export const Default = Template.bind({});
Default.args = {
  isMyProfile: false,
  userName: "Chris",
};

export const ProfileMe = Template.bind({});
ProfileMe.args = {
  isMyProfile: true,
  userName: "Tanney",
};
