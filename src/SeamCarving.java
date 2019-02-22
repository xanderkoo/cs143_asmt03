import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;

/*

 */

public class SeamCarving {

	private int energyArray[][];

	private static double[][] energyFunction(Color[][] colorArray) {

		double[][] energyArray = new double[colorArray.length][colorArray[0].length];
		double redEnergy, greenEnergy, blueEnergy;
		double ddx, ddy;

		// Go through entire array
		for (int i = 0; i < colorArray.length; i++) {
			for (int j = 0; j < colorArray[i].length; j++) {

				// On left edge
				if (i == 0) {
					ddx = Math.abs(colorArray[i + 1][j].getRed() - colorArray[i][j].getRed());
				}

				// On right edge
				else if (i == colorArray.length - 1) {
					ddx = Math.abs(colorArray[i - 1][j].getRed() - colorArray[i][j].getRed());

				}

				// Not on a vertical edge
				else {
					ddx = Math.abs(colorArray[i - 1][j].getRed() - colorArray[i + 1][j].getRed()) / 2;
				}

				// On top edge
				if (j == 0) {
					ddy = Math.abs(colorArray[i][j + 1].getRed() - colorArray[i][j].getRed());

				}

				// On bottom edge
				else if (j == colorArray[i].length - 1) {
					ddy = Math.abs(colorArray[i][j - 1].getRed() - colorArray[i][j].getRed());

				}

				// Not on a horizontal edge
				else {
					ddy = Math.abs(colorArray[i][j - 1].getRed() - colorArray[i][j + 1].getRed()) / 2;

				}

				redEnergy = ddx + ddy;

				// Edge cases in the x axis
				if (i == 0) {
					ddx = Math.abs(colorArray[i + 1][j].getGreen() - colorArray[i][j].getGreen());
				} else if (i == colorArray.length - 1) {
					ddx = Math.abs(colorArray[i - 1][j].getGreen() - colorArray[i][j].getGreen());

				} else { // Not on a vertical edge
					ddx = Math.abs(colorArray[i - 1][j].getGreen() - colorArray[i + 1][j].getGreen()) / 2;
				}

				// Edge cases in the y axis
				if (j == 0) {
					ddy = Math.abs(colorArray[i][j + 1].getGreen() - colorArray[i][j].getGreen());
				} else if (j == colorArray[i].length - 1) {
					ddy = Math.abs(colorArray[i][j - 1].getGreen() - colorArray[i][j].getGreen());
				} else { // Not on a horizontal edge
					ddy = Math.abs(colorArray[i][j - 1].getGreen() - colorArray[i][j + 1].getGreen()) / 2;

				}

				greenEnergy = ddx + ddy;

				// Edge cases in the x axis
				if (i == 0) {
					ddx = Math.abs(colorArray[i + 1][j].getBlue() - colorArray[i][j].getBlue());
				}

				else if (i == colorArray.length - 1) {
					ddx = Math.abs(colorArray[i - 1][j].getBlue() - colorArray[i][j].getBlue());

				} else {
					ddx = Math.abs(colorArray[i - 1][j].getBlue() - colorArray[i + 1][j].getBlue()) / 2;
				}

				// Edge cases in the y axis
				if (j == 0) {
					ddy = Math.abs(colorArray[i][j + 1].getBlue() - colorArray[i][j].getBlue());

				} else if (j == colorArray[i].length - 1) {
					ddy = Math.abs(colorArray[i][j - 1].getBlue() - colorArray[i][j].getBlue());

				} else {
					ddy = Math.abs(colorArray[i][j - 1].getBlue() - colorArray[i][j + 1].getBlue()) / 2;

				}

				blueEnergy = ddx + ddy;

				// Average energy function output for each color channel
				energyArray[i][j] = (redEnergy + greenEnergy + blueEnergy) / 3;
			}

		}

		return energyArray;
	}

	public static void main(String args[]) throws IOException {
		File file = new File("./kinkakuji.jpg");
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
		File fileNew = new File("./leaves_out_java.jpg");
		double[][] energyArray = energyFunction(image);
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				System.out.print((int) energyArray[i][j] + " ");
				int r = 0; //(int) energyArray[i][j];
				int g = 0; //(int) energyArray[i][j];
				int b = 255 - (int) energyArray[i][j];
				int col = (r << 16) | (g << 8) | b;
				imageNew.setRGB(i, j, col);
			}
			System.out.println();
		}

		ImageIO.write(imageNew, "JPEG", fileNew);
	}
}
