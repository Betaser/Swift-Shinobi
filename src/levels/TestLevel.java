package levels;

import java.util.ArrayList;

import entity.tile.Tile;

public class TestLevel extends Level {
	
	public TestLevel() {}
	
	public void init() {
		entities.clear();
		entities.addAll(loadTestLevel());
	}
	
	public static ArrayList<Tile> loadTestLevel() {
		String[] gridOfIds = {
			"1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1",
			"1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1",
			"1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1",
			"1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 1 1",
			"1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1",
			"1 0 0 0 0 0 0 0 0 0 0 0 1 1 0 0 0 0 0 0 1",
			"1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 1",
			"1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1",
			"1 0 0 0 0 0 0 1 1 1 0 0 0 0 0 0 1 0 0 0 1",
			"1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 1 0 0 0 1",
			"1 0 0 1 0 0 0 0 0 0 0 0 0 0 1 1 1 0 0 0 1",
			"1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1"
		};
		
		return loadTiles(gridOfIds);
	}
	
}
