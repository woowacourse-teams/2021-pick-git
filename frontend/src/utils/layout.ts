export const getScrollYPosition = (element: Element, container: Element) => {
  return element.getBoundingClientRect().top + container.scrollTop;
};
