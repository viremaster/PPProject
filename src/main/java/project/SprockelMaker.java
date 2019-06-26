package main.java.project;

import java.util.*;

import main.java.project.antlr.EmojiLangBaseVisitor;
import main.java.project.antlr.EmojiLangParser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

public class SprockelMaker extends EmojiLangBaseVisitor<String> {

    private String prog;

    private int regCount;

    private ParseTreeProperty<String> regs;

    private String res;
    private List<String> registerlist = Arrays.asList("regA","regB","regC","regD","regE","regF");

    private Map<String, String> varmap = new HashMap<>();

    public String generate(ParseTree tree) {
        this.prog = "";
        this.regs = new ParseTreeProperty<>();
        this.regCount = 0;
        tree.accept(this);
        return this.prog;
    }

    @Override
    public String visitDeclvar(EmojiLangParser.DeclvarContext ctx) {
        String address = reg(ctx);
        varmap.put(ctx.ID().getText(), address);
        visit(ctx.expr());
        String result = "Pop regA, \n";
        result += "Store regA " + address + ", \n";
        prog += result;
        return result;
    }

    @Override
    public String visitAssStat(EmojiLangParser.AssStatContext ctx) {
        visit(ctx.expr());
        String result = "Pop regA, \n";
        String address = reg(ctx.target());
        result += "Store regA " + address + ", \n";
        prog += result;
        return result;
    }

    @Override
    public String visitBlockStat(EmojiLangParser.BlockStatContext ctx) {
        return visit(ctx.block());
    }

    @Override
    public String visitBlock(EmojiLangParser.BlockContext ctx) {
        for(EmojiLangParser.StatContext st:ctx.stat()){
            visit(st);
        }
        return null;
    }

    @Override
    public String visitIfStat(EmojiLangParser.IfStatContext ctx) {
        visit(ctx.expr());
        prog += "Pop regA";
        prog += "Branch regA (Abs y), \n";
        if(ctx.stat().size() == 2) {
            visit(ctx.stat(1));
        }
        prog += "Jump (Rel x), \n";
        visit(ctx.stat(0));
        return null;
    }

    @Override
    public String visitWhileStat(EmojiLangParser.WhileStatContext ctx) {
        visit(ctx.expr());
        prog += "Pop regA";
        prog += "Branch regA (Abs y), \n";
        prog += "Jump (Rel z), \n";
        visit(ctx.stat());
        prog += "Jump (Rel x), \n";
        return null;
    }

    @Override
    public String visitPrfExpr(EmojiLangParser.PrfExprContext ctx) {
        visit(ctx.expr());
        String result = "Pop regA, \n";
        if(ctx.prfOp().getText().equals("-")){
            result += "Load (ImmValue -1) regB, \n";
            result += "Compute Mul regA regB regA, \n";
        } else {
            result += "Load (ImmValue 1) regB, \n";
            result += "Compute Xor regA regB regA, \n";
        }
        result += "Push regA, \n";
        prog += result;
        return result;
    }

    @Override
    public String visitMultExpr(EmojiLangParser.MultExprContext ctx) {
        visit(ctx.expr(0));
        visit(ctx.expr(1));
        String result = "Pop regA, \n";
        result += "Pop regB, \n";
        result += "Compute Mul regA regB regA, \n";
        result += "Push regA, \n";
        prog += result;
        return result;
    }

    @Override
    public String visitPlusExpr(EmojiLangParser.PlusExprContext ctx) {
        visit(ctx.expr(0));
        visit(ctx.expr(1));
        String result = "Pop regA, \n";
        result += "Pop regB, \n";
        switch(ctx.plusOp().getText()) {
            case "+":
                result += "Compute Add regA regB regA, \n";
                break;
            case "-":
                result += "Compute Sub regA regB regA, \n";
                break;
        }
        result += "Push regA, \n";
        prog += result;
        return result;
    }

    @Override
    public String visitCompExpr(EmojiLangParser.CompExprContext ctx) {
        visit(ctx.expr(0));
        visit(ctx.expr(1));
        String result = "Pop regA, \n";
        result += "Pop regB, \n";
        switch(ctx.compOp().getText()) {
            case "<":
                result += "Compute Lt regA regB regA, \n";
                break;
            case "<=":
                result += "Compute LtE regA regB regA, \n";
                break;
            case "==":
                result += "Compute Equal regA regB regA, \n";
                break;
            case ">=":
                result += "Compute GtE regA regB regA, \n";
                break;
            case ">":
                result += "Compute Gt regA regB regA, \n";
                break;
            case "!=":
                result += "Compute NEq regA regB regA, \n";
                break;
        }
        result += "Push regA, \n";
        prog += result;
        return result;
    }

    @Override
    public String visitBoolExpr(EmojiLangParser.BoolExprContext ctx) {
        visit(ctx.expr(0));
        visit(ctx.expr(1));
        String result = "Pop regA, \n";
        result += "Pop regB, \n";
        switch(ctx.boolOp().AND().getText().toLowerCase()) {
            case "and":
                result += "Compute And regA regB regA, \n";
                break;
            case "or":
                result += "Compute Or regA regB regA, \n";
                break;
        }
        result += "Push regA, \n";
        prog += result;
        return result;
    }

    @Override
    public String visitParExpr(EmojiLangParser.ParExprContext ctx) {
        visit(ctx.expr());
        return null;
    }

    @Override
    public String visitIdExpr(EmojiLangParser.IdExprContext ctx) {
        String address = reg(ctx);
        String result = "Load " + address + " regA, \n";
        result += "Push regA, \n";
        prog += result;
        return result;
    }

    @Override
    public String visitNumExpr(EmojiLangParser.NumExprContext ctx) {
        String result = "Load (ImmValue " + Integer.parseInt(ctx.NUM().getText()) + ") regA, \n";
        result += "Push regA, \n";
        prog += result;
        return result;
    }

    @Override
    public String visitTrueExpr(EmojiLangParser.TrueExprContext ctx) {
        String result = "Load (ImmValue 1) regA, \n";
        result += "Push regA, \n";
        prog += result;
        return result;
    }

    @Override
    public String visitFalseExpr(EmojiLangParser.FalseExprContext ctx) {
        String result = "Load (ImmValue 0) regA, \n";
        result += "Push regA, \n";
        prog += result;
        return result;
    }

    private String reg(ParseTree node) {
        String result = this.varmap.get(node.getText());
        if (result == null) {
            result = "(DirAddr " + regCount + ")";
            this.varmap.put(node.getText(), result);
            this.regCount += 4;
        }
        return result;
    }
}

