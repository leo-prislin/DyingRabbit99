package de.wattestaebchen.dyingrabbit99.helpers;

public class IntPair {
	
	private int x, y;
	
	public IntPair(int x, int y) {
		set(x, y);
	}
	
	public int getX() { return x; }
	public int getY() { return y; }
	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }
	public void set(int x, int y) {
		setX(x);
		setY(y);
	}
	
	
	public double vectorLength() {
		return java.lang.Math.sqrt(x*x + y*y);
	}
	
}
