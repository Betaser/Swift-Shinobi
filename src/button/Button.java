package button;

import java.awt.Color;
import java.awt.Graphics;

import entity.Entity;
import helper.Utils;
import math.Polygon;
import math.Sizer;
import math.Vec;

public class Button extends Entity {
	
	boolean isHovered;

	public Color def = Color.GRAY;
	public Color highlight = Utils.diffAlpha(Color.RED, 100);
	public Vec selectorPos;
	public boolean press;
	public Runnable onPress;
	
	public Button() {}

	public Button(Vec selectorPos, Polygon bounds, Runnable onPress, Sizer cam) {
		init(selectorPos, bounds, onPress, cam);
	}

	public void init(Vec selectorPos, Polygon bounds, Runnable onPress, Sizer cam) {
		this.selectorPos = selectorPos;
		this.bounds = bounds;
		this.onPress = onPress;
		this.cam = cam;
	}
	
	public void render(Graphics g) {
		renderNormal(g);
		if (isHovered)
			renderHighlighted(g);
	}
	
	public void renderNormal(Graphics g) {
		renderFilled(g, def);
	}
	
	public void renderHighlighted(Graphics g) {
		renderFilled(g, highlight);
	}
	
	protected void calc() {
		isHovered = bounds.surroundsPoint(selectorPos);
	}
	
	protected void tryRun() {
		if (isHovered && press)
			onPress.run();
	}
	
	public boolean isHovered() {
		return isHovered;
	}
	
	public void tick() {
		calc();
		tryRun();
	}
	
}
