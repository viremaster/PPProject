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
        varmap.put(ctx.ID().getText(), reg(ctx));
        visit(ctx.expr());
        return null;
    }

    @Override
    public String visitAssStat(EmojiLangParser.AssStatContext ctx) {
        visit(ctx.target());
        String R1 = res;
        visit(ctx.expr());
        String R2 = res;
        String result = "Load " + R2 + " " + R1 + ", \n";
        prog += result;
        return result;
    }

    @Override
    public String visitIdTarget(EmojiLangParser.IdTargetContext ctx) {
        visit(ctx.ID());
        res = varmap.get(ctx.ID().getText());
        return null;
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
        int y = 0; //size of the jump, so after the stat part and the jump
        prog += "Branch " + res + " (Abs y)" + ", \n";
        if(ctx.stat().size() == 2) {
            visit(ctx.stat(1));
        }
        int x = 0; //size of the jump, so after the if part
        prog += "Jump (Rel x)" + ", \n";
        visit(ctx.stat(0));
        return null;
    }

    @Override
    public String visitWhileStat(EmojiLangParser.WhileStatContext ctx) {
        // Labels.. still.. dont.. work.. why am I like this!
        int y = 0; //size of the jump, so after the jump
        visit(ctx.expr());
        prog += "Branch " + res + " (Abs y)" + ", \n";
        int z = 0; //size of the jump, so to after the while loop
        prog += "Jump (Rel z)" + ", \n";
        visit(ctx.stat());
        int x = 0; //size of the jump, so back to the comparison
        prog += "Jump (Rel x)" + ", \n";
        return null;
    }

    @Override
    public String visitPrfExpr(EmojiLangParser.PrfExprContext ctx) {
        visit(ctx.expr());
        String R1 = res;
        String R2 = reg(ctx);
        String result = "";
        if(ctx.prfOp().getText().equals("-")){
            result += "Compute Mul " + R1 + " -1 " + R2 + ", \n";
        } else {
            result += "Compute Xor " + R1 + " true " + R2 + ", \n";
        }
        res = R2;
        prog += result;
        return result;
    }

    @Override
    public String visitMultExpr(EmojiLangParser.MultExprContext ctx) {
        String result = "";
        visit(ctx.expr(0));
        String R1 = res;
        visit(ctx.expr(1));
        String R2 = res;
        String R3 = reg(ctx);
        result += "Compute Mul " + R1 + "" + R2 + "" + R3 + ", \n";
        res = R3;
        prog += result;
        return result;
    }

    @Override
    public String visitPlusExpr(EmojiLangParser.PlusExprContext ctx) {
        visit(ctx.expr(0));
        String R1 = res;
        visit(ctx.expr(1));
        String R2 = res;
        String R3 = reg(ctx);
        String result = "";
        switch(ctx.plusOp().getText()) {
            case "+":
                result += "Compute Add " + R1 + "" + R2 + "" + R3 + ", \n";
                break;
            case "-":
                result += "Compute Sub " + R1 + "" + R2 + "" + R3 + ", \n";
                break;
        }
        res = R3;
        prog += result;
        return result;
    }

    @Override
    public String visitCompExpr(EmojiLangParser.CompExprContext ctx) {
        visit(ctx.expr(0));
        String R1 = res;
        visit(ctx.expr(1));
        String R2 = res;
        String R3 = reg(ctx);
        String result = "";
        switch(ctx.compOp().getText()) {
            case "<":
                result += "Compute Lt " + R1 + "" + R2 + "" + R3 + ", \n";
                break;
            case "<=":
                result += "Compute LtE " + R1 + "" + R2 + "" + R3 + ", \n";
                break;
            case "==":
                result += "Compute Equal " + R1 + "" + R2 + "" + R3 + ", \n";
                break;
            case ">=":
                result += "Compute GtE " + R1 + "" + R2 + "" + R3 + ", \n";
                break;
            case ">":
                result += "Compute Gt " + R1 + "" + R2 + "" + R3 + ", \n";
                break;
            case "!=":
                result += "Compute NEq " + R1 + "" + R2 + "" + R3 + ", \n";
                break;
        }
        res = R3;
        prog += result;
        return result;
    }

    @Override
    public String visitBoolExpr(EmojiLangParser.BoolExprContext ctx) {
        visit(ctx.expr(0));
        String R1 = res;
        visit(ctx.expr(1));
        String R2 = res;
        String R3 = reg(ctx);
        String result = "";
        switch(ctx.boolOp().AND().getText().toLowerCase()) {
            case "and":
                result += "Compute And " + R1 + "" + R2 + "" + R3 + ", \n";
                break;
            case "or":
                result += "Compute Or " + R1 + "" + R2 + "" + R3 + ", \n";
                break;
        }
        res = R3;
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
        res = varmap.get(ctx.ID().getText());
        return null;
    }

    @Override
    public String visitNumExpr(EmojiLangParser.NumExprContext ctx) {
        String R1 = reg(ctx);
        String result = "Load (ImmValue " + Integer.parseInt(ctx.NUM().getText()) + ") " + R1 + ", \n";
        res = R1;
        prog += result;
        return result;
    }

    @Override
    public String visitTrueExpr(EmojiLangParser.TrueExprContext ctx) {
        String R1 = reg(ctx);
        String result = "Load (ImmValue 1) " + R1 + ", \n";
        res = R1;
        prog += result;
        return result;
    }

    @Override
    public String visitFalseExpr(EmojiLangParser.FalseExprContext ctx) {
        String R1 = reg(ctx);
        String result = "Load (ImmValue 0) " + R1 + ", \n";
        res = R1;
        prog += result;
        return result;
    }

    private String reg(ParseTree node) {
        String result = this.regs.get(node);
        if (result == null) {
            result = this.registerlist.get(this.regCount%6);
            this.regs.put(node, result);
            this.regCount++;
        }
        return result;
    }
}

