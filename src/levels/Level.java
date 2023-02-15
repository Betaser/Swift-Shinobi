package levels;

import java.util.ArrayList;

import base.Game;
import entity.Entity;
import entity.tile.Tile;
import helper.Utils;
import math.Sizer;
import math.Vec;

public class Level {

	//	So we want the concept of entities existing on different layers, for parallax
	//	but more importantly for rendering and levelDesignState
	//	Either we can have entity depths be a part of the data structure holding entities
	//	or (and I suppose this makes more sense for possible changing of "depth") make it a part of entities
	//	Then "layers" in levelDesignState would just be a collection of entities with the same depth
	//	and you would set the depth of entities by creating one in the layer of the correct depth
	
	//	I'm going with having entities have a depth field
	public ArrayList<Entity> entities;
	public ArrayList<Tile> tiles;
	
	public Level() {
		entities = new ArrayList<>();
		tiles = new ArrayList<>();
	}
	
	public void init() {
		entities.forEach(Entity::init);
	}
	
	public void tick() {
		tiles.clear();
		for (Entity en : entities)
			if (en instanceof Tile)
				tiles.add((Tile) en);
		entities.forEach(e -> e.tick.run());
	}
	
	public static ArrayList<Tile> loadTiles(String[] gridOfIds) {
		ArrayList<Tile> tiles = new ArrayList<>();

		for (int y = 0; y < gridOfIds.length; y++) {
			String[] row = gridOfIds[y].split(" ");
			for (int x = 0; x < row.length; x++) {
				Tile tile = Tile.fromId(Integer.parseInt(row[x]));
				tile.init();
				tile.bounds.set(Utils.mkCenteredSquare(new Vec(), Tile.DEF_SIZE / 2));
				tile.bounds.moveTo(new Vec(x * Tile.DEF_SIZE, (gridOfIds.length - y - 1) * Tile.DEF_SIZE));
				Vec center = new Vec(Tile.DEF_SIZE * row.length / 2, Tile.DEF_SIZE * gridOfIds.length / 2);
				tile.cam = new Sizer(new Vec(Game.get().getWidth() / 2, Game.get().getHeight() / 2).sub(center).neg(), center, 1);
				tiles.add(tile);
			}
		}
		
		return tiles;
	}
	
	public void render(ArrayList<Runnable> renderers) {
		entities.forEach(en -> renderers.add(() -> en.render.accept(Game.get().getGraphics(en.depth))));
	}
	
}
