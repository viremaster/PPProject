package main.java.project;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

public class Result {
	private final ParseTreeProperty<Type> types = new ParseTreeProperty<>();

	public void setType(ParseTree node, Type type) {
		this.types.put(node, type);
	}

	public Type getType(ParseTree node) {
		return this.types.get(node);
	}
}
