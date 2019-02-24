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

						// Add direction based on if last pixel is up-left (y - 1)
						if (minimum == energyArray[i - 1][j - 1])
							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, -1);

						// straight up (y)
						else if (minimum == energyArray[i][j - 1])
							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, 0);

						// or up-right (y + 1)
						else
							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, 1);
					}

					// add the minimum value to the energy value of the cell
					// and assign the cumulative value to new 2D array
					pathEnergyDirArray[i][j].setFirst(energyArray[i][j] + minimum);
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

						// Add direction based on if last pixel is up-left (y - 1)
						if (minimum == energyArray[i - 1][j - 1])
							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, -1);

						// straight left (y)
						else if (minimum == energyArray[i - 1][j])
							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, 0);

						// or down-left (y + 1)
						else
							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, 1);
					}

					// add the minimum value to the energy value of the cell
					// and assign the cumulative value to new 2D array
					pathEnergyDirArray[i][j].setFirst(energyArray[i][j] + minimum);
				}
			}
		}
		return pathEnergyDirArray;
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
