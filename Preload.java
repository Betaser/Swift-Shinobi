package base.input;

import java.util.ArrayList;

import entity.Entity;

public class Preload {
	
	public static void check() {
		//	Throws exception if there are duplicates
		{
			ArrayList<Entity> entities = Entity.entities();
			
			for (int i = 0; i < entities.size(); i++) {
				for (int j = 0; j < i; j++) {
					Entity e1 = entities.get(i);
					Entity e2 = entities.get(j);
					if (e1 == e2 || e1.getClass() == e2.getClass())
						throw new RuntimeException("duplicate ids for " + e1 + " and " + e2);
				}
			}
		}
		System.out.println("Done checking!");
	}

}
