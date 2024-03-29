package main.java.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import main.java.project.antlr.EmojiLangBaseListener;
import main.java.project.antlr.EmojiLangParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;


public class Checker extends EmojiLangBaseListener {


	//Stack for scopes for threads
	Stack<SymbolTableNestedScopes> scopestack = new Stack<>();
	
	//normal global and lock scopes
	private SymbolTableNestedScopes scope;
	private SymbolTableNestedScopes gscope;
	private SymbolTableNestedScopes lscope;

	//List of errors
	private List<String> errors;
	
	//Types
	private final ParseTreeProperty<Type> types = new ParseTreeProperty<>();

	public void check(ParseTree tree) throws ParseException {
		this.scope = new SymbolTableNestedScopes();
		this.gscope = new SymbolTableNestedScopes();
		this.lscope = new SymbolTableNestedScopes();
		this.errors = new ArrayList<>();
		new ParseTreeWalker().walk(this, tree);
		if (!errors.isEmpty()) {
			throw new ParseException(this.errors);
		}
	}

	//Stat
	
	//Puts a new variable in scope if there isn't already a variable with that name in scope gscope or lscop and checks if the declared type equal the type of the expression.
	@Override
	public void exitDeclvar(EmojiLangParser.DeclvarContext ctx) {
		Type type = getType(ctx.type());
		if(getType(ctx.expr()) != type) {
			addError(ctx, "assignment type " + type + " does not match " + getType(ctx.expr()));
		} else if (this.scope.contains(ctx.ID().getText()) || this.gscope.contains(ctx.ID().getText()) || this.lscope.contains(ctx.ID().getText())) {
			addError(ctx, ctx.ID().getText() + " is already defined.");
		} else {
			this.scope.add(ctx.ID().getText(),type);
		}
	}
	
	//Checks if the type of the targer is the same as the type of the expression.
	@Override
	public void exitAssStat(EmojiLangParser.AssStatContext ctx) {
		checkType(ctx.target(), getType(ctx.expr()));
	}
	
	//Entering an if statement opens a new scope.
	@Override
	public void enterIfStat(EmojiLangParser.IfStatContext ctx) {
		this.scope.openScope();
	}
	
	//Exiting an if statement closes the scope that was opened upon entering the if statement.
	@Override
	public void exitIfStat(EmojiLangParser.IfStatContext ctx) {
		checkType(ctx.expr(), Type.BOOL);
		this.scope.closeScope();
	}
	
	//if an else is present in the if statement the current scope has to be closed and a new scope has to be opened for the else part.
	@Override public void visitTerminal(TerminalNode node) { 
		if (node.getText().toLowerCase().equals("else")) {
			this.scope.closeScope();
			this.scope.openScope();
		}
	}
	
	//Starting a while loop opens a new scope.
	@Override
	public void enterWhileStat(EmojiLangParser.WhileStatContext ctx) {
		this.scope.openScope();
	}
	
	//Exiting a while loop closes the scope that was opened upon entering the while loop.
	@Override
	public void exitWhileStat(EmojiLangParser.WhileStatContext ctx) {
		checkType(ctx.expr(), Type.BOOL);
		this.scope.closeScope();
		
	}
	
	//Entering a par statement creates an entire new scope and stores te previous one in a stack.
	@Override
	public void enterParStat(EmojiLangParser.ParStatContext ctx) {
		scopestack.push(scope);
		scope = new SymbolTableNestedScopes();
	}
	
	//Exiting a par statement pops a scope from the stack and puts it as the scope.
	@Override
	public void exitParStat(EmojiLangParser.ParStatContext ctx) {
		scope = scopestack.pop();
	}
	
	//Puts a variable in lscope if it is not already in scope or gscope. If variable already exists in lscope it is also allowed.
	@Override
	public void exitLockStat(EmojiLangParser.LockStatContext ctx) {
		if (this.scope.contains(ctx.ID().getText()) || this.gscope.contains(ctx.ID().getText())) {
			addError(ctx, ctx.ID().getText() + " is already defined.");
		} else if (!this.lscope.contains(ctx.ID().getText())) {
			this.lscope.add(ctx.ID().getText(),Type.BOOL);
		}
	}
	
	//Checks if the lock exists in lscope.
	@Override
	public void exitUnlockStat(EmojiLangParser.UnlockStatContext ctx) {
		if (!this.lscope.contains(ctx.ID().getText())) {
			addError(ctx, ctx.ID().getText() + " has not been locked yet");
		}
	}
	
	//Puts a new variable in gscope if there isn't already a variable with that name in scope gscope or lscop and checks if the declared type equal the type of the expression.
	@Override
	public void exitDeclgvar(EmojiLangParser.DeclgvarContext ctx) {
		if (scopestack.empty() && scope.getScope() == 0) {
			Type type = getType(ctx.type());
			if(getType(ctx.expr()) != type) {
				addError(ctx, "assignment type " + type + " does not match " + getType(ctx.expr()));
			} else if (this.scope.contains(ctx.ID().getText()) || this.gscope.contains(ctx.ID().getText()) || this.lscope.contains(ctx.ID().getText())) {
				addError(ctx, ctx.ID().getText() + " is already defined.");
			} else {
				this.gscope.add(ctx.ID().getText(),type);
			}
		} else {
			addError(ctx, "Global vars can only be declared on top level of main thread");
		}
	}
	
	//Checks if join is used in the main thread by checking if the scope stack is empty.
	@Override
	public void exitJoinstat(EmojiLangParser.JoinstatContext ctx) {
		if (!scopestack.empty()) {
			addError(ctx, "Join can only be used in the main thread");
		}
	}
	
	//Target
	
	//Checks if variable is in current scope or in gscope and sets it type to this.
	@Override
	public void exitIdTarget(EmojiLangParser.IdTargetContext ctx) {
		String id = ctx.ID().getText();
		Type var = this.scope.getType(id);
		if(var == null) {
			var = this.gscope.getType(id);
			if (var == null ) {
			addError(ctx, "target id type was not yet defined");
			} else {
				setType(ctx, var);
			}
		} else {
			setType(ctx, var);
		}

	}
	
	//Expr

	//if the terminal is a minus the expressions has to be an integer and if the terminal is an exclamation mark the expression needs to be a boolean. Sets it's type to the type found.
	@Override
	public void exitPrfExpr(EmojiLangParser.PrfExprContext ctx) {
		Type type;
		if (ctx.prfOp().MINUS() != null) {
			type = Type.INT;
		} else {
			assert ctx.prfOp().NOT() != null;
			type = Type.BOOL;
		}
		checkType(ctx.expr(), type);
		setType(ctx, type);
	}

	//Checks if both expressions are int types. Sets itself to int.
	@Override
	public void exitMultExpr(EmojiLangParser.MultExprContext ctx) {
		checkType(ctx.expr(0), Type.INT);
		checkType(ctx.expr(1), Type.INT);
		setType(ctx, Type.INT);
	}

	//Checks if both expressions are int types. Sets itself to int.
	@Override
	public void exitPlusExpr(EmojiLangParser.PlusExprContext ctx) {
		checkType(ctx.expr(0), Type.INT);
		checkType(ctx.expr(1), Type.INT);
		setType(ctx, Type.INT);
	}
	
	//Checks if both expressions are boolean types. Sets itself to int.
	@Override
	public void exitBoolExpr(EmojiLangParser.BoolExprContext ctx) {
		checkType(ctx.expr(0), Type.BOOL);
		checkType(ctx.expr(1), Type.BOOL);
		setType(ctx, Type.BOOL);
	}

	//Checks if both expressions are int types. Sets itself to boolean.
	@Override
	public void exitCompExpr(EmojiLangParser.CompExprContext ctx) {
		checkType(ctx.expr(0), Type.INT);
		checkType(ctx.expr(1), Type.INT);
		setType(ctx, Type.BOOL);
	}

	//Sets itself to the expression
	@Override
	public void exitParExpr(EmojiLangParser.ParExprContext ctx) {
		setType(ctx, getType(ctx.expr()));
	}

	//Checks if variable is in current scope or in gscope.
	@Override
	public void exitIdExpr(EmojiLangParser.IdExprContext ctx) {
		String id = ctx.ID().getText();
		Type var = this.scope.getType(id);
		if (var == null) {
			var = this.gscope.getType(id);
			if (var == null) {
				addError(ctx, "Variable '%s' not declared", id);
			} else {
				setType(ctx, var);
			}
			
		} else {
			setType(ctx, var);
		}
	}

	//Sets itself to int.
	@Override
	public void exitNumExpr(EmojiLangParser.NumExprContext ctx) {
		setType(ctx, Type.INT);
	}
	
	//Sets itself to boolean.
	@Override
	public void exitTrueExpr(EmojiLangParser.TrueExprContext ctx) {
		setType(ctx, Type.BOOL);
	}
	
	//Sets itself to boolean.
	@Override
	public void exitFalseExpr(EmojiLangParser.FalseExprContext ctx) {
		setType(ctx, Type.BOOL);
	}
	
	//Type
	//Sets itself to boolean.
	@Override
	public void exitBoolType(EmojiLangParser.BoolTypeContext ctx) {
		setType(ctx, Type.BOOL);
	}

	//Sets itself to int.
	@Override
	public void exitIntType(EmojiLangParser.IntTypeContext ctx) {
		setType(ctx, Type.INT);
	}

	//Checks the type of a node to the expected type.
	private void checkType(ParserRuleContext node, Type expected) {
		Type actual = getType(node);
		if (actual == null) {
			addError(node, node.getText() + " is not assigned");
		} else if (!actual.equals(expected)) {
			addError(node, "Expected type '%s' but found '%s'", expected,
					actual);
		}
	}

	//Adds an error to the list of errors.
	private void addError(ParserRuleContext node, String message,
			Object... args) {
		addError(node.getStart(), message, args);
	}


	private void addError(Token token, String message, Object... args) {
		int line = token.getLine();
		int column = token.getCharPositionInLine();
		message = String.format(message, args);
		message = String.format("Line %d:%d - %s", line, column, message);
		this.errors.add(message);
	}

	//Sets the type of a node.
	private void setType(ParseTree node, Type type) {
		this.types.put(node, type);
	}

	//Gets the type of a node.
	private Type getType(ParseTree node) {
		return this.types.get(node);
	}

}
