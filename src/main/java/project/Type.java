package main.java.project;

/** Pascal data type. */
abstract public class Type {
	/** The singleton instance of the {@link Bool} type. */
	public static final Type BOOL = new Bool();
	/** The singleton instance of the {@link Int} type. */
	public static final Type INT = new Int();
	private final TypeKind kind;

	/** Constructor for subclasses. */
	protected Type(TypeKind kind) {
		this.kind = kind;
	}

	/** Returns the kind of this type. */
	public TypeKind getKind() {
		return this.kind;
	}

	/** returns the size (in bytes) of a value of this type. */
	abstract public int size();

	/** Representation of the Pascal Boolean type. */
	static public class Bool extends Type {
		private Bool() {
			super(TypeKind.BOOL);
		}

		@Override
		public int size() {
			return 4;
		}

		@Override
		public String toString() {
			return "Boolean";
		}
	}

	/** Representation of the Pascal Integer type. */
	static public class Int extends Type {
		private Int() {
			super(TypeKind.INT);
		}

		@Override
		public int size() {
			return 4;
		}

		@Override
		public String toString() {
			return "Intr";
		}
	}
}
