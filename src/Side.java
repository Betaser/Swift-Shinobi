package miscObjects;

import math.Vec;

public class Side {
	
	public Vec p1;
	public Vec p2;
	
	public Side(Vec p1, Vec p2) {
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public String toString() {
		return "from {" + p1 + " to " + p2 + "}";
	}
	
}
