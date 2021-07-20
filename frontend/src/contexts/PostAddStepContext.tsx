import { createContext, Dispatch, SetStateAction, useState } from "react";

interface Value {
  stepIndex: number;
  setStepIndex: Dispatch<SetStateAction<number>>;
}

const defaultValue = {
  stepIndex: 0,
  setStepIndex: () => {},
};

const PostAddStepContext = createContext<Value>(defaultValue);

export const PostAddStepContextProvider = ({ children }: { children: React.ReactNode }) => {
  const [stepIndex, setStepIndex] = useState<number>(0);

  return (
    <PostAddStepContext.Provider
      value={{
        stepIndex,
        setStepIndex,
      }}
    >
      {children}
    </PostAddStepContext.Provider>
  );
};

export default PostAddStepContext;
