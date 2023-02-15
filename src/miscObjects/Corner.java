package miscObjects;

import math.Vec;

public class Corner extends Vec {
	
	public Corner() {
		super();
	}
	
	public Corner(Vec vec) {
		super(vec.getX(), vec.getY());
	}
	
	public Corner(miscObjects.Point point) {
		super(point);
	}
	
	public Corner(double x, double y) {
		super(x, y);
	}
	
}
