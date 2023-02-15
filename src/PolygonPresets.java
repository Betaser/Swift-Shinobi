package state;

import java.util.ArrayList;
import java.util.List;

import java.awt.Color;
import java.awt.image.BufferedImage;

import base.Game;
import helper.Constants;
import math.Polygon;
import math.Sizer;
import math.Vec;

public class PolygonPresets {
	
	public static final PolygonPresets EX1;
	
	static {
		EX1 = new PolygonPresets();
		EX1.polygons = new ArrayList<>(List.of(
			new Polygon(List.of(
				new Vec(150, 120),
				new Vec(200, 120),
				new Vec(200, 200)
			)),
			new Polygon(List.of(
				new Vec(120, 200),
				new Vec(220, 200),
				new Vec(220, 300),
				new Vec(120, 300)
			)),
			new Polygon(List.of(
				new Vec(300, 200),
				new Vec(400, 220),
				new Vec(350, 300),
				new Vec(200, 270),
				new Vec(275, 230)
			))
		));
	}
	
	public ArrayList<Polygon> polygons;
	
	public void draw(Sizer gfxSizer) {
		BufferedImage layer = Game.get().createLayer();
		for (Polygon p : polygons)
			p.draw(layer.getGraphics(), Color.WHITE, gfxSizer);
		Game.get().draw(layer, Constants.EN_DEPTH);
	}
	
	public PolygonPresets() {

	}
	
}
