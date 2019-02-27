/**
 * Object for holding two numbers
 */
public class SeamFindingPair {

	private double d;
	private int i;

	/**
	 * create pair of double and int
	 * 
	 * @param d double
	 * @param i int
	 */
	public SeamFindingPair(double d, int i) {
		this.d = d;
		this.i = i;
	}

	/**
	 * 
	 * @return double
	 */
	public double getDouble() {
		return this.d;
	}

	/**
	 * 
	 * @return int
	 */
	public int getInt() {
		return this.i;
	}

	/**
	 * sets double
	 */
	public void setDouble(double d) {
		this.d = d;
	}

	/**
	 * sets int
	 */
	public void setInt(int i) {
		this.i = i;
	}

	public String toString() {
		return "<" + (int) d + "," + i + ">";
	}
}
