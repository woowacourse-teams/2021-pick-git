import { useRef } from "react";

const useDebounce = <TArg = unknown>(callback: (arg?: TArg) => void, delay: number) => {
  const timer = useRef<ReturnType<typeof setTimeout> | null>();

  const debounce = (arg?: TArg) => {
    if (timer.current) clearTimeout(timer.current);

    timer.current = setTimeout(() => callback(arg), delay);
  };

  return debounce;
};

export default useDebounce;
