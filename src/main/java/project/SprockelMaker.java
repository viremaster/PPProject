package main.java.project;

import java.util.*;

import main.java.project.antlr.EmojiLangBaseVisitor;
import main.java.project.antlr.EmojiLangParser;
import org.antlr.v4.runtime.tree.ParseTree;

public class SprockelMaker extends EmojiLangBaseVisitor<String> {

    //The String that stores the program
    private String prog;

    //The integer keeping count of the registers
    private int regCount;

    //The SymbolTable that keeps track of all variables in the scopes.
    private SymbolTableNestedScopesInteger varmap = new SymbolTableNestedScopesInteger();

    //The map that keeps track of all global variables.
    private Map<String, Integer> gvars = new HashMap<>();
   
    //The integer storing how many Threads are used in this code.
    private int totalThreads = 0;

    //Calling this function will generate the corresponding Sprockel code of a parsetree
    public String generate(ParseTree tree) {
        gvars.put("threadstarterlock", 0);
        gvars.put("threadrunnerlock", 1);
        gvars.put("threadtargetlock", 2);
        gvars.put("threadcounter", 3);
        this.prog = "";
        this.regCount = 0;
        tree.accept(this);
        String[] split = this.prog.split("\n");
        String newprog = "";
        for(String s:split){
            newprog += "       " + s + "\n";
        }
        newprog =   "import Sprockell\n" +
                    "\n" +
                    "prog :: [Instruction]\n" +
                    "prog = [ \n" + newprog;
        newprog +=  "       ]\n" +
                    "\n" +
                    "main = run [prog";
        for (int i = 0; i < totalThreads; i++) {
        	newprog += ", prog";
        }
        newprog += "]";

        return newprog;
    }

    
    //When visiting a program first the setup is done for concurrency, making the code that makes the threads visit different parts of the code.
    @Override
    public String visitProgram(EmojiLangParser.ProgramContext ctx) {
    	
    	int trl = gvars.get("threadrunnerlock");
    	int ttl = gvars.get("threadtargetlock");
    	int tc = gvars.get("threadcounter");
        visit(ctx.block());
    	if(totalThreads != 0) {
            String newprog = "Compute Equal regSprID reg0 regB, \n";
            newprog += "Branch regB (Rel 21), \n";

            newprog += "TestAndSet (DirAddr " + trl + "), \n";
            newprog += "Receive regA, \n";
            newprog += "Compute Equal regA reg0 regB, \n";
            newprog += "Branch regB (Rel (-3)), \n";

            // increase thread counter

            newprog += "ReadInstr (DirAddr " + tc + "), \n";
            newprog += "Receive regA, \n";
            newprog += "Load (ImmValue 1) regB, \n";
            newprog += "Compute Add regA regB regA, \n";
            newprog += "WriteInstr regA (DirAddr " + tc + "), \n";
            newprog += "ReadInstr (DirAddr " + tc + "), \n";
            newprog += "Receive regB, \n";
            newprog += "Compute NEq regA regB regC, \n";
            newprog += "Branch regC (Rel (-4)), \n";

            // settarget

            newprog += "ReadInstr (DirAddr " + ttl + "), \n";
            newprog += "Receive regA, \n";
            newprog += "Compute Equal regA reg0 regB, \n";
            newprog += "Branch regB (Rel (-3)), \n";
            newprog += "WriteInstr reg0 (DirAddr " + ttl + "), \n";

            // unlock
            newprog += "WriteInstr reg0 (DirAddr " + trl + "), \n";

            // jump

            newprog += "Jump (Ind regA), \n";

            prog = newprog + prog;
        }
        prog += "EndProg";
        return null;
    }

    //When joining the thread waits for the lock to release and then continues.
    @Override
    public String visitJoinstat(EmojiLangParser.JoinstatContext ctx) {
    	String result = "ReadInstr (DirAddr " + gvars.get("threadcounter") + "), \n";
    	result += "Receive regA, \n";
    	result += "Branch regA (Rel (-2)), \n";
    	prog += result;
    	return result;
    }

    //When a global variable is declared it will be set and tested,
    //when it is not set correctly it will try again until it has been set properly
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

    //When a lock is made we see if the lock already exists, if it does we try and set it until we succeed.
    //if it doesn't we make it and set it.
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

    //When we unlock we set the address of the lock to unlocked.
    @Override
    public String visitUnlockStat(EmojiLangParser.UnlockStatContext ctx) {
    	String result = "WriteInstr reg0 (DirAddr " + gvars.get(ctx.ID().getText()) + "), \n";
    	prog += result;
		return result;
    
    }

    //When the parallel segment is visited we lock until the main thread arrives.
    @Override
    public String visitParStat(EmojiLangParser.ParStatContext ctx) {
    	
    	totalThreads++;
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
        int insert2 = prog.length() - 4;
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
        
        prog = prog.substring(0, insert1) + (split1 + 22) + prog.substring(insert1, insert2) + (split2 - split1 + 1) + prog.substring(insert2, prog.length());
        
    	this.varmap = temp;
        
		return prog;
    
    }

    // When a variable is declared it will be add to the Symboltable
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

    // When an assignment is done we check if it is a global variable, if it is we try to change it until we succeed,
    // otherwise we change the local variable.
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

    // Out returns the value in a register to the output
    @Override
    public String visitOutStat(EmojiLangParser.OutStatContext ctx) {
        visit(ctx.expr());
        prog += "Pop regA, \n";
        prog += "WriteInstr regA numberIO, \n";
        return null;
    }

    // The blockstat visits the block
    @Override
    public String visitBlockStat(EmojiLangParser.BlockStatContext ctx) {
        return visit(ctx.block());
    }

    // Block visits all statements in the block
    @Override
    public String visitBlock(EmojiLangParser.BlockContext ctx) {
        for(EmojiLangParser.StatContext st:ctx.stat()){
            visit(st);
        }
        return null;
    }

    //The if loop is restructured so that we put the else first and jump over it if the expression is true
    @Override
    public String visitIfStat(EmojiLangParser.IfStatContext ctx) {

        visit(ctx.expr());
        prog += "Pop regA, \n";
        prog += "Branch regA (Rel ), \n";
        int split0 = prog.split("\n").length;
        int ins1 = prog.length() - 4;
        if(ctx.stat().size() == 2) {
            varmap.openScope();
            visit(ctx.stat(1));
            varmap.closeScope();
        }
        prog += "Jump (Rel ), \n";
        int split1 = prog.split("\n").length;
        prog = prog.substring(0, ins1) + (split1 - split0 + 1) + prog.substring(ins1);
        int ins2 = prog.length() - 4;
        varmap.openScope();
        visit(ctx.stat(0));
        varmap.closeScope();
        int split2 = prog.split("\n").length;
        prog = prog.substring(0, ins2) + (split2 - split1 + 1) + prog.substring(ins2);
        return null;
    }

    //The expression is visited and then we end the loop if the expression is false or we
    // go into the stat if the expression is true, afterwards we jump back to the expression
    @Override
    public String visitWhileStat(EmojiLangParser.WhileStatContext ctx) {
        int split3 = prog.split("\n").length;
        visit(ctx.expr());
        prog += "Pop regA, \n";
        prog += "Branch regA (Rel 2), \n";
        int split1 = prog.split("\n").length;
        prog += "Jump (Rel ), \n";
        int ins2 = prog.length() - 4;
        varmap.openScope();
        visit(ctx.stat());
        varmap.closeScope();
        prog += "Jump (Rel ()), \n";
        int split2 = prog.split("\n").length;
        prog = prog.substring(0, ins2) + (split2 - split1) + prog.substring(ins2);
        int ins3 = prog.length() - 5;
        prog = prog.substring(0, ins3) + (split3 - split2 + 1) + prog.substring(ins3);
        return null;
    }

    //The PrfExpr checks wether we are dealing with a negative number or a boolean situation,
    //changing the value accordingly
    @Override
    public String visitPrfExpr(EmojiLangParser.PrfExprContext ctx) {
        visit(ctx.expr());
        String result = "Pop regA, \n";
        if(ctx.prfOp().getText().equals("-") || ctx.prfOp().getText().equals("➖")){
            result += "Load (ImmValue (-1)) regB, \n";
            result += "Compute Mul regA regB regA, \n";
        } else {
            result += "Load (ImmValue 1) regB, \n";
            result += "Compute Xor regA regB regA, \n";
        }
        result += "Push regA, \n";
        prog += result;
        return result;
    }

    //Visits the two expressions and multiplies them or does a soft integer division
    @Override
    public String visitMultExpr(EmojiLangParser.MultExprContext ctx) {
        visit(ctx.expr(0));
        visit(ctx.expr(1));
        String result = "Pop regB, \n";
        result += "Pop regA, \n";
        switch(ctx.multOp().getText()) {
            case "*":
                result += "Compute Mul regA regB regA, \n";
                result += "Push regA, \n";
                break;
            case "/":
                result += "Load (ImmValue 0) regC, \n";
                result += "Compute Lt regA reg0 regE, \n";
                result += "Compute Lt regB reg0 regF, \n";
                result += "Branch regE (Rel 3), \n";
                result += "Branch regF (Rel 14), \n";
                result += "Jump (Rel 27),\n";
                result += "Branch regF (Rel 23), \n";

                //case for A negative
                result += "Load (ImmValue (-1)) regE, \n";
                result += "Compute Mul regA regE regA, \n";
                result += "Compute GtE regA regB regA, \n";
                result += "Branch regA (Rel 2), \n";
                result += "Jump (Rel 5), \n";
                result += "Load (ImmValue 1) regD, \n";
                result += "Compute Add regC regD regC, \n";
                result += "Compute Sub regA regB regA, \n";
                result += "Jump (Rel (-6)), \n";
                result += "Compute Mul regC regE regC, \n";
                result += "Jump (Rel 22), \n";

                //case for B negative
                result += "Load (ImmValue (-1)) regE, \n";
                result += "Compute Mul regB regE regB, \n";
                result += "Compute GtE regA regB regA, \n";
                result += "Branch regA (Rel 2), \n";
                result += "Jump (Rel 5), \n";
                result += "Load (ImmValue 1) regD, \n";
                result += "Compute Add regC regD regC, \n";
                result += "Compute Sub regA regB regA, \n";
                result += "Jump (Rel (-6)), \n";
                result += "Compute Mul regC regE regC, \n";
                result += "Jump (Rel 11), \n";

                //case for two negative
                result += "Load (ImmValue (-1)) regE, \n";
                result += "Compute Mul regB regE regB, \n";
                result += "Compute Mul regA regE regA, \n";


                //case for two positive
                result += "Compute GtE regA regB regA, \n";
                result += "Branch regA (Rel 2), \n";
                result += "Jump (Rel 5), \n";
                result += "Load (ImmValue 1) regD, \n";
                result += "Compute Add regC regD regC, \n";
                result += "Compute Sub regA regB regA, \n";
                result += "Jump (Rel (-6)), \n";
                result += "Push regC, \n";
        }
        prog += result;
        return result;

    }

    //Visits the two expressions and adds them or subtracts them
    @Override
    public String visitPlusExpr(EmojiLangParser.PlusExprContext ctx) {
        visit(ctx.expr(0));
        visit(ctx.expr(1));
        String result = "Pop regB, \n";
        result += "Pop regA, \n";
        switch(ctx.plusOp().getText()) {
            case "+":
            case "➕":
                result += "Compute Add regA regB regA, \n";
                break;
            case "-":
            case "➖":
                result += "Compute Sub regA regB regA, \n";
                break;
        }
        result += "Push regA, \n";
        prog += result;
        return result;
    }

    //Visits the two expressions and does the boolean comparison on them
    @Override
    public String visitCompExpr(EmojiLangParser.CompExprContext ctx) {
        visit(ctx.expr(0));
        visit(ctx.expr(1));
        String result = "Pop regB, \n";
        result += "Pop regA, \n";
        switch(ctx.compOp().getText()) {
            case "<":
            case "⏪":
                result += "Compute Lt regA regB regA, \n";
                break;
            case "<=":
            case "\u23EE":
                result += "Compute LtE regA regB regA, \n";
                break;
            case "==":
            case "\uD83D\uDD04":
                result += "Compute Equal regA regB regA, \n";
                break;
            case ">=":
            case "⏭":
                result += "Compute GtE regA regB regA, \n";
                break;
            case ">":
            case "⏩":
                result += "Compute Gt regA regB regA, \n";
                break;
            case "!=":
            case "❌\uD83D\uDD04":
                result += "Compute NEq regA regB regA, \n";
                break;
        }
        result += "Push regA, \n";
        prog += result;
        return result;
    }

    //Visits the two expressions and ands them or ors them
    @Override
    public String visitBoolExpr(EmojiLangParser.BoolExprContext ctx) {
        visit(ctx.expr(0));
        visit(ctx.expr(1));
        String result = "Pop regA, \n";
        result += "Pop regB, \n";
        switch(ctx.boolOp().getText().toLowerCase()) {
            case "&&":
            case "\u2194":
                result += "Compute And regA regB regA, \n";
                break;
            case "||":
            case "\u23F8":
                result += "Compute Or regA regB regA, \n";
                break;
        }
        result += "Push regA, \n";
        prog += result;
        return result;
    }

    //Visits the expression in between parentheses
    @Override
    public String visitParExpr(EmojiLangParser.ParExprContext ctx) {
        visit(ctx.expr());
        return null;
    }

    //Takes the value of stored position of the id out of the varmap and loads the value that is in there
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

    //Loads the number that is visited
    @Override
    public String visitNumExpr(EmojiLangParser.NumExprContext ctx) {
        String result = "Load (ImmValue " + Integer.parseInt(ctx.NUM().getText()) + ") regA, \n";
        result += "Push regA, \n";
        prog += result;
        return result;
    }

    //Loads the value true
    @Override
    public String visitTrueExpr(EmojiLangParser.TrueExprContext ctx) {
        String result = "Load (ImmValue 1) regA, \n";
        result += "Push regA, \n";
        prog += result;
        return result;
    }

    //Loads the value false
    @Override
    public String visitFalseExpr(EmojiLangParser.FalseExprContext ctx) {
        String result = "Load (ImmValue 0) regA, \n";
        result += "Push regA, \n";
        prog += result;
        return result;
    }

}

