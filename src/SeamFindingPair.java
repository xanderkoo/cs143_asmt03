/**
 * Object for holding two numbers. For seam finding, the first int will be the
 * cumulative path energy and the second int will be the direction (-1, 0, 1)
 */
public class SeamFindingPair {

	private double d;
	private int i;

	/**
	 * create pair of double and int
	 * 
	 * @param i1
	 * @param i2
	 */
	public SeamFindingPair(double d, int i) {
		this.d = d;
		this.i = i;
	}

	/**
	 * 
	 * @return first number, double
	 */
	public double getFirst() {
		return this.d;
	}

	/**
	 * 
	 * @return second number, int
	 */
	public int getSecond() {
		return this.i;
	}

	/**
	 * sets first number, double
	 */
	public void setFirst(double d) {
		this.d = d;
	}

	/**
	 * sets second number, int
	 */
	public void setSecond(int i) {
		this.i = i;
	}
	
	public String toString() {
		return "<" + d + "," + i +">";
	}
}
