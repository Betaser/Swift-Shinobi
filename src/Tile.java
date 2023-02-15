package entity.tile;

import java.util.List;

import entity.Entity;

public abstract class Tile extends Entity {
	
	public static final double DEF_SIZE = 30;

	public Tile() {
		
	}
	
	public void init() {}
	
	public static List<Tile> tiles() {
		return List.of(
			new WallTile(),
			new SolidTile()
		);
	}
	
	public static Tile fromId(int id) {
		return Entity.entities()
			.stream()
			.filter(e -> e instanceof Tile)
			.map(e -> (Tile) e)
			.peek(Tile::init)
			.filter(t -> t.getId() == id)
			.findFirst()
			.orElse(null);
	}
	
}
