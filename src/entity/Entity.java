package entity;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import entity.tile.Tile;
import math.Polygon;
import math.Sizer;
import math.Vec;
import miscObjects.Json;

public class Entity {
	
	public static final double DEF_WIDTH = 30;
	public static final double DEF_HEIGHT = 30;
	public static int ID = 0;
	
	public Polygon bounds = new Polygon(List.of(new Vec(), new Vec(), new Vec()));
	public Vec velocity = new Vec();
	public double depth;
	public Sizer cam;
	
	protected String name = getClass().getSimpleName();

	public Entity() {
		cam = new Sizer(new Vec(), new Vec(DEF_WIDTH / 2, DEF_HEIGHT / 2), 1);
	}
	
	public Entity(String name) {
		this();
		this.name = name;
	}
	
	public void init() {
		
	}
	
	public final int getId() {
		ArrayList<Entity> entities = entities();
		for (int i = 0; i < entities.size(); i++)
			if (entities.get(i).getClass() == getClass())
				return i;
		
		return -1;
	}
	
	public void tick() {
		tickPos();
	}
	
	public Runnable tick = this::tick;
	
	public void tickPos() {
		bounds.translate(velocity);
	}
	
	public Consumer<Graphics> render = this::render;

	public void render(Graphics g) {
		bounds.draw(g, Color.WHITE, cam);
	}
	
	public void renderFilled(Graphics g, Color color) {
		bounds.drawFilled(g, color, cam);
	}
	
	public void cloneHelper(Entity clone) {
		clone.bounds = bounds.cloneVertices();
		clone.velocity = velocity.clone();
		clone.cam = cam;
		clone.depth = depth;
	}
	
	public Entity clone() {
		Entity clone = new Entity();
		cloneHelper(clone);
		return clone;
	}
	
	public void jsonInit(Json json) {
		bounds.pos().jsonInit(json);
		depth = json.getDouble("depth");
	}
	
	public Json toJson() {
		Json json = new Json();
		
		json.put("bounds", bounds.toJson());
		json.put("depth", depth);
		
		return json;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return getClass().getName() + getId() + bounds;
	}
	
	public static ArrayList<Entity> entities() {
		ArrayList<Entity> entities = new ArrayList<>();
		
		//	Populate entities with other static entity givers
		entities.addAll(Tile.tiles());

		return entities;
	}
	
	public static Entity fromId(int id) {
		return entities().get(id);
	}

}
