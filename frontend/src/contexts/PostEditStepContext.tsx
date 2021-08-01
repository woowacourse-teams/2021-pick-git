import { createContext, Dispatch, SetStateAction, useState } from "react";

interface Value {
  stepIndex: number;
  setStepIndex: Dispatch<SetStateAction<number>>;
}

const defaultValue = {
  stepIndex: 0,
  setStepIndex: () => {},
};

const PostEditStepContext = createContext<Value>(defaultValue);

export const PostEditStepContextProvider = ({ children }: { children: React.ReactNode }) => {
  const [stepIndex, setStepIndex] = useState<number>(0);

  return (
    <PostEditStepContext.Provider
      value={{
        stepIndex,
        setStepIndex,
      }}
    >
      {children}
    </PostEditStepContext.Provider>
  );
};

export default PostEditStepContext;
