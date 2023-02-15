package miscObjects;

import math.Vec;

public class ShortestCollisionInfo extends Tuple4<Double, Vec, SideCornerCollision, Boolean> {

	public ShortestCollisionInfo(double dist, Vec pos, SideCornerCollision collisionInfo, boolean isPoly1Corner) {
		super(dist, pos, collisionInfo, isPoly1Corner);
	}

	public double dist() {
		return a;
	}

	public Vec pos() {
		return b;
	}

	public SideCornerCollision collisionInfo() {
		return c;
	}

	public boolean isPoly1Corner() {
		return d;
	}

}
