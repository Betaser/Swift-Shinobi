package state.levelDesignState;

import java.util.List;

import base.Game;
import levels.Level;
import math.Polygon;
import math.Vec;
import ui.Panel;

public class MainPanel extends Panel {
	
	Level level;
	Brushwork brushwork;
	Eraserwork eraserwork;
	Camwork camwork;
	
	public MainPanel(Level level, Brushwork brushwork,
		Eraserwork eraserwork, Camwork camwork) {
		super(0, new Polygon(List.of(
			new Vec(0, 0),
			new Vec(Game.get().getWidth(), 0),
			new Vec(Game.get().getWidth(), Game.get().getHeight()),
			new Vec(0, Game.get().getHeight())
		)), List.of(
			brushwork, brushwork.selectwork, eraserwork, eraserwork.selectwork, camwork
		));
		this.level = level;
		this.brushwork = brushwork;
		this.camwork = camwork;
		this.eraserwork = eraserwork;
	}
	
	public void tick() {
		camwork.tick(level.entities);
	}

}
