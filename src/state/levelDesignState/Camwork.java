package state.levelDesignState;

import java.util.ArrayList;
import java.util.List;

import base.Game;
import entity.Entity;
import helper.Utils;
import math.Sizer;
import math.Vec;
import ui.Controllable;

public class Camwork implements Controllable {
	
	public Vec camPos;
	public Sizer camera;
	public boolean move;
	public boolean zoom;
	
	public void init(ArrayList<Entity> entitiesWhoNeedCamwork) {
		camera = new Sizer(new Vec(), new Vec(), 1);
		camPos = new Vec();
		for (Entity en : entitiesWhoNeedCamwork)
			en.cam = camera;
		recenterCamera(entitiesWhoNeedCamwork);
		calcCamera();
		move = false;
		zoom = false;
	}
	
	public void disableControls() {
		move = false;
		zoom = false;
	}
	
	public void findControls() {
		move = Utils.unitDIR().getMag() != 0;
		zoom = Game.get().input.scroll != 0;
	}
	
	public void tick(List<Entity> entities) {
		manageCameraMovement();
		manageCameraZoom();
		tryRecenterCamera(entities);
		calcCamera();
	}
	
	public void manageCameraMovement() {
		if (move)
			camPos.add(Utils.unitDIR().setMag(2));
	}
	
	public void manageCameraZoom() {
		if (zoom) {
			double zoom = Game.get().input.scroll > 0 ? 0.9 : 1 / 0.9;
			for (int i = 0; i < Math.abs(Game.get().input.scroll); i++)
				camera.setScale(camera.getScale() * zoom);
		}
	}
	
	public void calcCamera() {
		camera.pos.setCart(camPos.clone().add(camera.center));
	}
	
	public void tryRecenterCamera(List<Entity> entities) {
		if (Game.get().input.resetCam.click())
			recenterCamera(entities);
	}
	
	public void recenterCamera(List<Entity> entities) {
		if (entities.size() == 0)
			return;
		
		double xMin = entities.stream().flatMap(en -> en.bounds.vertices.stream())
			.map(Vec::getX)
			.min(Double::compare).get();
		double xMax = entities.stream().flatMap(en -> en.bounds.vertices.stream())
			.map(Vec::getX)
			.max(Double::compare).get();
		double yMin = entities.stream().flatMap(en -> en.bounds.vertices.stream())
			.map(Vec::getY)
			.min(Double::compare).get();
		double yMax = entities.stream().flatMap(en -> en.bounds.vertices.stream())
			.map(Vec::getY)
			.max(Double::compare).get();
		Vec center = new Vec(xMin + (xMax - xMin) / 2, yMin + (yMax - yMin) / 2);
		
		camera.center.setCart(center);
		camPos.setCart(-Game.get().getWidth() / 2, -Game.get().getHeight() / 2);
	}

}
