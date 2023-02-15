package state.levelDesignState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import java.awt.Color;

import base.Game;
import entity.Entity;
import helper.Utils;
import math.Vec;
import ui.Controllable;

public class Selectwork implements Controllable {
	
	protected Vec cursor;
	
	public HashSet<Entity> selected;
	public HashSet<Entity> hovered;
	public boolean select;
	public boolean deselect;
	public boolean hovering;
	
	public void init(Vec cursor) {
		this.cursor = cursor;
		selected = new HashSet<>();
		hovered = new HashSet<>();
		select = false;
		deselect = false;
		hovering = true;
	}
	
	public void disableControls() {
		select = false;
		deselect = false;
		hovering = false;
	}
	
	public void findControls() {
		select = Game.get().input.select.hold();
		deselect = Game.get().input.deselect.hold();
		hovering = true;
	}
	
	public void tick(ArrayList<Entity> entities, Camwork camwork) {
		manageHoveredEntities(entities, camwork);
		trySelectEntities(entities, camwork);
	}
	
	public void manageHoveredEntities(ArrayList<Entity> entities, Camwork camwork) {
		hovered.clear();
		Vec cursorPos = cursor.clone();
		camwork.camera.invert(List.of(cursorPos));

		if (hovering) {
			for (Entity en : entities) {
				if (en.bounds.surroundsPoint(cursorPos))
					hovered.add(en);
			}
		}
	}
	
	protected void trySelectEntities(ArrayList<Entity> entities, Camwork camwork) {
		if (!select && !deselect)
			return;
		
		Vec cursorPos = cursor.clone();
		camwork.camera.invert(List.of(cursorPos));
		
		for (Entity en : entities) {
			if (en.bounds.surroundsPoint(cursorPos)) {
				if (select)
					selected.add(en);
				if (deselect)
					selected.remove(en);
			}
		}
	}
	
	public void render(ArrayList<Runnable> renderers) {
		for (Entity en : selected)
			renderers.add(() -> Game.get().draw(g -> en.renderFilled(g, Utils.diffAlpha(Color.MAGENTA, 75)), en.depth - 1));
		for (Entity en : hovered)
			renderers.add(() -> Game.get().draw(g -> en.renderFilled(g, Utils.diffAlpha(Color.CYAN, 75)), en.depth - 2));
	}

}
