export const getLastPath = (pathname: string) => {
  const [lastPath] = pathname.split("/").slice(-1);

  return lastPath;
};
