import path from "path";
import webpack from "webpack";
import HTMLWebpackPlugin from "html-webpack-plugin";
import { CleanWebpackPlugin } from "clean-webpack-plugin";

const isProduction = process.env.NODE_ENV === "production";

const config = {
  mode: isProduction ? "production" : "development",
  devtool: isProduction ? "hidden-source-map" : "eval",

  entry: "./src/index.tsx",
  output: {
    path: path.resolve("./dist"),
    filename: "bundle.js",
  },

  resolve: {
    extensions: [".js", ".jsx", ".ts", ".tsx"],
  },

  module: {
    rules: [
      {
        test: /\.tsx?$/,
        exclude: /node_modules/,
        use: ["babel-loader", "ts-loader"],
      },
      {
        test: /\.svg$/,
        use: ["@svgr/webpack", "url-loader"],
      },
    ],
  },

  devServer: {
    port: 3000,
    historyApiFallback: true,
    hot: true,
  },

  plugins: [
    new CleanWebpackPlugin(),
    new webpack.ProvidePlugin({
      React: "react",
    }),
    new HTMLWebpackPlugin({
      template: "./public/index.html", // 아까는 ./src/index.html 이었음
    }),
  ],
};

export default config;
