package state.levelDesignState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import base.Game;
import entity.Entity;
import entity.tile.Tile;
import helper.Utils;
import math.Polygon;
import math.Vec;
import ui.Controllable;

public class Eraserwork implements Controllable {
	
	private Vec cursor;
	private Vec ogCursorPos;
	private Vec center;
	private ArrayList<Entity> brush;
	private HashMap<Entity, Vec> ogPositions;
	private enum Mode {
		ERASE,
		SELECT
	}
	private Mode mode;
	private Mode prevMode;
	
	public Selectwork selectwork;
	public boolean eraserTool;
	public boolean erase;
	public boolean selectTool;
	
	public void init(Vec cursor) {
		this.cursor = cursor;
		mode = Mode.ERASE;
		prevMode = mode;
		ogCursorPos = new Vec();
		center = new Vec();
		brush = new ArrayList<>();
		ogPositions = new HashMap<>();
		
		erase = true;
		
		selectwork = new EraserSelectwork();
		selectwork.init(cursor);
	}
	
	public void disableControls() {
		erase = false;
	}
	
	public void findControls() {
		eraserTool = Game.get().input.eraserTool.hold();
		erase = Game.get().input.erase.hold();
		selectTool = Game.get().input.selectTool.hold();
	}
	
	public void toolInit(Camwork camwork) {
		ogPositions.clear();
		brush.clear();
		
		ogCursorPos.setCart(cursor);
		camwork.camera.invert(List.of(ogCursorPos));
		
		if (selectwork.selected.size() == 0) {
			brush.add(defBrush(ogCursorPos));
			center.setCart(ogCursorPos);
		} else {
			brush.addAll(selectwork.selected
				.stream()
				.map(Entity::clone)
				.peek(Entity::init)
				.collect(Collectors.toList()));
			
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
	
	public void tick(LevelDesignState.Tool prevTool, ArrayList<Entity> entities, Camwork camwork) {
		if (eraserTool)
			mode = Mode.ERASE;
		if (selectTool)
			mode = Mode.SELECT;
		
		switch (mode) {
			case SELECT: {
				selectwork.tick(entities, camwork);
				break;
			}
			case ERASE: {
				if (prevMode != Mode.ERASE || prevTool != LevelDesignState.Tool.ERASER) {
					toolInit(camwork);
				}
				
				moveBrush(camwork);
				tryDeleteEntities(selectwork.selected, entities);
				break;
			}
		}
		
		prevMode = mode;
	}
	
	public void tryDeleteEntities(HashSet<Entity> selected, ArrayList<Entity> entities) {
		if (!erase)
			return;
		
		for (Entity en : brush) {
			Entity enAtGrid = entities.stream()
				.filter(e -> e.bounds.pos().equals(en.bounds.pos()))
				.findFirst()
				.orElse(null);
			
			if (enAtGrid != null) {
				entities.remove(enAtGrid);
				selected.remove(enAtGrid);
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
	
	private Entity defBrush(Vec cursorPos) {
		Polygon bounds = Utils.mkCenteredSquare(cursorPos, Tile.DEF_WIDTH / 2);
		EraserEntity en = new EraserEntity();
		en.init();
		en.bounds = bounds;
		return en;
	}
	
	public void render(ArrayList<Runnable> renderers) {
		switch (mode) {
			case SELECT: {
				selectwork.render(renderers);
				break;
			}
			case ERASE: {
				for (Entity en : brush)
					renderers.add(() -> Game.get().draw(en.render, en.depth));
				break;
			}
		}
	}
	
}
