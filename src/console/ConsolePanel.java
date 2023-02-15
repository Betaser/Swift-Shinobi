package console;

import java.awt.Rectangle;
import java.util.List;

import base.Game;
import helper.Calc;
import helper.Utils;
import math.Polygon;
import math.Vec;
import ui.Panel;

public class ConsolePanel extends Panel {
	
	public Console console;
	public Polygon defBounds;
	
	public ConsolePanel(Console console) {
		super(-10, new Polygon(List.of(new Vec())), List.of(console));
		this.console = console;
	}
	
	public void initBounds() {
		defBounds = mkBounds(console);
	}
	
	public void tick() {
		console.tick();
	}
	
	public void render() {
		console.render();
	}
	
	//	wrap console in bounds that just fit it
	static Polygon mkBounds(Console console) {
		Rectangle scrollPaneBounds = console.scrollPane.getBounds();
		final double HEIGHT = scrollPaneBounds.getHeight() + Calc.containingRect(console.displayedText.bounds).height;
		final double Y_POS = Game.get().getHeight() - scrollPaneBounds.y - scrollPaneBounds.height;
		Vec center = new Vec(Game.get().getWidth() / 2, HEIGHT / 2 + Y_POS);
		return Utils.mkCenteredRect(center, scrollPaneBounds.getWidth() / 2, HEIGHT / 2);
	}

}
