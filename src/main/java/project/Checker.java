package main.java.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import main.java.project.antlr.EmojiLangBaseListener;
import main.java.project.antlr.EmojiLangParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;


public class Checker extends EmojiLangBaseListener {
	private Result result;

	//Stack for scopes for threads
	Stack<SymbolTableNestedScopes> scopestack = new Stack<>();
	
	//normal global and lock scopes
	private SymbolTableNestedScopes scope;
	private SymbolTableNestedScopes gscope;
	private SymbolTableNestedScopes lscope;

	//List of errors
	private List<String> errors;

	public Result check(ParseTree tree) throws ParseException {
		this.scope = new SymbolTableNestedScopes();
		this.gscope = new SymbolTableNestedScopes();
		this.lscope = new SymbolTableNestedScopes();
		this.result = new Result();
		this.errors = new ArrayList<>();
		new ParseTreeWalker().walk(this, tree);
		if (hasErrors()) {
			throw new ParseException(getErrors());
		}
		return this.result;
	}

	//Program
	
	@Override
	public void exitProgram(EmojiLangParser.ProgramContext ctx) {

	}
	
	//Block
	
	@Override
	public void exitBlock(EmojiLangParser.BlockContext ctx) {
	}

	//Stat
	
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
	
	@Override
	public void exitAssStat(EmojiLangParser.AssStatContext ctx) {
		checkType(ctx.target(), getType(ctx.expr()));
	}
	
	@Override
	public void enterIfStat(EmojiLangParser.IfStatContext ctx) {
		this.scope.openScope();
	}
	
	@Override
	public void exitIfStat(EmojiLangParser.IfStatContext ctx) {
		checkType(ctx.expr(), Type.BOOL);
		this.scope.closeScope();
	}
	
	@Override
	public void enterWhileStat(EmojiLangParser.WhileStatContext ctx) {
		this.scope.openScope();
	}
	
	@Override
	public void exitWhileStat(EmojiLangParser.WhileStatContext ctx) {
		checkType(ctx.expr(), Type.BOOL);
		this.scope.closeScope();
		
	}
	
	@Override
	public void exitBlockStat(EmojiLangParser.BlockStatContext ctx) {
	}
	
	@Override
	public void enterParStat(EmojiLangParser.ParStatContext ctx) {
		scopestack.push(scope);
		scope = new SymbolTableNestedScopes();
	}
	
	@Override
	public void exitParStat(EmojiLangParser.ParStatContext ctx) {
		scope = scopestack.pop();
	}
	
	@Override
	public void exitLockStat(EmojiLangParser.LockStatContext ctx) {
		if (this.scope.contains(ctx.ID().getText()) || this.gscope.contains(ctx.ID().getText())) {
			addError(ctx, ctx.ID().getText() + " is already defined.");
		} else if (!this.lscope.contains(ctx.ID().getText())) {
			this.lscope.add(ctx.ID().getText(),Type.BOOL);
		}
	}
	
	@Override
	public void exitUnlockStat(EmojiLangParser.UnlockStatContext ctx) {
		if (!this.lscope.contains(ctx.ID().getText())) {
			addError(ctx, ctx.ID().getText() + " has not been locked yet");
		}
	}
	
	@Override
	public void exitDeclgvar(EmojiLangParser.DeclgvarContext ctx) {
		Type type = getType(ctx.type());
		if(getType(ctx.expr()) != type) {
			addError(ctx, "assignment type " + type + " does not match " + getType(ctx.expr()));
		} else if (this.scope.contains(ctx.ID().getText()) || this.gscope.contains(ctx.ID().getText()) || this.lscope.contains(ctx.ID().getText())) {
			addError(ctx, ctx.ID().getText() + " is already defined.");
		} else {
			this.gscope.add(ctx.ID().getText(),type);
		}
	}
	
	@Override
	public void exitJoinstat(EmojiLangParser.JoinstatContext ctx) {
		
	}
	
	@Override
	public void exitOutStat(EmojiLangParser.OutStatContext ctx) {
		
	}
	
	//Target
	
	@Override
	public void exitIdTarget(EmojiLangParser.IdTargetContext ctx) {
		String id = ctx.ID().getText();
		Var var = this.scope.getVar(id);
		if(var == null) {
			var = this.gscope.getVar(id);
			if (var == null ) {
			addError(ctx, "target id type was not yet defined");
			} else {
				setType(ctx, var.getType());
			}
		} else {
			setType(ctx, var.getType());
		}

	}
	
	//Expr

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

	@Override
	public void exitMultExpr(EmojiLangParser.MultExprContext ctx) {
		checkType(ctx.expr(0), Type.INT);
		checkType(ctx.expr(1), Type.INT);
		setType(ctx, Type.INT);
	}

	@Override
	public void exitPlusExpr(EmojiLangParser.PlusExprContext ctx) {
		checkType(ctx.expr(0), Type.INT);
		checkType(ctx.expr(1), Type.INT);
		setType(ctx, Type.INT);
	}
	
	@Override
	public void exitBoolExpr(EmojiLangParser.BoolExprContext ctx) {
		checkType(ctx.expr(0), Type.BOOL);
		checkType(ctx.expr(1), Type.BOOL);
		setType(ctx, Type.BOOL);
	}

	@Override
	public void exitCompExpr(EmojiLangParser.CompExprContext ctx) {
		checkType(ctx.expr(0), Type.INT);
		checkType(ctx.expr(1), Type.INT);
		setType(ctx, Type.BOOL);
	}

	@Override
	public void exitParExpr(EmojiLangParser.ParExprContext ctx) {
		setType(ctx, getType(ctx.expr()));
	}

	@Override
	public void exitIdExpr(EmojiLangParser.IdExprContext ctx) {
		String id = ctx.ID().getText();
		Var var = this.scope.getVar(id);
		if (var == null) {
			var = this.gscope.getVar(id);
			if (var == null) {
				addError(ctx, "Variable '%s' not declared", id);
			} else {
				setType(ctx, var.getType());
			}
			
		} else {
			setType(ctx, var.getType());
		}
	}

	@Override
	public void exitNumExpr(EmojiLangParser.NumExprContext ctx) {
		setType(ctx, Type.INT);
	}
	
	@Override
	public void exitTrueExpr(EmojiLangParser.TrueExprContext ctx) {
		setType(ctx, Type.BOOL);
	}
	
	@Override
	public void exitFalseExpr(EmojiLangParser.FalseExprContext ctx) {
		setType(ctx, Type.BOOL);
	}
	
	//Type
	
	@Override
	public void exitBoolType(EmojiLangParser.BoolTypeContext ctx) {
		setType(ctx, Type.BOOL);
	}

	@Override
	public void exitIntType(EmojiLangParser.IntTypeContext ctx) {
		setType(ctx, Type.INT);
	}

	/** Indicates if any errors were encountered in this tree listener. */
	public boolean hasErrors() {
		return !getErrors().isEmpty();
	}

	/** Returns the list of errors collected in this tree listener. */
	public List<String> getErrors() {
		return this.errors;
	}

	private void checkType(ParserRuleContext node, Type expected) {
		Type actual = getType(node);
		if (actual == null) {
			throw new IllegalArgumentException(node.getText() + " is not assigned");
		}
		if (!actual.equals(expected)) {
			addError(node, "Expected type '%s' but found '%s'", expected,
					actual);
		}
	}

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

	private void setType(ParseTree node, Type type) {
		this.result.setType(node, type);
	}

	private Type getType(ParseTree node) {
		return this.result.getType(node);
	}

}
