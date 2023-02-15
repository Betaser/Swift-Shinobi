package helper;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import math.Polygon;
import math.Vec;
import miscObjects.Collision;
import miscObjects.Consumer5;
import miscObjects.Corner;
import miscObjects.Intersection;
import miscObjects.Mut;
import miscObjects.ShortestCollisionInfo;
import miscObjects.Side;
import miscObjects.SideCornerCollision;

public class Calc {
	
	public static double dist(double x, double y) {
		return Math.sqrt(x * x + y * y);
	}
	
	public static int sign(Number n) {
		if (n.doubleValue() == 0)
			return 0;
		return n.doubleValue() > 0 ? 1 : -1;
	}
	
	public static Rectangle containingRect(Polygon polygon) {
		final int X_MIN = polygon.vertices.stream().map(Vec::getX).min(Double::compare).get().intValue();
		final int Y_MIN = polygon.vertices.stream().map(Vec::getY).min(Double::compare).get().intValue();
		final int X_MAX = polygon.vertices.stream().map(Vec::getX).max(Double::compare).get().intValue();
		final int Y_MAX = polygon.vertices.stream().map(Vec::getY).max(Double::compare).get().intValue();
		return new Rectangle(
			X_MIN,
			Y_MIN,
			X_MAX - X_MIN,
			Y_MAX - Y_MIN
		);
	}
	
	/*
	GENERAL SUMMARY:
		Given a polygon, its displacement and some other polygons, 
		return what the displacement of the one polygon should be
		and return how the polygons collided
	PARAMETERS:
		Polygon polygon
		Vec pDisplacement
		Polygon otherPs
	PREREQUISITES:
		polygon is not already colliding with anything
	GENERAL ALGORITHM:
		For every otherP in otherPs, check each side and corner of polygon when displaced
		for if it intersects with any side or corner of otherP.
		If it does, store the shortest distance polygon has to travel along the displacement direction
		before it would intersect with a side/corner of otherP.
		Also store the side/corner that polygon collides with that is from the shortest distance collision.
		Move polygon (but not mutably) by the shortest distance.
		
		Take the difference between pDisplacement and the shortest distance displacement
		and move polygon (but not mutably) by that distance shadowed onto the side/corner it collided with.
		
		For every otherP in otherPs, check each side and corner of polygon (except for the side/corner it collided with)
		when displaced by the difference for if it intersects with any side of otherP.
		If it does, store the shortest distance polygon has to travel along the displacement direction
		before it would intersect with a side/corner of otherP.
		Also store the side that polygon collides with that is from the shortest distance collision.
		
		Return the sum vec of the first move and the shortest distance displacement along the second move,
		the first and second sides/corners it collided with,
		and the first and second side/corners that collided with the aforementioned sides/corners from the otherP
		
		Technically, I believe you can create a circular ramp with tons of super small lines with increasing slope
		and have collision work properly by repeatedly getting the slideMove of the polygon, pretending the polygon moved,
		and repeating as long as you hit another side.
	*/
	public static Optional<Collision> testCollision(Polygon polygon, Vec pDisplacement, List<Polygon> otherPs) {
		Optional<ShortestCollisionInfo> collisionInfo1 = Calc.shortestCollisionInfo(polygon, pDisplacement, otherPs);
		if (collisionInfo1.isEmpty())
			return Optional.empty();
		
		ShortestCollisionInfo cInfo1 = collisionInfo1.get();
		Vec move1 = movePolygon(cInfo1);
		Vec slideMove = slideMove(move1, pDisplacement, cInfo1);
		
		Optional<ShortestCollisionInfo> collisionInfo2 = shortestCollisionInfo(
			polygon.cloneVertices().translate(move1), slideMove, otherPs);
		if (collisionInfo2.isEmpty())
			return Optional.empty();
		
		ShortestCollisionInfo cInfo2 = collisionInfo2.get();
		Vec move2 = movePolygon(cInfo2);
		
		return Optional.of(new Collision(move1, cInfo1, move2, cInfo2));
	}
	
	public static Vec slideMove(Vec move, Vec pDisplacement, ShortestCollisionInfo info) {
		Vec excessMove = pDisplacement.clone().sub(move);
		//	Shadow excessMove onto info's side
		Vec side = info.collisionInfo().side.p2.clone().sub(info.collisionInfo().side.p1);
		return excessMove.shadowOnto(side);
	}
	
	//	Does not move polygon all the way onto a surface because then the polygon is considered "inside" that surface
	public static Vec movePolygon(ShortestCollisionInfo info) {
		Vec move = info.pos().clone().sub(info.collisionInfo().corner);
		move.setMag(move.getMag() - Math.pow(10, -8));
		return info.isPoly1Corner() ? move : move.neg();
	}
	
	public static Optional<ShortestCollisionInfo> shortestCollisionInfo(Polygon polygon, Vec pDisplacement, List<Polygon> otherPs) {
		Mut<ShortestCollisionInfo> info = new Mut<>();
		Mut<Double> dist = new Mut<>(Double.POSITIVE_INFINITY);
		
		loopThruPVertices(polygon, otherPs,
			(vertex, p, i, sideV1, sideV2) -> {
				Intersection intersection = intersection(vertex, pDisplacement, sideV1, sideV2.clone().sub(sideV1));
				if (!intersection.withinLine1() || !intersection.withinLine2())
					return;
				double vertDist = vertex.clone().sub(intersection.pos()).getMag();
				if (dist.val > vertDist) {
					dist.val = vertDist;
					info.val = new ShortestCollisionInfo(
						dist.val,
						intersection.pos(),
						new SideCornerCollision(new Side(sideV1, sideV2), new Corner(vertex)),
						true
					);
				}
			}
		);
		
		loopThruPSides(polygon, otherPs,
			(vertex, p, i, sideV1, sideV2) -> {
				Intersection intersection = intersection(vertex, pDisplacement.clone().neg(), sideV1, sideV2.clone().sub(sideV1));
				if (!intersection.withinLine1() || !intersection.withinLine2())
					return;
				double vertDist = vertex.clone().sub(intersection.pos()).getMag();
				if (dist.val > vertDist) {
					dist.val = vertDist;
					info.val = new ShortestCollisionInfo(
						dist.val,
						intersection.pos(),
						new SideCornerCollision(new Side(sideV1, sideV2), new Corner(vertex)),
						false
					);
				}
			}
		);
		
		return Optional.ofNullable(info.val);
	}

	/*
	Detect if and where two vectors intersect
	 */
	public static Vec infIntersection(Vec pos1, Vec v1, Vec pos2, Vec v2) {
		/*
		v1 and v2 intersect at a point, meaning same x and y
		y1 = ax1 + b
		y2 = cx2 + d
		
		x=: ax1 + b = cx2 + d
			x1 = x2
			ax + b = cx + d
			ax - cx = d - b
			(a - c)x = d - b
			x = (d - b) / (a - c)
		
		y=: y = ax + b
		 */
		Vec intersection = new Vec();
		final double A, B, C, D;
		boolean setX = true;
		
		if (v1.getX() == 0) {
			intersection.setX(pos1.getX());
			Vec temp = v1;
			v1 = v2;
			v2 = temp;
			temp = pos1;
			pos1 = pos2;
			pos2 = temp;
		} else if (v2.getX() == 0) {
			intersection.setX(pos2.getX());
		} else {
			setX = false;
		}
		
		//	A is the slope of v1
		A = v1.getY() / v1.getX();
		
		//	B is a shift of v1, calc by solving using pos1
		//	b = y1 - ax1
		B = pos1.getY() - A * pos1.getX();

		//	C is the slope of v2
		C = v2.getY() / v2.getX();

		//	D is a shift of v2, calc by solving using pos2
		//	d = y2 - cx2
		D = pos2.getY() - C * pos2.getX();
		
		if (!setX)
			intersection.setX((D - B) / (A - C));
		intersection.setY(A * intersection.getX() + B);

		return intersection;
	}
	
	public static Intersection intersection(Vec pos1, Vec v1, Vec pos2, Vec v2) {
		Vec intersection = infIntersection(pos1, v1, pos2, v2);

		boolean between1XSE = pos1.getX() <= intersection.getX() && intersection.getX() <= pos1.clone().add(v1).getX();
		boolean between1XES = pos1.clone().add(v1).getX() <= intersection.getX() && intersection.getX() <= pos1.getX();
		boolean between1YSE = pos1.getY() <= intersection.getY() && intersection.getY() <= pos1.clone().add(v1).getY();
		boolean between1YES = pos1.clone().add(v1).getY() <= intersection.getY() && intersection.getY() <= pos1.getY();
		
		boolean between2XSE = pos2.getX() <= intersection.getX() && intersection.getX() <= pos2.clone().add(v2).getX();
		boolean between2XES = pos2.clone().add(v2).getX() <= intersection.getX() && intersection.getX() <= pos2.getX();
		boolean between2YSE = pos2.getY() <= intersection.getY() && intersection.getY() <= pos2.clone().add(v2).getY();
		boolean between2YES = pos2.clone().add(v2).getY() <= intersection.getY() && intersection.getY() <= pos2.getY();
		
		return new Intersection(
			intersection, 
			(between1XSE || between1XES) && (between1YSE || between1YES), 
			(between2XSE || between2XES) && (between2YSE || between2YES)
		);
	}
	
	//	Only for testing
	public static ArrayList<Intersection> loopThruGetIntersections(Polygon polygon, Vec pDisplacement, List<Polygon> otherPs) {
		ArrayList<Intersection> intersections = new ArrayList<>();
		loopThruPVertices(polygon, otherPs, 
			(vertex, p, i, sideV1, sideV2) -> intersections.add(intersection(vertex, pDisplacement, sideV1, sideV2.clone().sub(sideV1)))
		);
		loopThruPSides(polygon, otherPs,
			(vertex, p, i, sideV1, sideV2) -> intersections.add(intersection(vertex, pDisplacement.clone().neg(), sideV1, sideV2.clone().sub(sideV1)))
		);
		return intersections;
	}
	
	/*
	Loop through every vertex and side on other polygon
	 */
	public static ArrayList<Intersection> loopThruPVertices(Polygon polygon, List<Polygon> otherPs, Consumer5<Vec, Polygon, Integer, Vec, Vec> body) {
		ArrayList<Intersection> intersections = new ArrayList<>();
		for (Vec vertex : polygon.vertices) {
//			System.out.println("new vertex");
			//	Each otherP: p
			for (Polygon p : otherPs) {
//				System.out.println("new polygon");
				//	Each side of p
				for (int i = 0, I = p.vertices.size(); i < I; i++) {
					Vec sideV1 = p.vertices.get(i);
					Vec sideV2 = p.vertices.get((i + 1) % I);
					
					body.accept(vertex, p, i, sideV1, sideV2);
				}
			}
		}
		return intersections;
	}
	
	/*
	Loop through every side and vertex on other polygon
	 */
	public static ArrayList<Intersection> loopThruPSides(Polygon polygon, List<Polygon> otherPs, Consumer5<Vec, Polygon, Integer, Vec, Vec> body) {
		ArrayList<Intersection> intersections = new ArrayList<>();
		for (Polygon p : otherPs) {
			for (Vec vertex : p.vertices) {
				for (int i = 0, I = polygon.vertices.size(); i < I; i++) {
					Vec sideV1 = polygon.vertices.get(i);
					Vec sideV2 = polygon.vertices.get((i + 1) % I);

					body.accept(vertex, polygon, i, sideV1, sideV2);
				}
			}
		}
		return intersections;
	}

}
