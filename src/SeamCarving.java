import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;

/**
 * 
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

	/**
	 * Given the energy of each pixel, finds and returns an array of all of the
	 * minimum paths to each pixel from the top to the bottom
	 * 
	 * @param energyArray 2D array of energy values for each pixel
	 * @return 2D array of pairs of (double) minimum cumulative path energy for each
	 *         pixel and (int) direction to the minimum path (-1, 0, 1 for up-left,
	 *         straight, and up-right, respectively)
	 */
	private static SeamFindingPair[][] findVerticalSeam(double[][] energyArray) {
		// width and height of array with energy values
		int width = energyArray.length;
		int height = energyArray[0].length;

		// variable to keep track of minimum energy value of path
		double minimum;

		// 2D array to store path energy values and direction (-1, 0, 1)
		SeamFindingPair[][] pathEnergyDirArray = new SeamFindingPair[width][height];

		// Loops through energy array to find smallest value of the adjacent pixels
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {

				// If topmost row...
				if (j == 0) {
					// Since these pixels are the beginning of the path, set path energy to the
					// energy of that pixel. Direction defaults to 0.
					pathEnergyDirArray[i][j] = new SeamFindingPair(energyArray[i][j], 0);
				}

				// Else...
				else {
					// If in the leftmost column...
					if (i == 0) {

						// Previous pixel with the minimum cumulative path energy is either to the
						// up-right or straight up
						minimum = Math.min(energyArray[i][j - 1], energyArray[i + 1][j - 1]);

						// Add direction based on if last pixel is straight up...
						if (minimum == energyArray[i][j - 1])
							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, 0);

						// or up-right (x + 1)
						else
							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, 1);

						// NOTE: cumulative path energy assigned later
					}

					// If in the rightmost column...
					else if (i == (width - 1)) {

						// Previous pixel with the minimum cumulative path energy is either to the
						// up-left or straight up
						minimum = Math.min(energyArray[i][j - 1], energyArray[i - 1][j - 1]);

						// Add direction based on if last pixel is straight up...
						if (minimum == energyArray[i][j - 1])
							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, 0);

						// or up-left (y - 1)
						else
							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, -1);

						// NOTE: cumulative path energy assigned later
					}

					// If not the bottom or top row...
					else {

						// Previous pixel with the minimum cumulative path energy is to the up-left,
						// straight up, or up-right
						minimum = Math.min(energyArray[i - 1][j - 1],
								Math.min(energyArray[i][j - 1], energyArray[i + 1][j - 1]));

						// Add direction based on if last pixel is straight up (y)
						if (minimum == energyArray[i][j - 1])
							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, 0);

						// up-left (y - 1)
						else if (minimum == energyArray[i - 1][j - 1])
							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, -1);

						// or up-right (y + 1)
						else
							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, 1);
					}

					// add the minimum value to the energy value of the cell
					// and assign the cumulative value to new 2D array
					pathEnergyDirArray[i][j].setCumulPathEnergy(energyArray[i][j] + minimum);
				}
			}
		}
		return pathEnergyDirArray;
	}

	/**
	 * Given the energy of each pixel, finds and returns an array of all of the
	 * minimum paths to each pixel from the left to the right
	 * 
	 * @param energyArray 2D array of energy values for each pixel
	 * @return 2D array of pairs of (double) minimum cumulative path energy for each
	 *         pixel and (int) direction to the minimum path (-1, 0, 1 for up-left,
	 *         straight-left, and down-left, respectively)
	 */
	private static SeamFindingPair[][] findHorizontalSeam(double[][] energyArray) {
		// width and height of array with energy values
		int width = energyArray.length;
		int height = energyArray[0].length;
		
		System.out.println(width + "," + height);

		// variable to keep track of minimum energy value of path
		double minimum;

		// 2D array to store path energy values and direction (-1, 0, 1)
		SeamFindingPair[][] pathEnergyDirArray = new SeamFindingPair[width][height];

		// Loops through energy array to find smallest value of the adjacent pixels
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {

				// If leftmost column...
				if (i == 0) {
					// Since these pixels are the beginning of the path, set path energy to the
					// energy of that pixel. Direction defaults to 0.
					pathEnergyDirArray[i][j] = new SeamFindingPair(energyArray[i][j], 0);
				}

				// Else...
				else {
					// If in the top row...
					if (j == 0) {

						// Previous pixel with the minimum cumulative path energy is either to the left
						// or down-left
						minimum = Math.min(energyArray[i - 1][j], energyArray[i - 1][j + 1]);

						// Add direction based on if last pixel is left...
						if (minimum == energyArray[i - 1][j])
							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, 0);

						// or down-left (y + 1)
						else
							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, 1);

						// NOTE: cumulative path energy assigned later
					}

					// If in the bottom row...
					else if (j == (height - 1)) {

						// Previous pixel with the minimum cumulative path energy is either to the left
						// or up-left
						minimum = Math.min(energyArray[i - 1][j], energyArray[i - 1][j - 1]);

						// Add direction based on if last pixel is left...
						if (minimum == energyArray[i - 1][j])
							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, 0);

						// or up-left (y - 1)
						else
							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, -1);

						// NOTE: cumulative path energy assigned later
					}

					// If not the bottom or top row...
					else {

						// Previous pixel with the minimum cumulative path energy is to the up-left,
						// left, or down-left
						minimum = Math.min(energyArray[i - 1][j - 1],
								Math.min(energyArray[i - 1][j], energyArray[i - 1][j + 1]));

						// Add direction based on if last pixel is straight-left (y)
						if (minimum == energyArray[i - 1][j])
							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, 0);
						// up-left (y-1)
						else if (minimum == energyArray[i - 1][j - 1])
							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, -1);

						// or down-left (y + 1)
						else
							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, 1);
					}

					// add the minimum value to the energy value of the cell
					// and assign the cumulative value to new 2D array
					pathEnergyDirArray[i][j].setCumulPathEnergy(energyArray[i][j] + minimum);

					System.out.print("<" + i + "," + j + ">\t");
				}
				System.out.println();
			}
		}
		return pathEnergyDirArray;
	}
	
//	private static SeamFindingPair[][] findHorizontalSeam(double[][] energyArray) {
//		// width and height of array with energy values
//		int width = energyArray.length;
//		int height = energyArray[0].length;
//
//		// variable to keep track of minimum energy value of path
//		double minimum;
//
//		// 2D array to store path energy values and direction (-1, 0, 1)
//		SeamFindingPair[][] pathEnergyDirArray = new SeamFindingPair[width][height];
//
//		// Loops through energy array to find smallest value of the adjacent pixels
//		for (int i = 0; i < width; i++) {
//			for (int j = 0; j < height; j++) {
//
//				// If leftmost column...
//				if (i == 0) {
//					// Since these pixels are the beginning of the path, set path energy to the
//					// energy of that pixel. Direction defaults to 0.
//					pathEnergyDirArray[i][j] = new SeamFindingPair(energyArray[i][j], 0);
//				}
//
//				// Else...
//				else {
//					// If in the top row...
//					if (j == 0) {
//
//						// Previous pixel with the minimum cumulative path energy is either to the left
//						// or down-left
//						minimum = Math.min(energyArray[i - 1][j], energyArray[i - 1][j + 1]);
//
//						// Add direction based on if last pixel is left...
//						if (minimum == energyArray[i - 1][j])
//							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, 0);
//
//						// or down-left (y + 1)
//						else
//							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, 1);
//
//						// NOTE: cumulative path energy assigned later
//					}
//
//					// If in the bottom row...
//					else if (j == (height - 1)) {
//
//						// Previous pixel with the minimum cumulative path energy is either to the left
//						// or up-left
//						minimum = Math.min(energyArray[i - 1][j], energyArray[i - 1][j - 1]);
//
//						// Add direction based on if last pixel is left...
//						if (minimum == energyArray[i - 1][j])
//							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, 0);
//
//						// or up-left (y - 1)
//						else
//							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, -1);
//
//						// NOTE: cumulative path energy assigned later
//					}
//
//					// If not the bottom or top row...
//					else {
//
//						// Previous pixel with the minimum cumulative path energy is to the up-left,
//						// left, or down-left
//						minimum = Math.min(energyArray[i - 1][j - 1],
//								Math.min(energyArray[i - 1][j], energyArray[i - 1][j + 1]));
//
//						// Add direction based on if last pixel is straight-left (y)
//						if (minimum == energyArray[i - 1][j])
//							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, 0);
//						// up-left (y-1)
//						else if (minimum == energyArray[i - 1][j - 1])
//							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, -1);
//
//						// or down-left (y + 1)
//						else
//							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, 1);
//					}
//
//					// add the minimum value to the energy value of the cell
//					// and assign the cumulative value to new 2D array
//					pathEnergyDirArray[i][j].setCumulPathEnergy(energyArray[i][j] + minimum);
//
//					System.out.print("<" + i + "," + j + ">\t");
//				}
//				System.out.println();
//			}
//		}
//		return pathEnergyDirArray;
//	}


	public static void retraceHorizontal(SeamFindingPair[][] pathArray, BufferedImage image) {

		/*
		 * Find the index in the path array of the minimum energy path to the rightmost
		 * column
		 */
		int minIndex = 0;
		double min = pathArray[pathArray.length - 1][minIndex].getCumulPathEnergy();
		for (int j = 0; j < pathArray[pathArray.length - 1].length; j++) {

			// System.out.println(minIndex + ", " + pathArray[pathArray.length -
			// 1][j].getCumulPathEnergy());
			if (pathArray[pathArray.length - 1][j].getCumulPathEnergy() < min) {
				minIndex = j;
				min = pathArray[pathArray.length - 1][j].getCumulPathEnergy();
			}
		}

		System.out.print("Minimum energy path, horizontal: " + min);

		/*
		 * Trace back that minimum energy path and re-color the corresponding pixels in
		 * the image
		 */

		// Keep track of direction (-1, 0, 1 for up-left, straight-left, down-left) of
		// next pixel and next index
		int nextDir = pathArray[pathArray.length - 1][minIndex].getDirection();
		int nextIndex = minIndex;

		// Trace the path back and color the seam red
		for (int i = pathArray.length; i > 0; i--) {
			System.out.println();

			// Set pixel red
			image.setRGB(i - 1, nextIndex, Color.RED.getRGB());

			// Update index of next pixel based on the direction
			nextIndex += nextDir;

			// Update direction to go to next
			nextDir = pathArray[i - 1][nextIndex].getDirection();
			System.out.print((int) pathArray[i - 1][nextIndex].getCumulPathEnergy());
		}

		// TODO: i am going to die
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

		double[][] energyArray = energyFunction(image);

		/* Save an image of the energy in grayscale */
		BufferedImage imageEnergy = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);
		File fileEnergy = new File("./image_energy.png");
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				int r = 255 - (int) energyArray[i][j];
				int g = 255 - (int) energyArray[i][j];
				int b = 255 - (int) energyArray[i][j];
				int col = (r << 16) | (g << 8) | b;
				imageEnergy.setRGB(i, j, col);
			}
		}
		ImageIO.write(imageEnergy, "PNG", fileEnergy);

		/* Save an image of the image with a seam */
		BufferedImage imageSeam = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);
		File fileSeam = new File("./image_seam.png");
		// Copies the energy representation of the photo into the buffered image
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				imageSeam.setRGB(i, j, imageEnergy.getRGB(i, j));
			}
		}
		// Draws the seam
		SeamFindingPair[][] pathArrayHoriz = findHorizontalSeam(energyArray);
		retraceHorizontal(pathArrayHoriz, imageSeam);

		System.out.println();

		// Print path/dir array
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				System.out.print(pathArrayHoriz[i][j] + "\t");
			}
			System.out.println();
		}

		ImageIO.write(imageSeam, "PNG", fileSeam);

	}
}
