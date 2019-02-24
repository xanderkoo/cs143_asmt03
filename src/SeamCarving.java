import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;

/*

 */

public class SeamCarving {

	/**
	 * Takes in an array of RGB values (Color) corresponding to the inputed image,
	 * and returns an array of energy values for each pixel
	 * 
	 * @param colorArray array of RGB values (Color), corresponding to the image
	 * @return array of energy values for each pixel
	 */
	private static double[][] energyFunction(Color[][] colorArray) {

		// Array of energy values for each pixel
		double[][] energyArray = new double[colorArray.length][colorArray[0].length];

		// Values to store energy values of each pixel for each color channel
		double redEnergy, greenEnergy, blueEnergy;

		// Variables to store the magnitude of the derivatives of each pixel for each
		// color channel
		double ddxRed, ddyRed, ddxGreen, ddyGreen, ddxBlue, ddyBlue;

		/*
		 * Go through entire array, getting partial derivatives for red, green, and blue
		 * color channels for each pixel and entering the average sum of partial
		 * derivatives for each pixel into the energy array
		 */
		for (int i = 0; i < colorArray.length; i++) {
			for (int j = 0; j < colorArray[i].length; j++) {

				// abs(d/dx) on left edge
				if (i == 0) {
					ddxRed = Math.abs(colorArray[i + 1][j].getRed() - colorArray[i][j].getRed());
					ddxGreen = Math.abs(colorArray[i + 1][j].getGreen() - colorArray[i][j].getGreen());
					ddxBlue = Math.abs(colorArray[i + 1][j].getBlue() - colorArray[i][j].getBlue());
				}

				// abs(d/dx) on right edge
				else if (i == colorArray.length - 1) {
					ddxRed = Math.abs(colorArray[i - 1][j].getRed() - colorArray[i][j].getRed());
					ddxGreen = Math.abs(colorArray[i - 1][j].getGreen() - colorArray[i][j].getGreen());
					ddxBlue = Math.abs(colorArray[i - 1][j].getBlue() - colorArray[i][j].getBlue());
				}

				// abs(d/dx) not on a vertical edge
				else {
					ddxRed = Math.abs(colorArray[i - 1][j].getRed() - colorArray[i + 1][j].getRed()) / 2;
					ddxGreen = Math.abs(colorArray[i - 1][j].getGreen() - colorArray[i + 1][j].getGreen()) / 2;
					ddxBlue = Math.abs(colorArray[i - 1][j].getBlue() - colorArray[i + 1][j].getBlue()) / 2;
				}

				// abs(d/dy) on top edge
				if (j == 0) {
					ddyRed = Math.abs(colorArray[i][j + 1].getRed() - colorArray[i][j].getRed());
					ddyGreen = Math.abs(colorArray[i][j + 1].getGreen() - colorArray[i][j].getGreen());
					ddyBlue = Math.abs(colorArray[i][j + 1].getBlue() - colorArray[i][j].getBlue());
				}

				// abs(d/dy) on bottom edge
				else if (j == colorArray[i].length - 1) {
					ddyRed = Math.abs(colorArray[i][j - 1].getRed() - colorArray[i][j].getRed());
					ddyGreen = Math.abs(colorArray[i][j - 1].getGreen() - colorArray[i][j].getGreen());
					ddyBlue = Math.abs(colorArray[i][j - 1].getBlue() - colorArray[i][j].getBlue());
				}

				// abs(d/dy) not on a horizontal edge
				else {
					ddyRed = Math.abs(colorArray[i][j - 1].getRed() - colorArray[i][j + 1].getRed()) / 2;
					ddyGreen = Math.abs(colorArray[i][j - 1].getGreen() - colorArray[i][j + 1].getGreen()) / 2;
					ddyBlue = Math.abs(colorArray[i][j - 1].getBlue() - colorArray[i][j + 1].getBlue()) / 2;
				}

				// sum of the magnitudes of the derivatives for each pixel
				redEnergy = ddxRed + ddyRed;
				greenEnergy = ddxGreen + ddyGreen;
				blueEnergy = ddxBlue + ddyBlue;

				// Average energy function output for each color channel for each pixel
				energyArray[i][j] = (redEnergy + greenEnergy + blueEnergy) / 3;
			}
		}

		return energyArray;
	}

//	private static int[][] findSeam(int[][] energyArray, int k) {
//		int[][] directional = new int[energyArray.length][energyArray[0].length];
//		int[][] energyPath = energyArray;
//
//		for (int i = 1; i < energyPath[0].length; i++) {
//			for (int j = 0; j < energyPath.length; j++) {
//				if (j == 0) {
//					energyPath[i][j] = energyPath[i][j] + Math.min(energyPath[i - 1][j + 1], energyPath[i - 1][j]);
//					if (energyPath[i - 1][j + 1] < energyPath[i - 1][j]) {
//						directional[i][j] = 1;
//					} else {
//						directional[i][j] = 0;
//					}
//				} else if (j == energyPath.length - 1) {
//					energyPath[i][j] = energyPath[i][j] + Math.min(energyPath[i - 1][j - 1], energyPath[i - 1][j]);
//					if (energyPath[i - 1][j - 1] < energyPath[i - 1][j]) {
//						directional[i][j] = -1;
//					} else {
//						directional[i][j] = 0;
//					}
//				} else {
//					energyPath[i][j] = energyPath[i][j] + Math.min(energyPath[i - 1][j - 1],
//							(Math.min(energyPath[i - 1][j], energyPath[i - 1][j + 1])));
//					if (energyPath[i - 1][j] <= energyPath[i - 1][j - 1] && energyPath[i - 1][j] <= energyPath[i - 1][j + 1]) {
//						directional[i][j] = 0;
//					} else if(energyPath)
//				}
//
//			}
//		}
//
//		return energyPath;
//
//	}

	private static int[][] findHorizontalSeam(int[][] energyArray) {
		// width and height of array with energy values
		int width = energyArray.length;
		int height = energyArray[0].length;

		// variable to keep track of minimum energy value of path
		int minimum;

		// 2D array to store path energy values and direction (-1, 0, 1)
		int[][] energyPath = energyArray.clone();
		int[][] directional = new int[width][height];

		// Loops through energy array to find smallest value of the adjacent pixels
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {

				// If leftmost column...
				if (i == 0) {
					// Since these pixels are the beginning of the path, set path energy to the
					// energy of that pixel
					energyPath[i][j] = energyArray[i][j];

					// Direction defaults to 0
					directional[i][j] = 0; // TODO most-left: 0 ?
				}

				// Else...
				else {
					// If in the top row...
					if (j == 0) {

						// Next pixel with the minimum energy is either to the left or down-left
						minimum = Math.min(energyPath[i + 1][j], energyPath[i + 1][j + 1]);

						// Add direction based on if next pixel is left...
						if (minimum == energyPath[i + 1][j]) {
							directional[i][j] = 0;
						}

						// or down-left (y + 1)
						else {
							directional[i][j] = 1;
						}

					}

					// If in the bottom row...
					else if (j == (height - 1)) {

						// Next pixel with the minimum energy is either to the left or up-left
						minimum = Math.min(energyPath[i + 1][j], energyPath[i + 1][j - 1]);

						// Add direction based on if next pixel is left...
						if (minimum == energyPath[i + 1][j]) {
							directional[i][j] = 0;
						}

						// ...or up-left (y - 1)
						else {
							directional[i][j] = -1;
						}
					}

					// If not the bottom or top row...
					else {

						// Next pixel with the minimum energy is to the up-left, left, or down-left
						minimum = Math.min(energyPath[i + 1][j - 1],
								Math.min(energyPath[i + 1][j], energyPath[i + 1][j + 1]));

						// Add direction based on if next pixel is up-left (y - 1)
						if (minimum == energyPath[i + 1][j - 1]) {
							directional[i][j] = -1;
						}

						// straight left (y)
						else if (minimum == energyPath[i + 1][j]) {
							directional[i][j] = 0;
						}

						// or down-left (y + 1)
						else {
							directional[i][j] = 1;
						}
					}
					
					// add the minimum value to the energy value of the cell
					// and assign the cumulative value to new 2D array
					energyPath[i][j] = energyArray[i][j] + minimum;

				}
			}
		}
		return energyPath;
	}

	public static void main(String args[]) throws IOException {
		File file = new File("./image_original.jpg");
		BufferedImage imageSource = ImageIO.read(file);
		int rows = imageSource.getHeight();
		int cols = imageSource.getWidth();

		System.out.printf("%d by %d pixels\n", rows, cols);

		/* Read into an array of rgb values */
		Color image[][] = new Color[cols][rows];
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				int color = imageSource.getRGB(i, j);
				int red = (color >> 16) & 0xff;
				int green = (color >> 8) & 0xff;
				int blue = (color) & 0xff;
				image[i][j] = new Color(red, green, blue);
			}
		}

		/* Save as new image where g values set to 0 */
		BufferedImage imageNew = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);
		File fileNew = new File("./image_energy.jpg");
		double[][] energyArray = energyFunction(image);
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				System.out.print((int) energyArray[i][j] + " ");
				int r = 255 - (int) energyArray[i][j];
				int g = 255 - (int) energyArray[i][j];
				int b = 255 - (int) energyArray[i][j];
				int col = (r << 16) | (g << 8) | b;
				imageNew.setRGB(i, j, col);
			}
			System.out.println();
		}

		ImageIO.write(imageNew, "JPEG", fileNew);
	}
}
