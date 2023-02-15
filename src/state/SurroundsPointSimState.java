package state;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import java.awt.Color;

import base.Game;
import helper.Constants;
import helper.Utils;
import math.Polygon;
import math.Sizer;
import math.Vec;

public class SurroundsPointSimState extends State {
	
	private enum SimState {
		NONE,
		HAS,
		PLACED,
		SIM_RAN
	}
	
	SimState simState;
	final int DRAW_CURSOR_I = 0;
	Vec cursor;
	Sizer camera;
	Polygon polygon;
	Polygon cursorP;
	ArrayList<Runnable> drawCalls;
	HashSet<Runnable> drawCallsToRemove;
	
	public void _init() {
		simState = SimState.NONE;
		
		cursor = new Vec();
		camera = Sizer.DEF;
		
		polygon = new Polygon(List.of(
			new Vec(300, 200),
			new Vec(400, 220),
			new Vec(350, 300),
			new Vec(200, 270),
			new Vec(275, 230)
		));
		cursorP = Utils.mkCenteredSquare(new Vec(20, 20), 20);
		
		drawCalls = new ArrayList<>();
		drawCallsToRemove = new HashSet<>();
		drawCalls.add(drawCursor(Color.WHITE));
	}
	
	Runnable drawCursor(Color color) {
		return () -> Game.get().draw(g -> cursorP.draw(g, color, camera), Constants.EN_DEPTH);
	}
	
	Runnable drawPolygon(Color color) {
		return () -> Game.get().draw(g -> polygon.draw(g, color, camera), Constants.EN_DEPTH);
	}
	
	public void _tick() {
		cursor.setCart(Game.get().input.mouseX, Game.get().input.mouseY);
		
		cursorP.moveTo(cursor.clone().add(-20, -20));
		
		switch (simState) {
			case NONE: {
				if (Game.get().input.lMouse.unclick()) {
					simState = SimState.HAS;
					drawCalls.set(DRAW_CURSOR_I, drawCursor(Color.GREEN.brighter()));
					Runnable drawPolygon = drawPolygon(Color.WHITE);
					drawCalls.add(drawPolygon);
					drawCallsToRemove.add(drawPolygon);
				}
				break;
			}
			case HAS: {
				if (Game.get().input.lMouse.unclick()) {
					simState = SimState.PLACED;
					boolean clickedWithin = polygon.surroundsPoint(cursor);
					Runnable drawPolygon = clickedWithin
						? () -> Game.get().draw(g -> polygon.drawFilled(g, Color.CYAN, camera), Constants.EN_DEPTH)
						: () -> Game.get().draw(g -> polygon.draw(g, Color.RED, camera), Constants.EN_DEPTH);
					drawCalls.add(drawPolygon);
					drawCallsToRemove.add(drawPolygon);
				}
				break;
			}
			case PLACED: {
				if (Game.get().input.lMouse.unclick()) {
					simState = SimState.SIM_RAN;
					drawCalls.set(DRAW_CURSOR_I, drawCursor(Color.YELLOW));
				}
				break;
			}
			case SIM_RAN: {
				if (Game.get().input.lMouse.click()) {
					simState = SimState.NONE;
					drawCalls.set(DRAW_CURSOR_I, drawCursor(Color.WHITE));
					drawCalls.removeAll(drawCallsToRemove);
					drawCallsToRemove.clear();
				}
				break;
			}
		}
	}
	
	public void _render() {
		for (Runnable drawCall : drawCalls)
			drawCall.run();
	}

}
