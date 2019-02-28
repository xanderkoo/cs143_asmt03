import java.io.*;
import java.util.Random;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;

/**
 * Runs a seam-carving algorithm for resizing (shrinking) images in the main
 * method. Allows for batch resizing. Outputs energy map, energy map with
 * vertical and horizontal seam, and resized image as .png files.
 * 
 * To run the method, place image files into the project folder named
 * "image0.jpg", "image1.jpg", "image2.jpg", etc. for however many images you
 * want to shrink. Make sure the images you want to resize are named in
 * sequential order from 0. Then run SeamCarving.java as a Java application.
 * 
 * The main method takes three arguments: vertical shrinkage in pixels [0,
 * height), horizontal shrinkage in pixels [0, width), and number of images to
 * resize.
 * 
 * @author aden-siebel
 * @author akari-ishida
 * @author xanderkoo
 */
public class SeamCarving {

	// RNG for tie-breakers
	private static Random random = new Random();

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
	 * minimum paths to each pixel from the left to the right
	 * 
	 * @param energyArray 2D array of energy values for each pixel
	 * @return 2D array of pairs of (double) minimum cumulative path energy for each
	 *         pixel and (int) direction to the minimum path (-1, 0, 1 for up-left,
	 *         straight up, and up-right, respectively)
	 */
	private static SeamFindingPair[][] findVerticalSeams(double[][] energyArray) {
		// width and height of array with energy values
		int width = energyArray.length;
		int height = energyArray[0].length;

		// variable to keep track of minimum energy value of path
		double minimum = 0;

		// 2D array to store path energy values and direction (-1, 0, 1)
		SeamFindingPair[][] pathEnergyDirArray = new SeamFindingPair[width][height];

		// Loops through energy array to find smallest value of the adjacent pixels
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {

				// If topmost row...
				if (j == 0) {
					// Since these pixels are the beginning of the path, set path energy to the
					// energy of that pixel. Direction defaults to 0.
					pathEnergyDirArray[i][j] = new SeamFindingPair(energyArray[i][j], 0);
				}

				// Else...
				else {
					// If in the left column...
					if (i == 0) {

						// Previous pixel with the minimum cumulative path energy is either up or
						// up-right
						minimum = Math.min(pathEnergyDirArray[i][j - 1].getDouble(),
								pathEnergyDirArray[i + 1][j - 1].getDouble());

						// Add direction based on if last pixel is up...
						if (minimum == pathEnergyDirArray[i][j - 1].getDouble())
							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, 0);

						// or up-right (x + 1)
						else
							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, 1);

						// NOTE: cumulative path energy assigned later
					}

					// If in the right column...
					else if (i == (width - 1)) {

						// Previous pixel with the minimum cumulative path energy is either up or
						// or up-left
						minimum = Math.min(pathEnergyDirArray[i][j - 1].getDouble(),
								pathEnergyDirArray[i - 1][j - 1].getDouble());

						// Add direction based on if last pixel is up...
						if (minimum == pathEnergyDirArray[i][j - 1].getDouble())
							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, 0);

						// or up-left (x - 1)
						else
							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, -1);

						// NOTE: cumulative path energy assigned later
					}

					// If not the left or right columns...
					else {

						// Previous pixel with the minimum cumulative path energy is to the up-left,
						// left, or down-left
						minimum = Math.min(pathEnergyDirArray[i][j - 1].getDouble(),
								Math.min(pathEnergyDirArray[i - 1][j - 1].getDouble(),
										pathEnergyDirArray[i + 1][j - 1].getDouble()));

						// Add direction based on if last pixel is straight-up (x)
						if (minimum == pathEnergyDirArray[i][j - 1].getDouble())
							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, 0);

						// Randomize checking up-left and up-right in case they are both the minimum
						else if (random.nextInt(2) == 0) {
							// up-left (x - 1)
							if (minimum == pathEnergyDirArray[i - 1][j - 1].getDouble())
								pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, -1);

							// or up-right (x + 1)
							else
								pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, +1);
						} else {
							// up-right (x + 1)
							if (minimum == pathEnergyDirArray[i + 1][j - 1].getDouble())
								pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, +1);

							// or up-left (x - 1)
							else
								pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, -1);
						}

					}

					// add the minimum value to the energy value of the cell
					// and assign the cumulative value to new 2D array
					pathEnergyDirArray[i][j].setDouble(energyArray[i][j] + minimum);

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
	private static SeamFindingPair[][] findHorizontalSeams(double[][] energyArray) {
		// width and height of array with energy values
		int width = energyArray.length;
		int height = energyArray[0].length;

		// variable to keep track of minimum energy value of path
		double minimum = 0;

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
						minimum = Math.min(pathEnergyDirArray[i - 1][j].getDouble(),
								pathEnergyDirArray[i - 1][j + 1].getDouble());

						// Add direction based on if last pixel is left...
						if (minimum == pathEnergyDirArray[i - 1][j].getDouble())
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
						minimum = Math.min(pathEnergyDirArray[i - 1][j].getDouble(),
								pathEnergyDirArray[i - 1][j - 1].getDouble());

						// Add direction based on if last pixel is left...
						if (minimum == pathEnergyDirArray[i - 1][j].getDouble())
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
						minimum = Math.min(pathEnergyDirArray[i - 1][j].getDouble(),
								Math.min(pathEnergyDirArray[i - 1][j - 1].getDouble(),
										pathEnergyDirArray[i - 1][j + 1].getDouble()));

						// Add direction based on if last pixel is straight-left (y)
						if (minimum == pathEnergyDirArray[i - 1][j].getDouble())
							pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, 0);

						// Randomize checking up-left and up-right in case they are both the minimum
						else if (random.nextInt(2) == 0) {
							// up-left (y - 1)
							if (minimum == pathEnergyDirArray[i - 1][j - 1].getDouble())
								pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, -1);

							// or down-left (y + 1)
							else
								pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, +1);
						} else {
							// down-left (y + 1)
							if (minimum == pathEnergyDirArray[i - 1][j + 1].getDouble())
								pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, +1);

							// or up-left (y - 1)
							else
								pathEnergyDirArray[i][j] = new SeamFindingPair(0.0, -1);
						}

					}

					// add the minimum value to the energy value of the cell
					// and assign the cumulative value to new 2D array
					pathEnergyDirArray[i][j].setDouble(energyArray[i][j] + minimum);

				}
			}
		}
		return pathEnergyDirArray;
	}

	/**
	 * Returns the SeamFindingPair (cumulative path energy and index of end of path)
	 * corresponding with the minimum energy vertical path
	 * 
	 * @param pathArray 2D array of SeamFindingPair, which contain cumulative path
	 *                  energy and the direction of the least cumulative path energy
	 * @return pair of least cumulative vertical path energy and corresponding index
	 */
	public static SeamFindingPair findMinSeamVertical(SeamFindingPair[][] pathArray) {
		/*
		 * Find the index in the path array of the minimum energy path to the rightmost
		 * column
		 */
		int minIndex = 0;
		double min = pathArray[minIndex][pathArray[minIndex].length - 1].getDouble();
		for (int i = 0; i < pathArray.length; i++) {
			if (pathArray[i][pathArray[i].length - 1].getDouble() <= min) {
				minIndex = i;
				min = pathArray[i][pathArray[minIndex].length - 1].getDouble();
			}
		}

		// Return pair of least cumulative path energy and the corresponding index
		return new SeamFindingPair(min, minIndex);
	}

	/**
	 * Returns the SeamFindingPair (cumulative path energy and index of end of path)
	 * corresponding with the minimum energy horizontal path
	 * 
	 * @param pathArray 2D array of SeamFindingPair, which contain cumulative path
	 *                  energy and the direction of the least cumulative path energy
	 * @return pair of least cumulative horizontal path energy and corresponding
	 *         index
	 */
	public static SeamFindingPair findMinSeamHorizontal(SeamFindingPair[][] pathArray) {
		/*
		 * Find the index in the path array of the minimum energy path to the rightmost
		 * column
		 */
		int minIndex = 0;
		double min = pathArray[pathArray.length - 1][minIndex].getDouble();
		for (int j = 0; j < pathArray[pathArray.length - 1].length; j++) {
			if (pathArray[pathArray.length - 1][j].getDouble() <= min) {
				minIndex = j;
				min = pathArray[pathArray.length - 1][j].getDouble();
			}
		}

		// Return pair of least cumulative path energy and the corresponding index
		return new SeamFindingPair(min, minIndex);
	}

	/**
	 * Given a path array and a 2D array representing an image, finds the minimum
	 * vertical seam, removes the pixels in the seam, and recombines the image.
	 * 
	 * @param pathArray 2D array of SeamFindingPair, which contain cumulative path
	 *                  energy and the direction of the least cumulative path energy
	 * @param image     2D array of Color objects representing the image
	 * @return 2D array of Color objects, (i-1) * j, representing the resized image
	 */
	public static Color[][] carveSeamVertical(SeamFindingPair[][] pathArray, Color[][] image) {

		// Get index of min energy path
		int minIndex = findMinSeamVertical(pathArray).getInt();

		/*
		 * Trace back that minimum energy path and remove the corresponding pixels in
		 * the image
		 */

		// Keep track of direction (-1, 0, 1 for up-left, straight-up, up-right) of next
		// pixel and next index
		int nextDir = pathArray[minIndex][pathArray[0].length - 1].getInt();
		int nextIndex = minIndex;

		// Trace the path back and remove the seam
		for (int j = pathArray[0].length; j > 0; j--) {

			// Removes pixels at seam
			image[nextIndex][j - 1] = null;

			// Update index of next pixel based on the direction
			nextIndex += nextDir;

			// Update direction to go to next
			nextDir = pathArray[nextIndex][j - 1].getInt();
		}

		Color[][] resized = new Color[image.length - 1][image[0].length];

		// For every column...
		for (int j = 0; j < image[0].length; j++) {

			// Start at top pixel
			int i = 0;
			Color pixel = image[i][j];

			// Copy pixels into new array until you hit the removed seam
			while (pixel != null) {
				resized[i][j] = pixel;
				pixel = image[++i][j];
			}

			// Skip the seam and copy the rest of the pixels into the new array
			while (i < image.length - 1) {
				resized[i][j] = image[++i][j];
			}
		}

		// Returns array of size i - 1, j
		return resized;
	}

	/**
	 * Given a path array and a 2D array representing an image, finds the minimum
	 * horizontal seam, removes the pixels in the seam, and recombines the image.
	 * 
	 * @param pathArray 2D array of SeamFindingPair, which contain cumulative path
	 *                  energy and the direction of the least cumulative path energy
	 * @param image     2D array of Color objects representing the image
	 * @return 2D array of Color objects, i * (j-1), representing the resized image
	 */
	public static Color[][] carveSeamHorizontal(SeamFindingPair[][] pathArray, Color[][] image) {

		// Get index of min energy path
		int minIndex = findMinSeamHorizontal(pathArray).getInt();

		/*
		 * Trace back that minimum energy path and remove the corresponding pixels in
		 * the image
		 */

		// Keep track of direction (-1, 0, 1 for up-left, straight-left, down-left) of
		// next pixel and next index
		int nextDir = pathArray[pathArray.length - 1][minIndex].getInt();
		int nextIndex = minIndex;

		// Trace the path back and remove the seam
		for (int i = pathArray.length; i > 0; i--) {

			// Removes pixels at seam
			image[i - 1][nextIndex] = null;

			// Update index of next pixel based on the direction
			nextIndex += nextDir;

			// Update direction to go to next
			nextDir = pathArray[i - 1][nextIndex].getInt();
		}

		Color[][] resized = new Color[image.length][image[0].length - 1];

		// For every column...
		for (int i = 0; i < image.length; i++) {

			// Start at top pixel
			int j = 0;
			Color pixel = image[i][j];

			// Copy pixels into new array until you hit the removed seam
			while (pixel != null) {
				resized[i][j] = pixel;
				pixel = image[i][++j];
			}

			// Skip the seam and copy the rest of the pixels into the new array
			while (j < image[i].length - 1) {
				resized[i][j] = image[i][++j];
			}
		}

		// Returns array of size i, j-1
		return resized;
	}

	/**
	 * Draws the minimum energy vertical seam corresponding to a path array onto an
	 * image
	 * 
	 * @param pathArray array of cumulative path energy + directions
	 * @param image     buffered image in question
	 * @post vertical seam drawn onto buffered image
	 */
	public static void drawSeamVertical(SeamFindingPair[][] pathArray, BufferedImage image) {

		// Get index of min energy path
		int minIndex = findMinSeamVertical(pathArray).getInt();

		/*
		 * Trace back that minimum energy path and re-color the corresponding pixels in
		 * the image
		 */

		// Keep track of direction (-1, 0, 1 for up-left, straight-up, up-right) of next
		// pixel and next index
		int nextDir = pathArray[minIndex][pathArray[0].length - 1].getInt();
		int nextIndex = minIndex;

		// Trace the path back and draw the seam
		for (int j = pathArray[0].length; j > 0; j--) {

			// Red line
			image.setRGB(nextIndex, j - 1, Color.RED.getRGB());

			// Update index of next pixel based on the direction
			nextIndex += nextDir;

			// Update direction to go to next
			nextDir = pathArray[nextIndex][j - 1].getInt();
		}
	}

	/**
	 * Draws the minimum energy horizontal seam corresponding to a path array onto
	 * an image
	 * 
	 * @param pathArray array of cumulative path energy + directions
	 * @param image     buffered image in question
	 * @post horizontal seam drawn onto buffered image
	 */
	public static void drawSeamHorizontal(SeamFindingPair[][] pathArray, BufferedImage image) {

		// Get index of min energy path
		int minIndex = findMinSeamHorizontal(pathArray).getInt();

		/*
		 * Trace back that minimum energy path and re-color the corresponding pixels in
		 * the image
		 */

		// Keep track of direction (-1, 0, 1 for up-left, straight-left, down-left) of
		// next pixel and next index
		int nextDir = pathArray[pathArray.length - 1][minIndex].getInt();
		int nextIndex = minIndex;

		// Trace the path back and remove the seam
		for (int i = pathArray.length; i > 0; i--) {

			// Red line
			image.setRGB(i - 1, nextIndex, Color.RED.getRGB());

			// Update index of next pixel based on the direction
			nextIndex += nextDir;

			// Update direction to go to next
			nextDir = pathArray[i - 1][nextIndex].getInt();
		}
	}

	/**
	 * 
	 * Main method. Takes images (named image0.jpg, image1.jpg, image2.jpg, ...) in
	 * project folder and writes resized image, energy map, and the energy map with
	 * demo seams as .PNG files into folder
	 * 
	 * @param args args[0]: vert. reduction in px, args[1]: horiz. reduction in px,
	 *             args[2]: number of images
	 * @throws IOException              if file is not found
	 * @throws IllegalArgumentException if deltaX >= width or deltaY >= length
	 */
	public static void main(String args[]) throws IOException {

		for (int imgNum = 0; imgNum < Integer.parseInt(args[2]); imgNum++) {

			// Begin timer
			long startTime = System.nanoTime();

			// File should be named "image<N>.jpg" s.t. <N> is elt of [0, number of images)

			File file = new File("./image" + imgNum + ".jpg");

			System.out.println("./image" + imgNum + ".jpg");
			try {
				BufferedImage imageSource = ImageIO.read(file);

				// Gets cols (width) and rows (height), and how much to shrink vertically and
				// horizontally
				int cols = imageSource.getWidth();
				int rows = imageSource.getHeight();
				int deltaY = Integer.parseInt(args[0]);
				int deltaX = Integer.parseInt(args[1]);
				if (deltaY >= cols || deltaY < 0 || deltaX >= rows || deltaX < 0)
					throw new IllegalArgumentException("Too much or negative shrinkage");

				// Print image dimensions
				System.out.printf("%d by %d pixels\n", cols, rows);

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

				/* Generate energy array, save image in grayscale */
				double[][] energyArray = energyFunction(image);
				BufferedImage imageEnergy = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);
				File fileEnergy = new File("./energy" + imgNum + ".png");
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
				File fileSeam = new File("./seam" + imgNum + ".png");
				// Copies the energy representation of the photo into the buffered image
				for (int i = 0; i < cols; i++) {
					for (int j = 0; j < rows; j++) {
						imageSeam.setRGB(i, j, imageEnergy.getRGB(i, j));
					}
				}
				// Draws the vertical and horizontal seams
				drawSeamHorizontal(findHorizontalSeams(energyArray), imageSeam);
				drawSeamVertical(findVerticalSeams(energyArray), imageSeam);
				ImageIO.write(imageSeam, "PNG", fileSeam);

				// Save pixels in x and y to shrink
				int deltaYt = deltaY;
				int deltaXt = deltaX;

				// Declare variables to prepare for the while loop
				Color[][] resized = image.clone(); // instantiate this one as our original image
				double[][] energyArr;
				SeamFindingPair[][] pathArrayV;
				SeamFindingPair[][] pathArrayH;
				SeamFindingPair mPathV;
				SeamFindingPair mPathH;

				System.out.println("Carving: ");

				// Keep going until we are done with the horizontal and vertical carving.
				// Recalculates the seam path array every time a seam is removed.
				while (deltaYt > 0 || deltaXt > 0) {

					// Get energy array for resized array
					energyArr = energyFunction(resized);

					// Get array of seam paths for vertical and horizontal
					pathArrayV = findVerticalSeams(energyArr);
					pathArrayH = findHorizontalSeams(energyArr);

					// Get min seam paths for vertical and horizontal
					mPathV = findMinSeamVertical(pathArrayV);
					mPathH = findMinSeamHorizontal(pathArrayH);

					// If no more horizontal seams to be carved, carve vertical seams
					if (deltaYt == 0) {
						resized = carveSeamVertical(pathArrayV, resized);
						deltaXt--;
						System.out.print("V");
					}

					// If no more vertical seams to be carved, carve horizontal seams
					else if (deltaXt == 0) {
						resized = carveSeamHorizontal(pathArrayH, resized);
						deltaYt--;
						System.out.print("H");
					}

					// If vertical seam has less cumulative energy than the horizontal, carve
					// vertical
					else if (mPathV.getDouble() < mPathH.getDouble()) {
						resized = carveSeamVertical(pathArrayV, resized);
						deltaXt--;
						System.out.print("V");
					}

					// If horizontal seam has less cumulative energy than the vertical, carve
					// horizontal
					else {
						resized = carveSeamHorizontal(pathArrayH, resized);
						deltaYt--;
						System.out.print("H");
					}
				}

				System.out.println();

				// Copies the seam-carved photo into the buffered image
				BufferedImage imageResized = new BufferedImage(cols - deltaX, rows - deltaY,
						BufferedImage.TYPE_INT_RGB);
				File fileResized = new File("./resized" + imgNum + ".png");
				for (int i = 0; i < cols - deltaX; i++) {
					for (int j = 0; j < rows - deltaY; j++) {
						imageResized.setRGB(i, j, resized[i][j].getRGB());
					}
				}
				ImageIO.write(imageResized, "PNG", fileResized);

				// Stop timer
				long endTime = System.nanoTime();

				// Print time for each image to be carved in ms
				System.out.println("Process successfully completed for " + file.getName() + " in "
						+ (endTime - startTime) / 1000000 + " ms\n");

			} catch (IOException e) {
				throw e;
			}
		}
	}
}
