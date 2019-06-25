package main.java.project;

import java.util.ArrayList;
import java.util.List;

import main.java.project.antlr.EmojiLangBaseListener;
import main.java.project.antlr.EmojiLangParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import main.java.project.tobedeleted.ParseException;
import main.java.project.tobedeleted.Result;

/** Class to type check and calculate flow entries and variable offsets. */
public class Checker extends EmojiLangBaseListener {
	/** Result of the latest call of {@link #check}. */
	private Result result;
	/** Variable scope for the latest call of {@link #check}. */
	private SymbolTableNestedScopes scope;
	/** List of errors collected in the latest call of {@link #check}. */
	private List<String> errors;

	/** Runs this checker on a given parse tree,
	 * and returns the checker result.
	 * @throws ParseException if an error was found during checking.
	 */
	public Result check(ParseTree tree) throws ParseException {
		this.scope = new SymbolTableNestedScopes();
		this.result = new Result();
		this.errors = new ArrayList<>();
		new ParseTreeWalker().walk(this, tree);
		if (hasErrors()) {
			throw new ParseException(getErrors());
		}
		return this.result;
	}

	// Override the listener methods for the statement nodes

	@Override
	public void exitBlock(EmojiLangParser.BlockContext ctx) {
		System.out.println(ctx);
		System.out.println(ctx.stat(0));
		setEntry(ctx, entry(ctx.stat(0)));
	}

	@Override
	public void exitBlockStat(EmojiLangParser.BlockStatContext ctx) {
		setEntry(ctx, entry(ctx.block()));
	}
	
	@Override
	public void enterWhileStat(EmojiLangParser.WhileStatContext ctx) {
		this.scope.openScope();
	}

	@Override
	public void exitWhileStat(EmojiLangParser.WhileStatContext ctx) {
		setEntry(ctx, entry(ctx.expr()));
		checkType(ctx.expr(), Type.BOOL);
		this.scope.closeScope();
		
	}

	@Override
	public void enterIfStat(EmojiLangParser.IfStatContext ctx) {
		this.scope.openScope();
	}
	
	@Override
	public void exitIfStat(EmojiLangParser.IfStatContext ctx) {
		setEntry(ctx, entry(ctx.expr()));
		checkType(ctx.expr(), Type.BOOL);
		this.scope.closeScope();
	}

	@Override
	public void exitAssStat(EmojiLangParser.AssStatContext ctx) {
		setEntry(ctx, entry(ctx.expr())); //WHY SHOULD THIS BE EXPR?
		checkType(ctx.target(), getType(ctx.expr()));
	}

	@Override
	public void exitIdTarget(EmojiLangParser.IdTargetContext ctx) {
		setEntry(ctx, ctx);
		String id = ctx.ID().getText();
		Var var = this.scope.getVar(id);
		if(var == null) {
			addError(ctx, "target id type was not yet defined");
		} else {
			setType(ctx, var.getType());
			setOffset(ctx, var.getOffset());
		}

	}

	@Override
	public void exitDeclvar(EmojiLangParser.DeclvarContext ctx) {
		setEntry(ctx, ctx.type());
		Type type = getType(ctx.type());
		if(getType(ctx.expr()) != type) {
			addError(ctx, "assignment type " + type + " does not match " + getType(ctx.expr()));
		} else {
			this.scope.add(ctx.ID().getText(),type);
			setOffset(ctx, this.scope.getVar(ctx.ID().getText()).getOffset());
		}
	}

	@Override
	public void exitBoolType(EmojiLangParser.BoolTypeContext ctx) {
		setType(ctx, Type.BOOL);
		setEntry(ctx, ctx);
	}

	@Override
	public void exitIntType(EmojiLangParser.IntTypeContext ctx) {
		setType(ctx, Type.INT);
		setEntry(ctx, ctx);
	}

	@Override
	public void exitProgram(EmojiLangParser.ProgramContext ctx) {

	}

	//expression listeners

	@Override
	public void exitBoolExpr(EmojiLangParser.BoolExprContext ctx) {
		checkType(ctx.expr(0), Type.BOOL);
		checkType(ctx.expr(1), Type.BOOL);
		setType(ctx, Type.BOOL);
		setEntry(ctx, entry(ctx.expr(0)));
	}

	@Override
	public void exitCompExpr(EmojiLangParser.CompExprContext ctx) {
		checkType(ctx.expr(0), Type.INT);
		checkType(ctx.expr(1), Type.INT);
		setType(ctx, Type.BOOL);
		setEntry(ctx, entry(ctx.expr(0)));
	}

	@Override
	public void exitFalseExpr(EmojiLangParser.FalseExprContext ctx) {
		setType(ctx, Type.BOOL);
		setEntry(ctx, ctx);
	}

	@Override
	public void exitIdExpr(EmojiLangParser.IdExprContext ctx) {
		String id = ctx.ID().getText();
		Var var = this.scope.getVar(id);
		if (var == null) {
			addError(ctx, "Variable '%s' not declared", id);
		} else {
			setType(ctx, var.getType());
			setOffset(ctx, var.getOffset());
			setEntry(ctx, ctx);
		}
	}

	@Override
	public void exitMultExpr(EmojiLangParser.MultExprContext ctx) {
		checkType(ctx.expr(0), Type.INT);
		checkType(ctx.expr(1), Type.INT);
		setType(ctx, Type.INT);
		setEntry(ctx, entry(ctx.expr(0)));
	}

	@Override
	public void exitNumExpr(EmojiLangParser.NumExprContext ctx) {
		setType(ctx, Type.INT);
		setEntry(ctx, ctx);
	}

	@Override
	public void exitParExpr(EmojiLangParser.ParExprContext ctx) {
		setType(ctx, getType(ctx.expr()));
		setEntry(ctx, entry(ctx.expr()));
	}

	@Override
	public void exitPlusExpr(EmojiLangParser.PlusExprContext ctx) {
		checkType(ctx.expr(0), Type.INT);
		checkType(ctx.expr(1), Type.INT);
		setType(ctx, Type.INT);
		setEntry(ctx, entry(ctx.expr(0)));
	}

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
		setEntry(ctx, entry(ctx.expr()));
	}

	@Override
	public void exitTrueExpr(EmojiLangParser.TrueExprContext ctx) {
		setType(ctx, Type.BOOL);
		setEntry(ctx, ctx);
	}

	/** Indicates if any errors were encountered in this tree listener. */
	public boolean hasErrors() {
		return !getErrors().isEmpty();
	}

	/** Returns the list of errors collected in this tree listener. */
	public List<String> getErrors() {
		return this.errors;
	}

	/** Checks the inferred type of a given parse tree,
	 * and adds an error if it does not correspond to the expected type.
	 */
	private void checkType(ParserRuleContext node, Type expected) {
		Type actual = getType(node);
		if (actual == null) {
			throw new IllegalArgumentException("Missing inferred type of "
					+ node.getText());
		}
		if (!actual.equals(expected)) {
			addError(node, "Expected type '%s' but found '%s'", expected,
					actual);
		}
	}

	/** Records an error at a given parse tree node.
	 * @param node the parse tree node at which the error occurred
	 * @param message the error message
	 * @param args arguments for the message, see {@link String#format}
	 */
	private void addError(ParserRuleContext node, String message,
			Object... args) {
		addError(node.getStart(), message, args);
	}

	/** Records an error at a given token.
	 * @param token the token at which the error occurred
	 * @param message the error message
	 * @param args arguments for the message, see {@link String#format}
	 */
	private void addError(Token token, String message, Object... args) {
		int line = token.getLine();
		int column = token.getCharPositionInLine();
		message = String.format(message, args);
		message = String.format("Line %d:%d - %s", line, column, message);
		this.errors.add(message);
	}

	/** Convenience method to add an offset to the result. */
	private void setOffset(ParseTree node, Integer offset) {
		this.result.setOffset(node, offset);
	}

	/** Convenience method to add a type to the result. */
	private void setType(ParseTree node, Type type) {
		this.result.setType(node, type);
	}

	/** Returns the type of a given expression or type node. */
	private Type getType(ParseTree node) {
		return this.result.getType(node);
	}

	/** Convenience method to add a flow graph entry to the result. */
	private void setEntry(ParseTree node, ParserRuleContext entry) {
		if (entry == null) {
		}
		this.result.setEntry(node, entry);
	}

	/** Returns the flow graph entry of a given expression or statement. */
	private ParserRuleContext entry(ParseTree node) {
		return this.result.getEntry(node);
	}
}
