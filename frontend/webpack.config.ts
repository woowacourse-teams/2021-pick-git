import path from "path";
import webpack from "webpack";
import HTMLWebpackPlugin from "html-webpack-plugin";
import { CleanWebpackPlugin } from "clean-webpack-plugin";
import CssMinimizerPlugin from "css-minimizer-webpack-plugin";
import CompressionPlugin from "compression-webpack-plugin";
import { BundleAnalyzerPlugin } from "webpack-bundle-analyzer";

const isProduction = process.env.NODE_ENV === "production";

const config = {
  mode: process.env.NODE_ENV,
  devtool: isProduction ? "hidden-source-map" : "eval",

  entry: "./src/index.tsx",
  output: {
    path: path.resolve("./dist"),
    filename: "[name].[contenthash].js",
    chunkFilename: "[id].[contenthash].js",
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
        target: "http://localhost:8080",
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
  ],
  optimization: {
    minimize: true,
    minimizer: ["...", new CssMinimizerPlugin()],
    splitChunks: {
      cacheGroups: {
        default: false,
        vendors: false,
        defaultVendors: false,
        asyncVendors: {
          chunks: "async",
          priority: 40,
        },
        react: {
          chunks: "all",
          name: "react",
          test: /(?<!node_modules.*)[\\/]node_modules[\\/](react|react-dom|react-router-dom)[\\/]/,
          priority: 30,
        },
        duplicates: {
          name: "duplicates",
          minChunks: 2,
          priority: 20,
        },
        commonVendors: {
          chunks: "all",
          name: "commonVendors",
          test: /[\\/]node_modules[\\/]/,
          minChunks: 1,
          priority: 10,
        },
      },
    },
  },
};

export default config;
