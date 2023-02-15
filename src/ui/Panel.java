package ui;

import java.util.List;

import math.Polygon;

public abstract class Panel {
	
	public double depth;
	public Polygon bounds;
	public List<Controllable> controllables;
	
	public Panel() {}
	
	public Panel(double depth, Polygon bounds, List<Controllable> controllables) {
		init(depth, bounds, controllables);
	}
	
	public void init(double depth, Polygon bounds, List<Controllable> controllables) {
		this.depth = depth;
		this.bounds = bounds;
		this.controllables = controllables;
	}
	
	public abstract void tick();
	
	public void findControls() {
		controllables.forEach(Controllable::findControls);
	}
	
	public void consumeControls() {
		controllables.forEach(Controllable::disableControls);
	}

}
