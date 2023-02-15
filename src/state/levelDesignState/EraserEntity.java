package state.levelDesignState;

import java.awt.Color;
import java.awt.Graphics;

import entity.Entity;

public class EraserEntity extends Entity {
	
	public void init() {
		render = this::render;
		depth = -20;
	}
	
	public void render(Graphics g) {
		renderFilled(g, Color.RED);
	}
	
	public EraserEntity clone() {
		EraserEntity clone = new EraserEntity();
		cloneHelper(clone);
		return clone;
	}

}
