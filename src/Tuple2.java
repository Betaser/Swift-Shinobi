package miscObjects;

public class Tuple2<A, B> {
	
	protected final A a;
	protected final B b;
	
	public Tuple2(A a, B b) {
		this.a = a;
		this.b = b;
	}
	
	public Tuple2<A, B> clone() {
		return new Tuple2<>(a, b);
	}
	
	public String toString() {
		return "(" + a + ", " + b + ")";
	}

}
