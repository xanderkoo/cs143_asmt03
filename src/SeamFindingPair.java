/**
 * Object for holding two numbers. For seam finding, the first int will be the
 * cumulative path energy and the second int will be the direction (-1, 0, 1)
 */
public class SeamFindingPair {

	private double energy;
	private int direction;

	/**
	 * create pair of minimum cumulative path energy and direction (-1, 0, 1)
	 * 
	 * @param energy minimum cumulative path energy
	 * @param direction (-1, 0, 1)
	 */
	public SeamFindingPair(double energy, int direction) {
		this.energy = energy;
		this.direction = direction;
	}

	/**
	 * 
	 * @return cumulative path energy, double
	 */
	public double getCumulPathEnergy() {
		return this.energy;
	}

	/**
	 * 
	 * @return direction (-1, 0, 1), int
	 */
	public int getDirection() {
		return this.direction;
	}

	/**
	 * sets cumulative path energy, double
	 */
	public void setCumulPathEnergy(double energy) {
		this.energy = energy;
	}

	/**
	 * sets direction (-1, 0, 1), int
	 */
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public String toString() {
		return "<" + energy + "," + direction +">";
	}
}
