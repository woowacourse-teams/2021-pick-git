import { useRef } from "react";

const useThrottle = (callback: (...args: any[]) => void, delay: number) => {
  const timer = useRef<ReturnType<typeof setTimeout> | null>(null);

  return (...args: any[]) => {
    if (timer.current) return;

    timer.current = setTimeout(() => {
      timer.current = null;
    }, delay);

    callback(...args);
  };
};

export default useThrottle;
