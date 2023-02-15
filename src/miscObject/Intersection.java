package miscObjects;

import math.Vec;

public class Intersection extends Tuple3<Vec, Boolean, Boolean> {

	public Intersection(Vec pos, boolean withinLine1, boolean withinLine2) {
		super(pos, withinLine1, withinLine2);
	}
	
	public Vec pos() {
		return a;
	}
	
	public Boolean withinLine1() {
		return b;
	}
	
	public Boolean withinLine2() {
		return c;
	}
	
}
