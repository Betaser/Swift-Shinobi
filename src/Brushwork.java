package state.levelDesignState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import base.Game;
import console.Console;
import entity.Entity;
import entity.tile.SolidTile;
import entity.tile.Tile;
import helper.Utils;
import math.Vec;
import miscObjects.Noted;
import ui.Controllable;

public class Brushwork implements Controllable {
	
	private Vec cursor;
	private Vec ogCursorPos;
	private Vec center;
	private ArrayList<Entity> brush;
	private HashMap<Entity, Vec> ogPositions;
	private enum Mode {
		SELECT,
		DRAW,
	}
	private Mode mode;
	private enum ASK_DEPTH {
		ASK_DEPTH
	}
	private Mode prevMode;
	private Console console;
	
	public double defEnDepth;
	public Selectwork selectwork;
	public boolean brushTool;
	public boolean paint;
	public boolean selectTool;
	
	public void init(Vec cursor) {
		this.cursor = cursor;
		console = Game.get().console();
		mode = Mode.DRAW;
		prevMode = mode;
		ogCursorPos = new Vec();
		center = new Vec();
		brush = new ArrayList<>();
		ogPositions = new HashMap<>();
		
		brushTool = true;
		paint = false;
		selectTool = false;
		
		selectwork = new Selectwork();
		selectwork.init(cursor);
	}
	
	public void disableControls() {
		brushTool = false;
		paint = false;
		selectTool = false;
	}
	
	public void findControls() {
		brushTool = Game.get().input.brushTool.hold();
		paint = Game.get().input.paint.hold();
		selectTool = Game.get().input.selectTool.hold();
	}
	
	public void setBrush(List<Entity> brush, Camwork camwork) {
		toolInit(camwork, brush, brush);
	}
	
	public void toolInit(Camwork camwork, Collection<Entity> brushEns, Collection<Entity> validEntities) {
		ogPositions.clear();
		brush.clear();
		
		ogCursorPos.setCart(cursor);
		camwork.camera.invert(List.of(ogCursorPos));

		List<Entity> toAdd = null;
		boolean emptyBrush = brushEns.size() == 0;
		if (!emptyBrush) 
			toAdd = brushEns
				.stream()
				.filter(validEntities::contains)
				.map(Entity::clone)
				.peek(Entity::init)
				.collect(Collectors.toList());
		
		if (emptyBrush || toAdd.size() == 0) {
			brush.add(defBrush(ogCursorPos));
			center.setCart(ogCursorPos);
		} else {
			brush.addAll(toAdd);
			
			double xMin = brush.stream().flatMap(en -> en.bounds.vertices.stream())
				.map(Vec::getX)
				.min(Double::compare).get();
			double xMax = brush.stream().flatMap(en -> en.bounds.vertices.stream())
				.map(Vec::getX)
				.max(Double::compare).get();
			double yMin = brush.stream().flatMap(en -> en.bounds.vertices.stream())
				.map(Vec::getY)
				.min(Double::compare).get();
			double yMax = brush.stream().flatMap(en -> en.bounds.vertices.stream())
				.map(Vec::getY)
				.max(Double::compare).get();
			center.setCart(xMin + (xMax - xMin) / 2, yMin + (yMax - yMin) / 2);
		}
		
		for (Entity en : brush) {
			en.cam = camwork.camera;
			ogPositions.put(en, en.bounds.pos().clone());
		}
	}
	
	private void checkIfUserAskDepth() {
		Noted<Console.InputValidity, String> okInput = console.getLastOkInput();
		
		switch (okInput.enumVal) {
			case NULL: {
				break;
			}
			case OK, CALLED: {
				if (!console.callingAndFits(ASK_DEPTH.ASK_DEPTH, s -> s.split(" ")[0].equals("set-depth")))
					break;
				
				console.setCurrCaller(ASK_DEPTH.ASK_DEPTH);
				String input = okInput.val;

				//	Parse input, in the form of "set-depth 5.2"
				String[] inputs = input.split(" ");
				
				if (inputs[0].equals("set-depth")) {
					if (!Utils.isNum(inputs[1]))
						break;
					double depth = Double.parseDouble(inputs[1]);
					console.forceShow();
					console.print("set brush depth to " + depth);
					console.forceHide();
					defEnDepth = depth;
				}
				
				break;
			}
		}
	}
	
	public void tick(LevelDesignState.Tool prevTool, ArrayList<Entity> entities, Camwork camwork) {
		if (brushTool)
			mode = Mode.DRAW;
		if (selectTool)
			mode = Mode.SELECT;
		
		checkIfUserAskDepth();
		
		switch (mode) {
			case SELECT: {
				selectwork.tick(entities, camwork);
				break;
			}
			case DRAW: {
				if (prevMode == Mode.SELECT || prevTool != LevelDesignState.Tool.BRUSH) {
					toolInit(camwork, selectwork.selected, entities);
				}
				
				moveBrush(camwork);
				tryPlaceEntities(selectwork.selected, entities);
				break;
			}
		}
		
		prevMode = mode;
	}
	
	public void tryPlaceEntities(HashSet<Entity> selected, ArrayList<Entity> entities) {
		if (!paint)
			return;

		for (Entity en : brush) {
			Entity enAtGrid = entities.stream()
				.filter(e -> e.bounds.pos().equals(en.bounds.pos()))
				.findFirst()
				.orElse(null);

			Entity clone = en.clone();
			clone.init();
			clone.depth = defEnDepth;
			entities.add(clone);

			if (enAtGrid != null) {
				entities.remove(enAtGrid);
				if (selected.remove(enAtGrid))
					selected.add(clone);
			}
		}
	}
	
	private void moveBrush(Camwork camwork) {		
		Vec cursorPos = cursor.clone();
		camwork.camera.invert(List.of(cursorPos));

		for (Entity en : brush) {
			Vec cursorOffset = cursorPos.clone().sub(ogCursorPos);
			Vec centerOffset = ogCursorPos.clone().sub(center);
			Vec newPos = ogPositions.get(en).clone()
				.add(cursorOffset)
				.add(centerOffset)
				.add(new Vec(Tile.DEF_SIZE / 2, Tile.DEF_SIZE / 2));
			
			newPos.setX((Math.floor(newPos.getX() / Tile.DEF_SIZE)) * Tile.DEF_SIZE);
			newPos.setY((Math.floor(newPos.getY() / Tile.DEF_SIZE)) * Tile.DEF_SIZE);
			
			en.bounds.moveTo(newPos);
		}
	}
	
	public void render(ArrayList<Runnable> renderers) {
		switch (mode) {
			case SELECT: {
				selectwork.render(renderers);
				break;
			}
			case DRAW: {
				for (Entity en : brush)
					renderers.add(() -> Game.get().draw(en.render, en.depth));
				break;
			}
		}
	}
	
	private Tile defBrush(Vec cursorPos) {
		Tile t = new SolidTile();
		t.init();
		t.bounds.set(Utils.mkCenteredSquare(cursorPos, Tile.DEF_SIZE / 2));
		return t;
	}

}