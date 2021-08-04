import { TabIndicatorKind } from "../@types";
import { theme } from "../App.style";

export const getTabTextColor = (tabIndicatorKind: TabIndicatorKind, isFocused: boolean) => {
  if (tabIndicatorKind === "line") {
    return isFocused ? theme.color.textColor : theme.color.lighterTextColor;
  }

  return isFocused ? theme.color.white : theme.color.textColor;
};
