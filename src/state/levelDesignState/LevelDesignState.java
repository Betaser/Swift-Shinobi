package state.levelDesignState;

import java.util.ArrayList;
import java.util.List;

import base.Game;
import console.ConsolePanel;
import entity.tile.Tile;
import levels.Level;
import math.Vec;
import state.State;
import state.levelDesignState.io.SaveLevel;
import state.levelDesignState.layouts.Layout;
import state.levelDesignState.layouts.LayoutsPanel;
import state.levelDesignState.layouts.Layoutwork;

public class LevelDesignState extends State {
	
	final static String[] DEF_GRID_OF_IDS = {
		"1 0 0 0 1",
		"1 0 0 0 1",
		"1 0 0 0 1",
		"1 1 1 1 1"
	};
	
	Vec cursor;
	Brushwork brushwork;
	Eraserwork eraserwork;
	Camwork camwork;
	ChooseEntityPanel chooseEntityPanel;
	MainPanel mainPanel;
	LayoutsPanel layersPanel;
	ConsolePanel consolePanel;
	Layoutwork layoutwork;
	SaveLevel saveLevel = new SaveLevel(Game.get().console());
	static enum Tool {
		BRUSH,
		ERASER
	}
	Tool tool;
	Tool prevTool;
	
	public void _init() {
		Level exLevel = new Level();
		exLevel.entities.addAll(loadDefLevel());
		
		cursor = new Vec();
		layoutwork = new Layoutwork();

		brushwork = new Brushwork();
		brushwork.init(cursor);
		
		eraserwork = new Eraserwork();
		eraserwork.init(cursor);

		camwork = new Camwork();
		camwork.init(exLevel.entities);
		
		chooseEntityPanel = new ChooseEntityPanel();
		chooseEntityPanel.init(cursor, brushwork, camwork, this);
	
		mainPanel = new MainPanel(exLevel, brushwork, eraserwork, camwork);
		
		layersPanel = new LayoutsPanel(layoutwork);
		
		layoutwork.init(cursor, layersPanel.bounds, brushwork);
		
		consolePanel = Game.get().genConsolePanel;

		tool = Tool.BRUSH;
		prevTool = Tool.BRUSH;
		
		brushwork.toolInit(camwork, brushwork.selectwork.selected, brushwork.selectwork.selected);
		
		exLevel.init();
		
		Layout exLayout = layoutwork.mkLayout(0);
		exLayout.level = exLevel;
		layoutwork.layouts.add(exLayout);
		
		layoutwork.activeLayout = exLayout;
		
		initPanelManager(List.of(chooseEntityPanel, mainPanel, layersPanel, consolePanel), cursor);
	}

	public void _tick() {
		cursor.setCart(Game.get().input.cursor);

		saveLevel.manageSave(layoutwork);
		manageToolChoice();
		manageTool();
		
		prevTool = tool;
	}
	
	public void _render() {
		renderTool();
		layoutwork.render(renderers);
		chooseEntityPanel.render(renderers);
		renderers.add(consolePanel::render);
	}
	
	public void manageTool() {
		switch (tool) {
			case BRUSH:
				brushwork.tick(prevTool, layoutwork.activeLayout.level.entities, camwork);
			break;
			case ERASER:
				eraserwork.tick(prevTool, layoutwork.activeLayout.level.entities, camwork);
			break;
		}
	}
	
	public void renderTool() {
		switch (tool) {
			case BRUSH:
				brushwork.render(renderers);
			break;
			case ERASER:
				eraserwork.render(renderers);
			break;
		}
	}
	
	public void manageToolChoice() {
		if (Game.get().input.brushTool.hold())
			tool = Tool.BRUSH;
		if (Game.get().input.eraserTool.hold())
			tool = Tool.ERASER;
	}
	
	public static ArrayList<Tile> loadDefLevel() {
		return Level.loadTiles(DEF_GRID_OF_IDS);
	}

}
