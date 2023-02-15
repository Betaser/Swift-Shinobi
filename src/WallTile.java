package entity.tile;

import java.awt.Color;
import java.awt.Graphics;

public class WallTile extends Tile {
	
	public void init() {
		render = this::render;
	}
	
	public void render(Graphics g) {
		renderFilled(g, Color.DARK_GRAY);
	}
	
	public WallTile clone() {
		WallTile clone = new WallTile();
		cloneHelper(clone);
		return clone;
	}

}
