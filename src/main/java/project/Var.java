package main.java.project;

public class Var {

	private Type type;
	private int offset;
	
	public Var(Type t, int o) {
		type = t;
		offset = o;
	}
	
	public Type getType() {
		return type;
	}
	
}
