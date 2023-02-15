package state.levelDesignState;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import base.Game;
import entity.Entity;
import entity.tile.Tile;
import helper.Utils;
import math.Vec;

public class EraserSelectwork extends Selectwork {

	public void tick(ArrayList<Entity> entities, Camwork camwork) {
		//	You should be able to "select" any square on the grid
		ArrayList<Entity> entitiesInGrid = new ArrayList<>(entities);
		
		Vec btmLeft = new Vec(0, 0);
		Vec topRight = new Vec(Game.get().getWidth(), Game.get().getHeight());
		camwork.camera.invert(List.of(btmLeft, topRight));
		btmLeft.setX((int) (btmLeft.getX() / Tile.DEF_SIZE) * Tile.DEF_SIZE);
		btmLeft.setY((int) (btmLeft.getY() / Tile.DEF_SIZE) * Tile.DEF_SIZE);
		topRight.setX((int) (topRight.getX() / Tile.DEF_SIZE) * Tile.DEF_SIZE);
		topRight.setY((int) (topRight.getY() / Tile.DEF_SIZE) * Tile.DEF_SIZE);
		
		final double LEFT_BOUND = btmLeft.getX();
		final double RIGHT_BOUND = topRight.getX();
		final double BTM_BOUND = btmLeft.getY();
		final double TOP_BOUND = topRight.getY();
		
		for (double i = LEFT_BOUND / Tile.DEF_SIZE; i <= RIGHT_BOUND / Tile.DEF_SIZE; i++) {
			for (double j = BTM_BOUND / Tile.DEF_SIZE; j <= TOP_BOUND / Tile.DEF_SIZE; j++) {
				Vec pos = new Vec(i * Tile.DEF_SIZE, j * Tile.DEF_SIZE);
				if (entities.stream()
					.filter(e -> e.bounds.pos().equals(pos))
					.findFirst()
					.isPresent()) {
					continue;
				}

				EraserEntity eraserEn = new EraserEntity();
				eraserEn.init();
				eraserEn.bounds.set(Utils.mkCenteredSquare(new Vec(), Tile.DEF_SIZE / 2));
				eraserEn.bounds.moveTo(pos);
				eraserEn.cam = camwork.camera;
				entitiesInGrid.add(eraserEn);
			}
		}

		super.tick(entitiesInGrid, camwork);
	}
	
	protected void trySelectEntities(ArrayList<Entity> entities, Camwork camwork) {
		if (!select && !deselect)
			return;
		
		Vec cursorPos = cursor.clone();
		camwork.camera.invert(List.of(cursorPos));
		
		for (Entity en : entities) {
			if (en.bounds.surroundsPoint(cursorPos)) {
				if (select && selected.stream()
					.filter(e -> e.bounds.pos().equals(en.bounds.pos()))
					.findFirst()
					.isEmpty())
					selected.add(en);
				if (deselect)
					selected.remove(en);
			}
		}
	}
	
	public void render(ArrayList<Runnable> renderers) {
		for (Entity en : selected)
			renderers.add(() -> Game.get().draw(g -> en.renderFilled(g, Utils.diffAlpha(Color.ORANGE, 75)), en.depth - 1));
		for (Entity en : hovered)
			renderers.add(() -> Game.get().draw(g -> en.renderFilled(g, Utils.diffAlpha(Color.GREEN, 75)), en.depth - 2));
	}
	
}
