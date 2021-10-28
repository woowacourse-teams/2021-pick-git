/* eslint-disable @typescript-eslint/no-var-requires */
const path = require("path");
const webpack = require("webpack");
const HTMLWebpackPlugin = require("html-webpack-plugin");
const { CleanWebpackPlugin } = require("clean-webpack-plugin");
const CssMinimizerPlugin = require("css-minimizer-webpack-plugin");
const CompressionPlugin = require("compression-webpack-plugin");
const { BundleAnalyzerPlugin } = require("webpack-bundle-analyzer");
const Dotenv = require("dotenv-webpack");

const isProduction = process.env.NODE_ENV === "production";

module.exports = {
  mode: process.env.NODE_ENV,
  devtool: isProduction ? "hidden-source-map" : "eval",

  entry: "./src/index.tsx",
  output: {
    path: path.resolve("./dist"),
    filename: "[name].[contenthash].js",
    chunkFilename: "[name].[contenthash].js",
  },

  resolve: {
    extensions: [".js", ".jsx", ".ts", ".tsx"],
    alias: {
      process: "process/browser",
      stream: "stream-browserify",
      zlib: "browserify-zlib",
    },
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
        test: /\.(png|jpg|jpeg|gif|ttf)$/i,
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
        target: "http://devapi.pick-git.com:8080",
        changeOrigin: true,
        secure: false,
      },
      "https://djgd6o993rakk.cloudfront.net/image": {
        target: "https://djgd6o993rakk.cloudfront.net",
        changeOrigin: true,
      },
    },
  },

  plugins: [
    new CleanWebpackPlugin(),
    new webpack.ProvidePlugin({
      React: "react",
      process: "process/browser",
      Buffer: ["buffer", "Buffer"],
    }),
    new webpack.DefinePlugin({
      "process.env.NODE_ENV": JSON.stringify(process.env.NODE_ENV),
      "process.env.DEPLOY": JSON.stringify(process.env.DEPLOY),
    }),
    new HTMLWebpackPlugin({
      template: "./public/index.html",
      favicon: "./public/favicon.ico",
    }),
    new BundleAnalyzerPlugin({
      analyzerMode: "static",
      reportFilename: "docs/report.html",
      openAnalyzer: false,
    }),
    new CompressionPlugin({
      test: /\.(js|js\.map)?$/i,
    }),
    new Dotenv(),
  ],
  optimization: {
    minimize: true,
    minimizer: ["...", new CssMinimizerPlugin()],
    splitChunks: {
      cacheGroups: {
        defaultVendors: false,
        vendors: {
          chunks: "all",
          name: "vendors",
          test: /[\\/]node_modules[\\/]/,
          priority: 10,
          reuseExistingChunk: true,
        },
        react: {
          chunks: "all",
          name: "react",
          test: /(?<!node_modules.*)[\\/]node_modules[\\/](react|react-dom|react-router-dom)[\\/]/,
          priority: 40,
        },
      },
    },
  },
};
