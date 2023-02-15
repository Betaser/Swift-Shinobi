package miscObjects;

import math.Vec;

public class Collision extends Tuple4<Vec, ShortestCollisionInfo, Vec, ShortestCollisionInfo> {
	
	public Collision(Vec move1, ShortestCollisionInfo info1, Vec move2, ShortestCollisionInfo info2) {
		super(move1, info1, move2, info2);
	}
	
	public Vec move1() {
		return a;
	}
	
	public ShortestCollisionInfo info1() {
		return b;
	}
	
	public Vec move2() {
		return c;
	}
	
	public ShortestCollisionInfo info2() {
		return d;
	}
	
}