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
    
    private Map<String, Integer> gvars = new HashMap<>();
    
    private int totalThreads = 0;
    
    public String generate(ParseTree tree) {
        this.prog = "";
        this.regs = new ParseTreeProperty<>();
        this.regCount = 0;
        tree.accept(this);
        return this.prog;
    }

    
    //Concurrency
    
    @Override
    public String visitDeclgvar(EmojiLangParser.DeclgvarContext ctx) {
    	int size = varmap.keySet().size();
        gvars.put(ctx.ID().getText(), size);
        visit(ctx.expr());
        String result = "Pop regA, \n";
        result += "WriteInstr regA (DirAddr " + size + "), \n";
    	result += "ReadInstr (DirAddr " + size + "), \n";
    	result += "Receive regB, \n";
    	result += "Compute NEq regA regB regC, \n";
    	result += "Branch regC (Rel (-4)), \n";
        prog += result;
        return result;
    
    }
    
    @Override
    public String visitLockStat(EmojiLangParser.LockStatContext ctx) {
    	Integer memaddr = 0;
    	String id = ctx.ID().getText();
    	if (gvars.containsKey(id)) {
        	System.out.println(2);
    		memaddr = gvars.get(id);
    	} else {
    		memaddr = gvars.keySet().size();
    		gvars.put(ctx.ID().getText(), memaddr);
    	}
    	String result = "TestAndSet (DirAddr " + memaddr + "), \n";
    	result += "Receive regA, \n";
        result += "Compute Equal regA reg0 regB, \n";
    	result += "Branch regB (Rel (-3)), \n";
    	prog += result;
		return result;
    
    }
    
    @Override
    public String visitUnlockStat(EmojiLangParser.UnlockStatContext ctx) {
    	String result = "Load (ImmValue 0) regA, \n";
    	result += "WriteInstr regA (DirAddr " + gvars.get(ctx.ID().getText()) + "), \n";
    	prog += result;
		return result;
    
    }
    
    @Override
    public String visitParStat(EmojiLangParser.ParStatContext ctx) {
    	prog += "Load (ImmValue ) regA, \n";
        int insert1 = prog.length() - 9;
        prog += "Compute Lt regA regSprID regA, \n";
        
    	prog += "Branch regA (Rel ), \n";
        int insert2 = prog.length() - 3;
        int split1 = prog.split("\n").length;
        
        visit(ctx.block(0));
        prog += "Jump (Rel ), \n";
        int insert3 = prog.length() - 1;
        int split2 = prog.split("\n").length;
        
        prog = prog.substring(0, insert1) + totalThreads++ + prog.substring(insert1, prog.length());
        
        visit(ctx.block(1));
        int split3 = prog.split("\n").length;
        

        prog = prog.substring(0, insert2) + (split2 - split1 + 1) + prog.substring(insert2, prog.length());
        prog = prog.substring(0, insert3) + (split3 - split2 + 1) + prog.substring(insert3, prog.length());
		return prog;
    
    }
    
    //End Concurrency
    
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
        //Concurrency
        if (gvars.containsKey(ctx.target().getText())) {
        	int maddr = gvars.get(ctx.target().getText());
        	result += "WriteInstr regA (DirAddr " + maddr + "), \n";
        	result += "ReadInstr (DirAddr " + maddr + "), \n";
        	result += "Receive regB, \n";
        	result += "Compute NEq regA regB regC, \n";
        	result += "Branch regC (Rel (-4)), \n";
        } else {
	        String address = reg(ctx.target());
	        result += "Store regA " + address + ", \n";
        }
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
    	String result = "";
    	String id = ctx.ID().getText();
    	if (gvars.containsKey(id)) {
        	result += "ReadInstr (DirAddr " + gvars.get(id) + "), \n";
        	result += "Receive regA, \n";
    	} else {
	        String address = reg(ctx);
	        result = "Load " + address + " regA, \n";
    	}
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

