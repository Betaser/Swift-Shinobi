package entity.tile;

import java.awt.Color;
import java.awt.Graphics;

public class SolidTile extends Tile {
	
	public SolidTile() {
		super();
	}
	
	public void init() {
		render = this::render;
	}
	
	public void render(Graphics g) {
		renderFilled(g, Color.WHITE);
	}
	
	public SolidTile clone() {
		SolidTile clone = new SolidTile();
		cloneHelper(clone);
		return clone;
	}
	
}
