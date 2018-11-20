const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CleanWebpackPlugin = require('clean-webpack-plugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin');

module.exports = {
	entry: {
		app: './src/index.js'
	},
	devtool: 'inline-source-map',
	module: {
		rules: [
			{
				test: /\.(js|jsx)$/,
				use: [
					{
						loader: 'babel-loader',
						options: {
							babelrc: false,
							presets: [ 'react', ['es2015', {modules: false}] ]
						}
					}
				]
			},
			{
				test: /\.css$/,
				loader: 'style-loader!css-loader'
			},
			{
				test: /\.(jpg|png|svg)$/,
				loader: 'file-loader',
				options: {
				  name: '[path][name].[hash].[ext]',
				  outputPath: 'images/'
				},
			}
		]},
	devServer: {
		proxy: {
			'/api': {
				target: 'http://localhost:8080',
				secure: false
			}
		},
		port: 9000
	},
	resolve: {
		extensions: ['.js', '.jsx', '.css']
	},
	plugins: [
		new CleanWebpackPlugin(['dist']),
		new HtmlWebpackPlugin({
			template: path.resolve(__dirname, 'src/index.html'),
			title: 'Development'
		}),
		new ExtractTextPlugin({
			filename:  'assets/css/[name].[hash].css',
			disable: false,
			allChunks: true
		})
	],
	output: {
		filename: '[name].bundle.js',
		path: path.resolve(__dirname, 'dist'),
		publicPath: '/'
	}
};