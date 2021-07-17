export const getTimeDiffFromCurrent = (dateString: string) => {
  const formattedDateString = dateString + (dateString.slice(-1) === "Z" ? "" : "Z");
  const gap = Number(new Date()) - Number(new Date(formattedDateString));

  if (gap < 0) {
    return {
      set: 0,
      min: 0,
      hour: 0,
      day: 0,
    };
  }

  const sec = Math.floor(gap / 1000);
  const min = Math.floor(sec / 60);
  const hour = Math.floor(min / 60);
  const day = Math.floor(hour / 24);

  return { sec, min, hour, day };
};
