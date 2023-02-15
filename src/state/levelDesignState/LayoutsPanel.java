package state.levelDesignState.layouts;

import java.util.List;

import base.Game;
import helper.Utils;
import math.Vec;
import ui.Panel;

public class LayoutsPanel extends Panel {
	
	public static final int WIDTH = 200;
	
	static final int HEIGHT = (int) (Game.get().getHeight() * 0.8);
	
	Layoutwork layers;
	
	public LayoutsPanel(Layoutwork layers) {
		super(-1, Utils.mkCenteredRect(
			new Vec(
				Game.get().getWidth() - WIDTH / 2,
				Game.get().getHeight() / 2
			), 
			WIDTH / 2,
			HEIGHT / 2
		), List.of(
			layers
		));
		this.layers = layers;
	}
	
	public void tick() {
		layers.tick();
	}

}
