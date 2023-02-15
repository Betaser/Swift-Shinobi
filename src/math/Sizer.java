package math;

import java.util.Collection;

public class Sizer {

	public Vec pos;
	public Vec center;
	
	private double scale;
	
	public static Sizer DEF = new Sizer(
		new Vec(),
		new Vec(),
		1
	);
	
	public Sizer(Vec pos, Vec center, double scale) {
		this.pos = pos;
		this.center = center;
		this.scale = scale;
	}
	
	public void resize(Collection<Vec> points) {
		for (Vec point : points) {
			point.setX(calcXPos(point.getX()));
			point.setY(calcYPos(point.getY()));
		}
	}
	
	public void invert(Collection<Vec> points) {
		for (Vec point : points) {
			point.setX(calcInvXPos(point.getX()));
			point.setY(calcInvYPos(point.getY()));
		}
	}
	
	public double calcLeftBound() {
		return calcXPos(pos.getX());
	}
	
	public double calcBtmBound() {
		return calcYPos(pos.getY());
	}
	
	public double calcWidth() {
		return 2 * Math.abs(center.getX() - pos.getX());
	}
	
	public double calcHeight() {
		return 2 * Math.abs(center.getY() - pos.getY());
	}
	
	public double calcXPos(double xPos) {
		double diff = xPos - center.getX();
		return -pos.getX() + center.getX() + diff * getScale();
	}
	
	public double calcYPos(double yPos) {
		double diff = yPos - center.getY();
		return -pos.getY() + center.getY() + diff * getScale();
	}
	
	public double calcInvXPos(double invXPos) {
		//	invXPos = -pos.getX() + center.getX() + (xPos - center.getX()) * getScale()
		//	invXPos + pos.getX() - center.getX() = (xPos - center.getX()) * getScale()
		//	(invXPos + pos.getX() - center.getX()) / getScale() = xPos - center.getX()
		//	(invXPos + pos.getX() - center.getY()) / getScale() + center.getX() = xPos
		double preScale = invXPos + pos.getX() - center.getX();
		return preScale / getScale() + center.getX();
	}
	
	public double calcInvYPos(double invYPos) {
		double preScale = invYPos + pos.getY() - center.getY();
		return preScale / getScale() + center.getY();
	}
	
	public void setScale(double scale) {
		this.scale = Math.max(0, scale);
	}
	
	public double getScale() {
		return scale;
	}
	
	public void moveTo(Vec pos) {
		this.pos.setCart(pos);
	}
	
	public Sizer clone() {
		return new Sizer(pos, center, scale);
	}
	
	public String toString() {
		return "pos " + pos + " center " + center + " scale " + scale;
	}
	
}
