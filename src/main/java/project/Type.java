package main.java.project;

abstract public class Type {
	public static final Type BOOL = new Bool();
	public static final Type INT = new Int();
	private final TypeKind kind;

	protected Type(TypeKind kind) {
		this.kind = kind;
	}

	public TypeKind getKind() {
		return this.kind;
	}


	static public class Bool extends Type {
		private Bool() {
			super(TypeKind.BOOL);
		}


		@Override
		public String toString() {
			return "Boolean";
		}
	}

	static public class Int extends Type {
		private Int() {
			super(TypeKind.INT);
		}

		@Override
		public String toString() {
			return "Int";
		}
	}
}
