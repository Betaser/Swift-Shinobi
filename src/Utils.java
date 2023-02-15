package helper;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import base.Game;
import entity.Entity;
import math.Polygon;
import math.Vec;

public class Utils {
	
	public static void draw(Entity en, ArrayList<Runnable> renderers) {
		renderers.add(() -> en.render.accept(Game.get().getGraphics(en.depth)));
	}
	
	public static <T> boolean endsWith(List<T> list, List<T> matcher) {
		if (list == null || matcher == null)
			return false;
		if (matcher.size() > list.size())
			return false;
		for (int i = 0; i < matcher.size(); i++) {
			if (!list.get(list.size() - 1 - i).equals(matcher.get(matcher.size() - 1 - i)))
				return false;
		}
		return true;
	}
	
	public static <T> boolean startsWith(List<T> list, List<T> matcher) {
		if (list == null || matcher == null)
			return false;
		if (matcher.size() > list.size())
			return false;
		for (int i = 0; i < matcher.size(); i++)
			if (!list.get(i).equals(matcher.get(i)))
				return false;
		return true;
	}
	
	public static <T> T last(List<T> list) {
		if (list.size() == 0)
			return null;
		return list.get(list.size() - 1);
	}
	
	static final Pattern isNumPattern = Pattern.compile("-?\\d+(\\.\\d+)?");;
	public static boolean isNum(String str) {
		if (str == null)
			throw new NullPointerException();
		return isNumPattern.matcher(str).matches();
	}
	
	public static Color diffAlpha(Color color, int alpha) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
	}
	
	public static Vec unitDIR() {
		Vec unit = new Vec();
		if (Game.get().input.up.hold())
			unit.addY(1);
		if (Game.get().input.left.hold())
			unit.addX(-1);
		if (Game.get().input.down.hold())
			unit.addY(-1);
		if (Game.get().input.right.hold())
			unit.addX(1);
		unit.setMag(1);
		return unit;
	}
	
	public static Polygon mkRect(Vec lowerLeftCorner, double width, double height) {
		return new Polygon(List.of(
			new Vec(lowerLeftCorner.getX(), lowerLeftCorner.getY()),
			new Vec(lowerLeftCorner.getX() + width, lowerLeftCorner.getY()),
			new Vec(lowerLeftCorner.getX() + width, lowerLeftCorner.getY() + height),
			new Vec(lowerLeftCorner.getX(), lowerLeftCorner.getY() + height)
		));
	}
	
	public static Polygon mkSquare(Vec lowerLeftCorner, double width) {
		return mkRect(lowerLeftCorner, width, width);
	}
	
	public static Polygon mkCenteredRect(Vec center, double halfWidth, double halfHeight) {
		return new Polygon(List.of(
			new Vec(center.getX() - halfWidth, center.getY() - halfHeight),
			new Vec(center.getX() + halfWidth, center.getY() - halfHeight),
			new Vec(center.getX() + halfWidth, center.getY() + halfHeight),
			new Vec(center.getX() - halfWidth, center.getY() + halfHeight)
		));
	}
	
	public static Polygon mkCenteredSquare(Vec center, double halfWidth) {
		return mkCenteredRect(center, halfWidth, halfWidth);
	}
	
	public static <T, X extends Throwable> T checkNull(T obj, X exception) throws X {
		if (obj != null)
			return obj;
		throw exception;
	}

}
