package math;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Map;
import java.util.Objects;

import helper.Calc;
import miscObjects.Json;

public class Vec {
	
	double groundFriction = 1, wallFriction = 1, clampX, clampY, x, y;
	
	boolean isPolar;
	public boolean isFrozen;
	
	public Vec() {
		x = 0.0;
		y = 0.0;
	}
	
	public Vec(miscObjects.Point point) {
		x = point.x;
		y = point.y;
	}
	
	public Vec(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vec(double groundFriction, double wallFriction, double clampX, double clampY) {
		this(groundFriction, wallFriction, clampX, clampY, 0, 0);
	}
	
	public Vec(double groundFriction, double wallFriction, double clampX, double clampY, double x, double y) {
		if (groundFriction < 1)
			throw new RuntimeException("groundFriction " + groundFriction + " shouldn't be less than 1");
		if (wallFriction < 1)
			throw new RuntimeException("wallFriction " + wallFriction + " shouldn't be less than 1");
		this.groundFriction = (float) groundFriction;
		this.wallFriction = (float) wallFriction;
		this.clampX = (float) clampX;
		this.clampY = (float) clampY;
		this.x = x;
		this.y = y;
	}
	
	public static int pol(double d) {
		if (d == 0)
			return 0;
		if (d < 0)
			return -1;
		return 1;
	}
	
	public Vec neg() {
		toCart();
		x *= -1;
		y *= -1;
		return this;
	}
	
	public Point toPoint() {
		return new Point(getXInt(), getYInt());
	}
	
	public static Vec fromRect(Rectangle rect) {
		return new Vec(rect.x, rect.y);
	}
	
	public void applyWallFriction(float wallFriction) {
		setY(getY() / wallFriction);
	}
	
	public void applyGroundFriction(float groundFriction) {
		setX(getX() / groundFriction);
	}
	
	public static Vec RIGHT() {
		return new Vec(1, 0);
	}
	
	public static Vec LEFT() {
		return new Vec(-1, 0);
	}
	
	public static Vec UP() {
		return new Vec(0, 1);
	}
	
	public static Vec DOWN() {
		return new Vec(0, -1);
	}
	
	public boolean isZero() {
		return getX() == 0 && getY() == 0;
	}
	
	public boolean isLeft() {
		if (isZero())
			return false;
		return getX() < 0 && getY() == 0;
	}
	
	public boolean isRight() {
		if (isZero())
			return false;
		return getX() > 0 && getY() == 0;
	}
	
	public boolean isUp() {
		if (isZero())
			return false;
		return getX() == 0 && getY() > 0;
	}
	
	public boolean isDown() {
		if (isZero())
			return false;
		return getX() == 0 && getY() < 0;
	}
	
	public void applyClamp() {
		if (Math.abs(getX()) < clampX)
			setX(0);
		if (Math.abs(getY()) < clampY)
			setY(0);
	}
	
	public Vec ZERO() {
		setX(0);
		setY(0);
		return this;
	}
	
	public Vec shadowOnto(Vec other) {
		double cos = Math.cos(getRad() - other.getRad());
		multMag(cos);
		setRad((cos > 0 ? 0 : Math.PI) + other.getRad());
		return this;
	}
	
	public double getX() {
		toCart();
		return x;
	}
	
	public double getY() {
		toCart();
		return y;
	}
	
	public int getXInt() {
		return (int) getX();
	}
	
	public int getYInt() {
		return (int) getY();
	}
	
	public Vec setX(double x) {
		if (isFrozen)
			return this;
		toCart();
		this.x = x;
		return this;
	}
	
	public Vec setY(double y) {
		if (isFrozen)
			return this;
		toCart();
		this.y = y;
		return this;
	}
	
	public double getMag() {
		toPolar();
		return x;
	}
	
	public double getRad() {
		toPolar();
		double rad = y % (2 * Math.PI);
		if (rad < 0)
			rad += 2 * Math.PI;
		return rad;
	}
	
	public Vec multX(double by) {
		setX(getX() * by);
		return this;
	}
	
	public Vec multY(double by) {
		setY(getY() * by);
		return this;
	}
	
	public Vec multMag(double by) {
		toPolar();
		setMag(by * getMag());
		return this;
	}
	
	public Vec mult(Vec other) {
		setX(getX() * other.getX());
		setY(getY() * other.getY());
		return this;
	}
	
	public Vec mult(double x, double y) {
		multX(x);
		multY(y);
		return this;
	}
	
	public Vec mod(double amt) {
		setX(getX() % amt);
		setY(getY() % amt);
		return this;
	}
	
	public Vec setMag(double mag) {
		if (isFrozen)
			return this;
		if (isZero())
			return this;
		toPolar();
		x = Math.abs(mag);
		return this;
	}

	public Vec setRad(double rad) {
		if (isFrozen)
			return this;
		toPolar();
		y = rad % (2 * Math.PI);
		return this;
	}
	
	public static Vec newPolar(double mag, double rad) {
		Vec newV = new Vec();
		newV.x = mag;
		newV.y = rad;
		newV.isPolar = true;
		return newV;
	}
	
	public Vec toPolar() {
		if (isPolar)
			return this;
		isPolar = true;
		double mag = Calc.dist(x, y);
		double rad = y > 0 ? Math.PI / 2 : Math.PI * 3 / 2;
		
		if (x != 0) {
			rad = Math.atan(y / x);
			if (x < 0)
				rad += Math.PI;
		}
		
		x = mag;
		y = rad;
		return this;
	}
	
	public Vec toCart() {
		if (!isPolar)
			return this;
		isPolar = false;
		double mag = x, rad = y;
		
		if (rad == 0) 				{ x = mag; 	y = 0.0;	return this; }
		if (rad == Math.PI / 2) 	{ x = 0.0; 	y = mag;	return this; }
		if (rad == Math.PI) 		{ x = -mag; y = 0.0;	return this; }
		if (rad == Math.PI / 2 * 3) { x = 0.0; 	y = -mag;	return this; }
		
		x = Math.cos(rad) * mag;
		y = Math.sin(rad) * mag;
		return this;
	}
	
	public Vec setCart(double xCmp, double yCmp) {
		toCart();
		setX(xCmp);
		setY(yCmp);
		return this;
	}
	
	public Vec setCart(Vec other) {
		setX(other.x);
		setY(other.y);
		return this;
	}
	
	public Vec setPolar(double mag, double rad) {
		toPolar();
		setMag(mag);
		setRad(rad);
		return this;
	}
	
	public Vec add(double x, double y) {
		addX(x);
		addY(y);
		return this;
	}
	
	public Vec addX(double x) {
		setX(getX() + x);
		return this;
	}
	
	public Vec addY(double y) {
		setY(getY() + y);
		return this;
	}
	
	public Vec add(Vec other) {
		setX(getX() + other.getX());
		setY(getY() + other.getY());
		return this;
	}
	
	public Vec sub(Vec other) {
		setX(getX() - other.getX());
		setY(getY() - other.getY());
		return this;
	}
	
	public Vec jsonInit(Json json) {
		setX(json.getDouble("x"));
		setY(json.getDouble("y"));
		return this;
	}
	
	public Json toJson() {
		return new Json(
			Map.entry("x", getX()),
			Map.entry("y", getY())
		);
	}
	
	@Override
	public Vec clone() {
		Vec V = new Vec(groundFriction, wallFriction, clampX, clampY, getX(), getY()); 
		V.isFrozen = isFrozen;
		return V;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vec other = (Vec) obj;
		return Double.doubleToLongBits(x) == Double.doubleToLongBits(other.x)
			&& Double.doubleToLongBits(y) == Double.doubleToLongBits(other.y);
	}

	@Override
	public String toString() {
		return String.format("<%.2f, %.2f>", getX(), getY());
	}

}
