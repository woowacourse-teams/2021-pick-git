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
      {
        test: /\.(png|jpg|jpeg|gif)$/i,
        use: {
          loader: "url-loader",
          options: {
            publicPath: "./dist/",
            name: "[name].[ext]?[hash]",
          },
        },
      },
    ],
  },

  devServer: {
    port: 3000,
    historyApiFallback: true,
    hot: true,
    proxy: {
      "/api": {
        target: "http://devapi.pickgit.p-e.kr:8080",
        changeOrigin: true,
        secure: false,
      },
    },
  },

  plugins: [
    new CleanWebpackPlugin(),
    new webpack.ProvidePlugin({
      React: "react",
    }),
    new webpack.DefinePlugin({
      "process.env.NODE_ENV": JSON.stringify(process.env.NODE_ENV),
      "process.env.DEPLOY": JSON.stringify(process.env.DEPLOY),
    }),
    new HTMLWebpackPlugin({
      template: "./public/index.html",
    }),
  ],
};

export default config;
