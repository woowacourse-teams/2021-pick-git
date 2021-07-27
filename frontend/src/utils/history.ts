export const getLastPath = (pathname: string) => {
  const [lastPath] = pathname.split("/").slice(-1);

  return lastPath;
};

export const getLastHash = (pathname: string) => {
  const [lastHash] = pathname.split("#").slice(-1);

  return lastHash;
};
