package console;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;

import base.Game;
import entity.Entity;
import helper.Calc;
import helper.Utils;
import math.Polygon;
import math.Vec;

public class DisplayedText extends Entity {

	List<String> lines;
	Rectangle boundsRect;
	JScrollPane consoleScrollPane;
	Vec selectorPos;
	double lineOffset;
	static final int TEXT_HEIGHT = 30;
	static final double SCROLL_SPEED = 5;
	
	public DisplayedText(Vec selectorPos, JScrollPane consoleScrollPane) {
		this.selectorPos = selectorPos;
		this.consoleScrollPane = consoleScrollPane;
	}
	
	public void init() {
		lines = new ArrayList<>();
		lineOffset = 0;
		
		final int HEIGHT = 80;
		Polygon BOUNDS = new Polygon(List.of(
			new Vec(consoleScrollPane.getX(), Game.get().getHeight() - consoleScrollPane.getY()),
			new Vec(consoleScrollPane.getX() + consoleScrollPane.getWidth(), Game.get().getHeight() - consoleScrollPane.getY()),
			new Vec(consoleScrollPane.getX() + consoleScrollPane.getWidth(), Game.get().getHeight() - consoleScrollPane.getY() + HEIGHT),
			new Vec(consoleScrollPane.getX(), Game.get().getHeight() - consoleScrollPane.getY() + HEIGHT)
		));
		bounds.set(BOUNDS);
		
		boundsRect = Calc.containingRect(bounds);
		
		render = this::render;
	}
	
	public void tick() {
		if (bounds.surroundsPoint(selectorPos)) {
			double scroll = Game.get().input.consoleScroll.scrollAmt();
			if (scroll != 0)
				lineOffset += SCROLL_SPEED * scroll;
		}
		if (lines.size() * TEXT_HEIGHT > boundsRect.height) {
			lineOffset = Math.max(boundsRect.height - lines.size() * TEXT_HEIGHT + TEXT_HEIGHT / 2, lineOffset);
		} else {
			lineOffset = Math.max(0, lineOffset);
		}
		lineOffset = Math.min(0, lineOffset);
	}
	
	public void render(Graphics g) {
		renderFilled(g, Utils.diffAlpha(Color.PINK, 50));
		g.setFont(new Font("Courier New", Font.PLAIN, 20));
		g.setColor(Color.CYAN);

		g.setClip(boundsRect.x, Game.get().getHeight() - boundsRect.y - boundsRect.height, boundsRect.width, boundsRect.height);
		
		for (int i = 0; i < lines.size(); i++) {
			g.drawString(lines.get(lines.size() - 1 - i), (int) boundsRect.getX(),
				Game.get().getHeight() - ((int) lineOffset + (int) boundsRect.getY() + i * TEXT_HEIGHT));
		}
	}
	
}
