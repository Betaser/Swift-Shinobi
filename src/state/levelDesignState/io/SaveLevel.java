package state.levelDesignState.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import base.Game;
import console.Console;
import entity.Entity;
import helper.Utils;
import math.Vec;
import miscObjects.Json;
import miscObjects.Noted;
import state.levelDesignState.layouts.Layout;
import state.levelDesignState.layouts.Layoutwork;

public class SaveLevel {
	
	Console console;
	
	public SaveLevel(Console console) {
		this.console = console;
	}
	
	public static void main(String[] args) {
		Json date = new Json();
		Date currDate = new Date(System.currentTimeMillis());
		DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy | HH:mm:ss");
		String dateStr = dateFormat.format(currDate);
		date.put("date", "\"" + dateStr + "\"");
		
		save(new File("res/levels/date.json"), date);
	}
	
	private enum SAVE_LEVEL {
		SAVE_LEVEL
	}
	public void manageSave(Layoutwork layoutwork) {
		if (Game.get().input.toggleConsole.click())
			System.out.println("toggle console");
		if (Game.get().input.save.click())
			console.forceShow().print("What will you name this level?", SAVE_LEVEL.SAVE_LEVEL);
		
		Noted<Console.InputValidity, String> okInput = console.getLastOkInput();
		
		switch (okInput.enumVal) {
			case NULL: {
				break;
			}
			case OK, CALLED: {
				if (!console.callingAndFits(SAVE_LEVEL.SAVE_LEVEL, s -> s.split(" ")[0].equals("save")))
					break;
				
				console.setCurrCaller(SAVE_LEVEL.SAVE_LEVEL);
				String input = okInput.val;
				
				String levelName = input.split(" ")[1];
				//	if input is null or level name already exists and this was a new level show error
				console.forceShow();
				//	if you had reopened this level then the text box will include the current saved name
				layoutwork.name = levelName;
				console.print("Level saved as " + levelName);
				save(layoutwork, levelName + ".json");
				console.forceHide();
				
				break;
			}
		}
	}
	
	private static void save(Layoutwork layoutwork, String levelName) {
		Json json = new Json();
		
		Date currDate = new Date(System.currentTimeMillis());
		DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy | HH:mm:ss");
		String dateStr = dateFormat.format(currDate);
		json.put("date", "\"" + dateStr + "\"");
		
		Json layouts = new Json();
		
		for (Layout l : layoutwork.layouts) {
			Json layout = new Json();
			
			layout.put("defEnDepth", l.defEnDepth);
			layout.put("entities", l.level.entities.stream()
				.map(Entity::toJson)
				.collect(Collectors.toList()));
			
			layouts.put(l.name, layout);
		}
		
		json.put("layouts", layouts);
		
		File file = new File("res/levels/" + levelName);
		save(file, json);
	}
	
	public static List<Entity> mockEntities() {
		Entity en1 = new Entity("Entity1");
		en1.bounds.set(Utils.mkRect(new Vec(0, 1), 30, 60));
		en1.depth = 0;
		Entity en2 = new Entity("Entity2");
		en2.bounds.set(Utils.mkRect(new Vec(2, -1), 30, 30));
		en2.depth = 1;
		return List.of(en1, en2);
	}
	
	public static void save(File file, Json data) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			writer.write(data.formatted());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void save(File file, List<? extends Entity> entities) {
		Json entitiesJson = new Json();
		for (Entity e : entities) {
			Json enJson = new Json();
			
			enJson.put("position", e.bounds.pos().toJson());
			
			enJson.put("depth", e.depth);
			
			Json weaponJson = new Json(
				Map.entry("damage", 12),
				Map.entry("element", "\"fire\"")
			);
			enJson.put("weapon", weaponJson);
			
			entitiesJson.put(e.getName(), enJson);
		}
		Json json = new Json(Map.of("entities", entitiesJson));

		save(file, json);
	}

}
