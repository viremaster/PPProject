package main.java.project;

import java.util.*;

import main.java.project.antlr.EmojiLangBaseVisitor;
import main.java.project.antlr.EmojiLangParser;
import org.antlr.v4.runtime.tree.ParseTree;

public class SprockelMaker extends EmojiLangBaseVisitor<String> {

    private String prog;

    private int regCount;

    private SymbolTableNestedScopesInteger varmap = new SymbolTableNestedScopesInteger();
    
    private Map<String, Integer> gvars = new HashMap<>();
   
    
    private int totalThreads = 0;
    
    public String generate(ParseTree tree) {
        gvars.put("threadstarterlock", 0);
        gvars.put("threadrunnerlock", 1);
        gvars.put("threadtargetlock", 2);
        gvars.put("threadcounter", 3);
        this.prog = "";
        this.regCount = 0;
        tree.accept(this);
        return this.prog;
    }

    
    //Concurrency

    @Override
    public String visitProgram(EmojiLangParser.ProgramContext ctx) {
    	
    	int trl = gvars.get("threadrunnerlock");
    	int ttl = gvars.get("threadtargetlock");
    	int tc = gvars.get("threadcounter");
    	
    	prog += "Compute Equal regSprID reg0 regB, \n";
    	prog += "Branch regB (Rel 21), \n";

    	prog += "TestAndSet (DirAddr " + trl + "), \n";
    	prog += "Receive regA, \n";
    	prog += "Compute Equal regA reg0 regB, \n";
        prog += "Branch regB (Rel (-3)), \n";
        
        // increase thread counter
        
        prog += "ReadInstr (DirAddr " + tc + "), \n";
        prog += "Receive regA, \n";
        prog += "Load (ImmValue 1) regB, \n";
        prog += "Compute Add regA regB regA, \n";
        prog += "WriteInstr regA (DirAddr " + tc + "), \n";
        prog += "ReadInstr (DirAddr " + tc + "), \n";
        prog += "Receive regB, \n";
        prog += "Compute NEq regA regB regC, \n";
        prog += "Branch regC (Rel (-4)), \n";
        
    	// settarget
        
        prog += "ReadInstr (DirAddr " + ttl + "), \n";
    	prog += "Receive regA, \n";
    	prog += "Compute Equal regA reg0 regB, \n";
        prog += "Branch regB (Rel (-3)), \n";
        prog += "WriteInstr reg0 (DirAddr " + ttl + "), \n";
        
    	// unlock
        prog += "WriteInstr reg0 (DirAddr " + trl + "), \n";
        
        // jump
        
        prog += "Jump (Ind regA), \n";
        
    	
        visit(ctx.block());
        prog += "EndProg";
        return null;
    }
    
    @Override
    public String visitJoinstat(EmojiLangParser.JoinstatContext ctx) {
    	String result = "ReadInstr (DirAddr " + gvars.get("threadcounter") + "), \n";
    	result += "Receive regA, \n";
    	result += "Branch regA (Rel (-2)), \n";
    	prog += result;
    	return result;
    }

    @Override
    public String visitDeclgvar(EmojiLangParser.DeclgvarContext ctx) {
    	int size = gvars.keySet().size();
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
    	String result = "WriteInstr reg0 (DirAddr " + gvars.get(ctx.ID().getText()) + "), \n";
    	prog += result;
		return result;
    
    }
    
    @Override
    public String visitParStat(EmojiLangParser.ParStatContext ctx) {
    	
    	SymbolTableNestedScopesInteger temp = this.varmap;
    	this.varmap = new SymbolTableNestedScopesInteger();
    	
    	int trl = gvars.get("threadrunnerlock");
    	int tsl = gvars.get("threadstarterlock");
    	int ttl = gvars.get("threadtargetlock");
    	int tc = gvars.get("threadcounter");
    	
    	// lock
    	prog += "TestAndSet (DirAddr " + tsl + "), \n";
    	prog += "Receive regA, \n";
    	prog += "Compute Equal regA reg0 regB, \n";
        prog += "Branch regB (Rel (-3)), \n";
    	
    	// settarget
        prog += "Load (ImmValue ) regA, \n";
        int insert1 = prog.length() - 9;
        prog += "WriteInstr regA (DirAddr " + ttl + "), \n";
        prog += "ReadInstr (DirAddr " + ttl + "), \n";
    	prog += "Receive regA, \n";
        prog += "Branch regA (Rel (-2)), \n";
    	
    	// unlock
        prog += "WriteInstr reg0 (DirAddr " + tsl + "), \n";
    	
    	prog += "Jump (Rel  ), \n";
        int insert2 = prog.length() - 2;
        int split1 = prog.split("\n").length;
        visit(ctx.block());
        
    	prog += "TestAndSet (DirAddr " + trl + "), \n";
    	prog += "Receive regA, \n";
    	prog += "Compute Equal regA reg0 regB, \n";
        prog += "Branch regB (Rel (-3)), \n";
        
        prog += "ReadInstr (DirAddr " + tc + "), \n";
        prog += "Receive regB, \n";
        prog += "Load (ImmValue 1) regB, \n";
        prog += "Compute Sub regA regB regA, \n";
        prog += "WriteInstr regA (DirAddr " + tc + "), \n";
        prog += "ReadInstr (DirAddr " + tc + "), \n";
        prog += "Receive regB, \n";
        prog += "Compute NEq regA regB regC, \n";
        prog += "Branch regC (Rel (-4)), \n";
        
        prog += "WriteInstr reg0 (DirAddr " + trl + "), \n";
        
        prog += "EndProg, \n";
        int split2 = prog.split("\n").length;
        
        prog = prog.substring(0, insert1) + (split1) + prog.substring(insert1, prog.length());
        prog = prog.substring(0, insert2) + (split2 - split1 + 1) + prog.substring(insert2, prog.length());
        
    	this.varmap = temp;
        
		return prog;
    
    }
    
    //End Concurrency
    
    @Override
    public String visitDeclvar(EmojiLangParser.DeclvarContext ctx) {
    	String address = "(DirAddr " + regCount + ")";
        this.varmap.add(ctx.ID().getText(), address);
        this.regCount += 4;
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
	        String address = this.varmap.getInt(ctx.target().getText());
	        result += "Store regA " + address + ", \n";
        }
        prog += result;
        return result;
    }

    @Override
    public String visitOutStat(EmojiLangParser.OutStatContext ctx) {
        visit(ctx.expr());
        prog += "Pop regA, \n";
        prog += "WriteInstr regA numberIO, \n";
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
        prog += "Pop regA, \n";
        prog += "Branch regA (Abs ), \n";
        int ins1 = prog.length() - 4;
        if(ctx.stat().size() == 2) {
            varmap.openScope();
            visit(ctx.stat(1));
            varmap.closeScope();
        }
        prog += "Jump (Abs ), \n";
        int ins2 = prog.length() - 2;
        int split1 = prog.split("\n").length + 1;
        varmap.openScope();
        visit(ctx.stat(0));
        varmap.closeScope();
        int split2 = prog.split("\n").length + 1;
        prog = prog.substring(0, ins1) + (split1) + prog.substring(ins1);
        prog = prog.substring(0, ins2) + (split2) + prog.substring(ins2);
        return null;
    }

    @Override
    public String visitWhileStat(EmojiLangParser.WhileStatContext ctx) {
        int split3 = prog.split("\n").length + 1;
        visit(ctx.expr());
        prog += "Pop regA, \n";
        prog += "Branch regA (Abs ), \n";
        int ins1 = prog.length() - 4;
        prog += "Jump (Abs ), \n";
        int ins2 = prog.length() - 2;
        int split1 = prog.split("\n").length + 1;
        prog = prog.substring(0, ins1) + (split1) + prog.substring(ins1);
        varmap.openScope();
        visit(ctx.stat());
        varmap.closeScope();
        prog += "Jump (Abs ), \n";
        int split2 = prog.split("\n").length + 1;
        prog = prog.substring(0, ins2) + (split2) + prog.substring(ins2);
        int ins3 = prog.length() - 4;
        prog = prog.substring(0, ins3) + (split3) + prog.substring(ins3);
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
	        String address = this.varmap.getInt(ctx.getText());
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

}

