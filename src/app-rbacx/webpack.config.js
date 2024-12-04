const path = require("path");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const LoadablePlugin = require("@loadable/webpack-plugin");
const TerserPlugin = require("terser-webpack-plugin");
const BundleAnalyzerPlugin =
  require("webpack-bundle-analyzer").BundleAnalyzerPlugin;
const CssMinimizerPlugin = require("css-minimizer-webpack-plugin");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");

var config = {
  module: {},
};

const returnAll = (watchFile) => {
  var allPages = Object.assign({}, config, {
    entry: ["./src/index.js"],
    watch: watchFile,
    output: {
      path: path.join(__dirname, "/../main/resources/static/build/"),
      filename: "[name].js",
      publicPath: "/build/",
      chunkFilename: "[name].chunk.js",
    },
    module: {
      rules: [
        {
          test: /\.js$|jsx/, // files ending with .js or .jsx
          exclude: /node_modules/, // exclude the node_modules directory
          loader: "babel-loader", // use this (babel-core) loader
        },
        {
          test: /\.module\.css$/, // Target .module.css files
          use: [
            "style-loader", // Injects styles into the DOM
            {
              loader: "css-loader", // Handles the CSS imports and URL resolution
              options: {
                modules: {
                  localIdentName: "[path][name]__[local]--[hash:base64:5]",
                },
              },
            },
          ],
        },  
        {
          test: /\.(png|jpe?g|gif)$/i,
          use: [
            {
              loader: 'file-loader', // handle images
              options: {
                name: '[path][name].[ext]',
              },
            },
          ],
        },      
        {
          test: /\.css$/, // Target .css files
          exclude: /\.module\.css$/, // Exclude .module.css files
          use: [
            "style-loader",
            {
              loader: "css-loader",
              options: {
                importLoaders: 1,
                modules: true,
              },
            },
          ],
        },
      ],
    },
    resolve: {
      extensions: [".js", ".jsx"], // Make sure .jsx is included
    },
    optimization: {
      splitChunks: {
        chunks: "all",
        minChunks: 3,
      },
      minimize: true, // Ensure minimization is enabled
      minimizer: [
        new TerserPlugin({
          extractComments: false, // Disable license comment extraction
        }),
        new CssMinimizerPlugin(), // Minify CSS
      ],
    },
    plugins: [
      new HtmlWebpackPlugin({
        template: "./public/index.html",
        filename: "../../templates/index.html",
      }),
      new LoadablePlugin(),
      new BundleAnalyzerPlugin({
        // This will generate a `report.html` file in the output directory
        analyzerMode: "static",
        reportFilename: "report.html",
        openAnalyzer: false, // Set to true if you want to open the report automatically in the browser
      }),
      new MiniCssExtractPlugin({
        filename: "styles.css",
      }),
    ],
  });
  return [allPages];
};
// Return Array of Configurations
module.exports = (env, argv) => {
  let watchFile = false;
  if (argv.mode === "development") {
    watchFile = true;
  }
  return returnAll(watchFile);
};
