package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Comparator;

import base.Game;
import math.Sizer;
import math.Vec;

public class PanelManager extends ArrayList<Panel> {
	
	private static final long serialVersionUID = 1L;
	
	public void managePanels(Vec selectorPos) {
		sort();
		findControls();
		consumeControls(selectorPos);
		tick();
	}
	
	public void findControls() {
		forEach(Panel::findControls);
	}
	
	public void consumeControls(Vec selectorPos) {
		Panel selected = get(selectorPos);
		for (Panel panel : this)
			if (panel != selected)
				panel.consumeControls();
	}
	
	public void renderPanels(Vec selectorPos) {
		Graphics g = Game.get().getGraphics(-1000);
		Color color = new Color(255, 255, 255);
		Panel p = get(selectorPos);
		p.bounds.draw(g, color, Sizer.DEF);
	}
	
	public void tick() {
		forEach(Panel::tick);
	}
	
	public Panel get(Vec selectorPos) {
		return stream().filter(p -> p.bounds.surroundsPoint(selectorPos)).findFirst().orElse(null);
	}
	
	public void sort() {
		super.sort(Comparator.comparing(p -> p.depth));
	}

}
