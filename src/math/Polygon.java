package math;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import base.Game;
import miscObjects.Json;

public class Polygon {
	
	public ArrayList<Vec> vertices;
	
	public Polygon(List<Vec> verticies) {
		this.vertices = new ArrayList<>(verticies);
	}
	
	public Json toJson() {
		return new Json(Map.entry("vertices", vertices
			.stream()
			.map(Vec::toJson)
			.collect(Collectors.toList())));
	}
	
	@SuppressWarnings("unchecked")
	public void jsonInit(Json json) {
		vertices.clear();
		vertices.addAll((List<Vec>) json.get("vertices"));
	}
	
	public Polygon set(Polygon other) {
		vertices = other.vertices;
		return this;
	}
	
	public Vec pos() {
		return vertices.get(0);
	}
	
	public Vec centerOffset() {
		double xMin = vertices.stream()
			.map(vtx -> vtx.getX())
			.min(Double::compare).get();
		double xMax = vertices.stream()
			.map(vtx -> vtx.getX())
			.max(Double::compare).get();
		double yMin = vertices.stream()
			.map(vtx -> vtx.getY())
			.min(Double::compare).get();
		double yMax = vertices.stream()
			.map(vtx -> vtx.getY())
			.max(Double::compare).get();
		return new Vec((xMax - xMin) / 2, (yMax - yMin) / 2);
	}
	
	public boolean surroundsPoint(Vec point) {
		java.awt.Polygon polygon = new java.awt.Polygon(
			vertices.stream().map(Vec::getXInt).mapToInt(i -> i).toArray(),
			vertices.stream().map(Vec::getYInt).mapToInt(i -> i).toArray(),
			vertices.size()
		);
		return polygon.contains(point.getX(), point.getY());
	}
	
	public Polygon resize(Sizer sizer) {
		sizer.resize(vertices);
		return this;
	}
	
	/*
	Treats the first vertex as the vertex to move to that position
	 */
	public Polygon moveTo(Vec position) {
		Vec firstVertex = vertices.get(0);
		translate(position.clone().sub(firstVertex));
		return this;
	}
	
	public Polygon translate(Vec displacement) {
		for (Vec vec : vertices)
			vec.add(displacement);
		return this;
	}
	
	public void drawFilled(Graphics g, Color color, Sizer gfxSizer) {
		g.setColor(color);
		Polygon sized = cloneVertices();
		gfxSizer.resize(sized.vertices);
		Game.get().fillPolygon(g, sized);
	}
	
	public void draw(Graphics g, Color color, Sizer gfxSizer) {
		g.setColor(color);
		for (int i = 0; i < vertices.size(); i++) {
			final Vec p1 = vertices.get(i);
			final Vec p2 = vertices.get((i + 1) % vertices.size());
			Game.get().drawLine(g, p1.getXInt(), p1.getYInt(), p2.getXInt(), p2.getYInt(), gfxSizer);
		}
	}
	
	public Polygon cloneVertices() {
		return new Polygon(vertices.stream().map(Vec::clone).collect(Collectors.toList()));
	}
	
	public Polygon clone() {
		return new Polygon(new ArrayList<>(vertices));
	}
	
	public String toString() {
		return "Polygon {" + vertices + "}";
	}
	
}
