const localStorageMock = (() => {
  const store: { [k: string]: string } = {
    username: "",
    accessToken: "",
  };

  return {
    getItem: (key: string) => {
      return store[key] || null;
    },
    setItem: (key: string, value: string) => {
      store[key] = value;
    },
  };
})();

const mockFileReader = jest.fn(function () {
  const self: any = {};
  self.result = null;
  self.error = null;
  self.onload = () => {};

  self.readAsArrayBuffer = function (blob: Blob) {
    self.result = new ArrayBuffer(blob.size);
    self.onload();
  };

  return self;
});

Object.defineProperties(global, {
  localStorage: {
    value: localStorageMock,
  },
  FileReader: {
    value: mockFileReader,
  },
});
