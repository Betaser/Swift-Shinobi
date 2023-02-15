package state.levelDesignState.layouts;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import base.Game;
import button.Button;
import button.ButtonGroup;
import console.Console;
import helper.Calc;
import helper.Utils;
import math.Polygon;
import math.Sizer;
import math.Vec;
import miscObjects.Noted;
import state.levelDesignState.Brushwork;
import ui.Controllable;

public class Layoutwork implements Controllable {

	public ArrayList<Layout> layouts;
	public Layout activeLayout;
	public String name;
	
	Brushwork brushwork;
	ButtonGroup buttonGroup;
	boolean cursorPress;
	boolean cursorHover;
	Rectangle PANEL_RECT;
	Console console;
	enum LayoutInput {
		ASK_DEPTH,
		ASK_NAME,
		NONE
	}
	LayoutInput layoutInput;
	Vec cursor;
	int n;
	
	public void init(Vec cursor, Polygon panelBounds, Brushwork brushwork) {
		this.cursor = cursor;
		this.brushwork = brushwork;

		layouts = new ArrayList<>();
		buttonGroup = new ButtonGroup(new ArrayList<>());
		layoutInput = LayoutInput.NONE;
		
		console = Game.get().console();

		PANEL_RECT = Calc.containingRect(panelBounds);
		final int WIDTH = (int) (PANEL_RECT.width * 0.8);
		final int HEIGHT = 50;
		final int Y_OFFSET = 25;
		
		Polygon bounds = Utils.mkCenteredRect(
			new Vec(PANEL_RECT.getCenterX(), PANEL_RECT.y + Y_OFFSET + HEIGHT / 2), 
			WIDTH / 2, 
			HEIGHT / 2
		);
		Runnable onPress = () -> {
			layoutInput = LayoutInput.ASK_DEPTH;
			Game.get().console().forceShow().print("Enter the layout's default entity depth", LayoutInput.ASK_DEPTH);
			console.setCurrCaller(LayoutInput.ASK_DEPTH);
		};
		Sizer cam = Sizer.DEF;
		
		Button makeLayerBtn = new Button(cursor, bounds, onPress, cam);
		makeLayerBtn.depth = -11;
		
		buttonGroup.buttons.add(makeLayerBtn);
	}
	
	public void findControls() {
		cursorPress = Game.get().input.lMouse.click();
		cursorHover = buttonGroup.buttons.stream().anyMatch(Button::isHovered);
	}
	
	public void tick() {
		Noted<Console.InputValidity, String> okInput = console.getLastOkInput();
		
		switch (okInput.enumVal) {
			case NULL: {
				break;
			}
			case OK, CALLED: {
				if (okInput.enumVal == Console.InputValidity.CALLED && 
					!Game.get().console().isCurrCaller(LayoutInput.ASK_DEPTH))
					break;
				
				String input = okInput.val;

				switch (layoutInput) {
					case ASK_DEPTH: {
						if (Utils.isNum(input)) {
							layoutInput = LayoutInput.NONE;
							Layout layout = mkLayout(Double.parseDouble(input));
							layouts.add(layout);
							console.endCaller();
							console.forceHide();
						} else
							console.print("Try again, you must enter a number");
						break;
					}
					case ASK_NAME: {
						activeLayout.name = input;
						break;
					}
					case NONE: {}
				}
				break;
			}
		}
		
		if (Game.get().input.deleteLayout.click() && layouts.size() > 1) {
			int prevI = layouts.indexOf(activeLayout);
			Layout prevLayout = activeLayout;
			int activeLayoutI = (prevI + 1) % layouts.size();
			activeLayout = layouts.get(activeLayoutI);
			
			layouts.remove(prevI);
			buttonGroup.buttons.removeAll(List.of(prevLayout.renameBtn, prevLayout.selectBtn, prevLayout.visibilityBtn));
		}

		buttonGroup.tick(cursorPress);
		layouts.forEach(layer -> layer.tick(cursorPress));
	}
	
	public Layout mkLayout(double defEnDepth) {
		Layout layout = new Layout("layout" + layouts.size(), -10, buttonGroup);
		
		//	Rename button
		final int WIDTH = (int) (PANEL_RECT.width * 0.4);
		final int HEIGHT = 30;
		final int S_HEIGHT = 40;
		final int TOP_BOUND = (int) (PANEL_RECT.height * 0.8);
		final int Y_OFFSET = TOP_BOUND - S_HEIGHT * layouts.size();

		final int V_WIDTH = (int) (PANEL_RECT.width * 0.2);

		Polygon bounds = Utils.mkCenteredRect(
			new Vec(PANEL_RECT.getCenterX() - V_WIDTH / 2, PANEL_RECT.y + Y_OFFSET + HEIGHT / 2),
			WIDTH / 2,
			HEIGHT / 2
		);
		Runnable onPress = () -> {
			layoutInput = LayoutInput.ASK_NAME;
			activeLayout = layout;
			brushwork.defEnDepth = activeLayout.defEnDepth;
			console.forceShow().print("Enter a new name for " + layout.name);
		};
		Sizer cam = Sizer.DEF;
		
		Button renameBtn = new Button(cursor, bounds, onPress, cam);
		renameBtn.def = Color.DARK_GRAY.darker();
		renameBtn.depth = -12;
		layout.renameBtn = renameBtn;
		
		//	Visibility button
		Polygon vBounds = Utils.mkCenteredRect(
			new Vec(PANEL_RECT.getCenterX() + 10 + WIDTH / 2, PANEL_RECT.y + Y_OFFSET + HEIGHT / 2),
			V_WIDTH / 2,
			HEIGHT / 2
		);
		
		Button visibilityBtn = new Button();
		
		Runnable vOnPress = () -> {
			layout.visible = !layout.visible;
			visibilityBtn.def = layout.visible ? Color.BLACK : Color.GRAY;
		};

		visibilityBtn.init(cursor, vBounds, vOnPress, cam);
		
		visibilityBtn.def = Color.BLACK;
		visibilityBtn.depth = -12;
		layout.visibilityBtn = visibilityBtn;
		
		//	Select button
		final int S_WIDTH = (int) (PANEL_RECT.width * 0.9);
		Polygon sBounds = Utils.mkCenteredRect(
			new Vec(PANEL_RECT.getCenterX(), PANEL_RECT.y + Y_OFFSET + S_HEIGHT / 2),
			S_WIDTH / 2,
			S_HEIGHT / 2
		);
		Runnable sOnPress = () -> {
			activeLayout = layout;
			brushwork.defEnDepth = activeLayout.defEnDepth;
		};
		
		Button selectBtn = new Button(cursor, sBounds, sOnPress, cam);
		selectBtn.def = Color.DARK_GRAY;
		selectBtn.depth = -11;
		layout.selectBtn = selectBtn;
		
		layout.groupBtns();
		layout.defEnDepth = defEnDepth;

		return layout;
	}
	
	public void render(ArrayList<Runnable> renderers) {
		for (Button button : buttonGroup.buttons)
			Utils.draw(button, renderers);
		layouts.forEach(layer -> layer.render(renderers));
		renderers.add(() -> Game.get().draw(g -> activeLayout.selectBtn.bounds.drawFilled(g,
			Utils.diffAlpha(Color.ORANGE, 100), activeLayout.selectBtn.cam), activeLayout.selectBtn.depth));
	}

	public void disableControls() {
		cursorPress = false;
		cursorHover = false;
	}
	
}
