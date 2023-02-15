package state.levelDesignState;

import java.util.ArrayList;
import java.util.List;

import base.Game;
import button.Button;
import helper.Calc;
import helper.Utils;
import math.Polygon;
import math.Vec;
import state.levelDesignState.layouts.LayoutsPanel;
import ui.Panel;

public class ChooseEntityPanel extends Panel {

	static final int HEIGHT = (int) ChooseEntitywork.ChooseEntityButton.HEIGHT;
	
	//	Sure, chooseEntitywork is forceably part of this panel but chooseEntitywork 
	//	probably won't be referenced much outside of here
	ChooseEntitywork chooseEntitywork;
	
	public void init(Vec cursor, Brushwork brushwork, Camwork camwork, LevelDesignState LDState) {
		chooseEntitywork = new ChooseEntitywork();
		init(-20, initBounds(), List.of(chooseEntitywork));
		chooseEntitywork.init(cursor, Calc.containingRect(bounds), brushwork, camwork, LDState);
	}
	
	private Polygon initBounds() {
		return new Polygon(List.of(
			new Vec(0, Game.get().getHeight() - HEIGHT),
			new Vec(Game.get().getWidth() - LayoutsPanel.WIDTH, Game.get().getHeight() - HEIGHT),
			new Vec(Game.get().getWidth() - LayoutsPanel.WIDTH, Game.get().getHeight()),
			new Vec(0, Game.get().getHeight())
		));
	}

	public void tick() {
		chooseEntitywork.tick();
	}
	
	public void render(ArrayList<Runnable> renderers) {
		for (Button button : chooseEntitywork.chooseEntityBar.buttons)
			Utils.draw(button, renderers);
	}
	
}
