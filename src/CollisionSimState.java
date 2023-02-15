package state;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import base.Game;
import helper.Calc;
import helper.Constants;
import helper.Utils;
import math.Polygon;
import math.Sizer;
import math.Vec;
import miscObjects.Intersection;
import miscObjects.Point;
import miscObjects.ShortestCollisionInfo;

public class CollisionSimState extends State {
	
	private enum SimState {
		NONE,
		HAS_1,
		PLACED_1,
		HAS_2,
		PLACED_2,
		SIM_RAN
	}
	
	SimState simState;
	final int DRAW_CURSOR_I = 0;
	Point polygonDest;
	ArrayList<Runnable> drawCalls;
	HashSet<Runnable> drawCallsToRemove;
	PolygonPresets navPolygons;
	Polygon polygon;
	Polygon displacedP;
	Polygon cursorP;
	Vec pDisplacement;
	Vec cursor;
	Sizer camera;
	
	public void _init() {
		simState = SimState.NONE;
		
		polygonDest = new Point();
		
		polygon = Utils.mkCenteredSquare(new Vec(20, 20), 20);
		cursorP = polygon.cloneVertices();
		displacedP = polygon.cloneVertices();
		
		drawCalls = new ArrayList<>();
		drawCallsToRemove = new HashSet<>();
		drawCalls.add(drawCursor(Color.WHITE));

		cursor = new Vec();
		
		navPolygons = PolygonPresets.EX1;
		drawCalls.add(() -> navPolygons.draw(camera));
		
		camera = Sizer.DEF;
	}
	
	Runnable drawCursor(Color color) {
		return () -> Game.get().draw(g -> cursorP.draw(g, color, camera), Constants.EN_DEPTH);
	}
	
	public void _tick() {
		cursor.setCart(Game.get().input.mouseX, Game.get().input.mouseY);

		cursorP.moveTo(cursor.clone().add(-20, -20));
		
		switch (simState) {
			case NONE: {
				if (Game.get().input.lMouse.unclick()) {
					simState = SimState.HAS_1;
					drawCalls.set(DRAW_CURSOR_I, drawCursor(Color.GREEN.brighter()));
				}
				break;
			}
			case HAS_1: {
				if (Game.get().input.lMouse.unclick()) {
					simState = SimState.PLACED_1;
					polygon.moveTo(cursorP.pos());
					Runnable drawPlacedAt1 = () -> Game.get().draw(g -> polygon.draw(g, Color.GREEN, camera), Constants.EN_DEPTH);
					drawCalls.add(drawPlacedAt1);
					drawCallsToRemove.add(drawPlacedAt1);
					drawCalls.set(DRAW_CURSOR_I, drawCursor(Color.WHITE));
				}
				break;
			}
			case PLACED_1: {
				if (Game.get().input.lMouse.unclick()) {
					simState = SimState.HAS_2;
					drawCalls.set(DRAW_CURSOR_I, drawCursor(Color.MAGENTA.brighter()));
				}
				break;
			}
			case HAS_2: {
				if (Game.get().input.lMouse.unclick()) {
					simState = SimState.PLACED_2;
					polygonDest.x = Game.get().input.mouseX - 20;
					polygonDest.y = Game.get().input.mouseY - 20;
					pDisplacement = new Vec(polygonDest.x - polygon.pos().getX(),
						polygonDest.y - polygon.pos().getY());
					displacedP.moveTo(polygon.pos().clone().add(pDisplacement));
					Runnable drawPlacedAt2 = () -> Game.get().draw(g -> displacedP.draw(g, Color.MAGENTA, camera), Constants.EN_DEPTH);
					drawCalls.add(drawPlacedAt2);
					drawCallsToRemove.add(drawPlacedAt2);
					drawCalls.set(DRAW_CURSOR_I, drawCursor(Color.WHITE));
				}
				break;
			}
			case PLACED_2: {
				if (Game.get().input.lMouse.unclick()) {
					simState = SimState.SIM_RAN;
					drawCalls.set(DRAW_CURSOR_I, drawCursor(Color.YELLOW));
					
					//	First run thru collisions
					List<Intersection> intersections = Calc.loopThruGetIntersections(polygon, pDisplacement, navPolygons.polygons);
					for (Intersection intersection : intersections) {
						Polygon square = Utils.mkCenteredSquare(intersection.pos(), 5);
						Color translucentRed = new Color(255, 30, 30, 55);
						Color translucentBlue = new Color(20, 255, 255, 85);
						Runnable drawSquare = () -> Game.get().draw(g -> square.draw(g, 
							intersection.withinLine1() && intersection.withinLine2() ? translucentBlue : translucentRed, camera), 
								Constants.EN_DEPTH);
						drawCallsToRemove.add(drawSquare);
						drawCalls.add(drawSquare);
					}
					
					Optional<ShortestCollisionInfo> collisionInfo = Calc.shortestCollisionInfo(polygon, pDisplacement, navPolygons.polygons);
					if (collisionInfo.isEmpty()) {
						break;
					}
					
					ShortestCollisionInfo cInfo = collisionInfo.get();
					Vec move = Calc.movePolygon(cInfo);
					Vec slideMove = Calc.slideMove(move, pDisplacement, cInfo);
					
					Runnable drawRealPos = () -> {
						Polygon cpy = polygon.cloneVertices();
						cpy.translate(move);
						Game.get().draw(g -> cpy.draw(g, Color.ORANGE, camera), Constants.EN_DEPTH);
					};
					drawCalls.add(drawRealPos);
					drawCallsToRemove.add(drawRealPos);
					
					Runnable drawShadowedPos = () -> {
						Polygon cpy = polygon.cloneVertices();
						cpy.translate(move);
						cpy.translate(slideMove);
						Game.get().draw(g -> cpy.draw(g, Color.WHITE, camera), Constants.EN_DEPTH);
					};
					drawCalls.add(drawShadowedPos);
					drawCallsToRemove.add(drawShadowedPos);
					
					Runnable drawCollisionPos = () -> Game.get().draw(g -> Utils.mkCenteredSquare(cInfo.pos(), 3)
						.draw(g, Color.YELLOW.darker(), camera), Constants.EN_DEPTH);
					drawCalls.add(drawCollisionPos);
					drawCallsToRemove.add(drawCollisionPos);
					
					Runnable drawCorner = () -> Game.get().draw(g -> Utils.mkCenteredSquare(cInfo.collisionInfo().corner, 3)
						.draw(g, Color.YELLOW.brighter(), camera), Constants.EN_DEPTH);
					drawCalls.add(drawCorner);
					drawCallsToRemove.add(drawCorner);
					
					Runnable drawSide = () -> {
						BufferedImage layer = Game.get().createLayer();
						Graphics g = layer.getGraphics();
						g.setColor(Color.RED);
						Game.get().drawLine(
							g,
							cInfo.collisionInfo().side.p1.getXInt(),
							cInfo.collisionInfo().side.p1.getYInt(),
							cInfo.collisionInfo().side.p2.getXInt(),
							cInfo.collisionInfo().side.p2.getYInt(),
							camera
						);
						Game.get().draw(layer, Constants.EN_DEPTH);
					};
					drawCalls.add(drawSide);
					drawCallsToRemove.add(drawSide);
						
					//	Second run thru collisions
					Optional<ShortestCollisionInfo> collisionInfo2 = Calc.shortestCollisionInfo(
						polygon.cloneVertices().translate(move), slideMove, navPolygons.polygons);
					if (collisionInfo2.isEmpty()) {
						break;
					}
					
					ShortestCollisionInfo cInfo2 = collisionInfo2.get();
					Vec move2 = Calc.movePolygon(cInfo2);
					
					Runnable drawFinalPos = () -> {
						Polygon cpy = polygon.cloneVertices();
						cpy.translate(move);
						cpy.translate(move2);
						Game.get().draw(g -> cpy.draw(g, Color.PINK, camera), Constants.EN_DEPTH);
					};
					drawCalls.add(drawFinalPos);
					drawCallsToRemove.add(drawFinalPos);
					
					Runnable drawCollisionPos2 = () -> Game.get().draw(g -> Utils.mkCenteredSquare(cInfo2.pos(), 3)
						.draw(g, Color.YELLOW.darker(), camera), Constants.EN_DEPTH);
					drawCalls.add(drawCollisionPos2);
					drawCallsToRemove.add(drawCollisionPos2);
					
					Runnable drawCorner2 = () -> Game.get().draw(g -> Utils.mkCenteredSquare(cInfo2.collisionInfo().corner, 3)
						.draw(g, Color.YELLOW.brighter(), camera), Constants.EN_DEPTH);
					drawCalls.add(drawCorner2);
					drawCallsToRemove.add(drawCorner2);
					
					Runnable drawSide2 = () -> {
						BufferedImage layer = Game.get().createLayer();
						Graphics g = layer.getGraphics();
						g.setColor(Color.RED);
						Game.get().drawLine(
							g,
							cInfo2.collisionInfo().side.p1.getXInt(),
							cInfo2.collisionInfo().side.p1.getYInt(),
							cInfo2.collisionInfo().side.p2.getXInt(),
							cInfo2.collisionInfo().side.p2.getYInt(),
							camera
						);
						Game.get().draw(layer, Constants.EN_DEPTH);
					};
					drawCalls.add(drawSide2);
					drawCallsToRemove.add(drawSide2);
				}
				break;
			}
			case SIM_RAN: {
				if (Game.get().input.lMouse.unclick()) {
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
