package de.wattestaebchen.dyingrabbit99.helpers;

public class Math {
	
	public static int divide(int dividend, int divisor) {
		int mod = dividend % divisor;
		int quot = dividend / divisor;
		return (mod*2 >= divisor) ?
				(quot + 1) :
				quot;
	}
	
	public static int divide(int dividend, int divisor, boolean roundUp) {
		int mod = dividend % divisor;
		int quot = dividend / divisor;
		return (mod != 0 && roundUp) ?
				(quot + 1) :
				quot;
	}
	
}
