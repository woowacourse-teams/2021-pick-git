export const getTimeDiffFromCurrent = (dateString: string) => {
  const gap = Number(new Date()) - Number(new Date(dateString));

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

const convertDateNumberToString = (dateNumber: number) => {
  if (dateNumber < 10) {
    return `0${dateNumber}`;
  }

  return String(dateNumber);
};

export const getDateString = (dateObj: Date) => {
  const fullYear = dateObj.getFullYear();
  const month = convertDateNumberToString(dateObj.getMonth() + 1);
  const date = convertDateNumberToString(dateObj.getDate());
  const hours = convertDateNumberToString(dateObj.getHours());
  const minutes = convertDateNumberToString(dateObj.getMinutes());
  const seconds = convertDateNumberToString(dateObj.getSeconds());

  return `${fullYear}-${month}-${date}T${hours}:${minutes}:${seconds}`;
};
