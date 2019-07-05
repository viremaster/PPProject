package main.java.project.test;


import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

import main.java.project.Checker;
import main.java.project.ParseException;
import main.java.project.antlr.EmojiLangLexer;
import main.java.project.antlr.EmojiLangParser;

public class CheckerTest {
	private final static String ABS_PATH = new File("").getAbsolutePath();
	private final static String BASE_DIR = "\\src\\src\\main\\java\\project\\test\\TypeCheckertestFiles\\";
	private final static String EXT = ".emoji";

	//testing variables with the same name.
	@Test
	public void VarsWithSameNames() throws IOException, ParseException {
		//all combinations of variables.
		checkFail("VarSameNameGlobalLocal");
		checkFail("VarSameNameGlobalLock");
		checkFail("VarSameNameLocalGlobal");
		checkFail("VarSameNameLocalLock");
		checkFail("VarSameNameLockLocal");
		checkFail("VarSameNameLockGlobal");
		checkFail("VarSameNameLocalLocal");
		checkFail("VarSameNameGlobalGlobal");
		
		//Special case multiple locks can be of the same name;
		check("VarSameNameLockLock");
	}
	
	//Scopes inside if and while
	@Test
	public void IfWhileScopes() throws IOException, ParseException {
		//Global variables in if statement.
		check("ifGlobal");
		//Local variables in if statement.
		check("ifLocal");
		//Local variables after statement.
		checkFail("ifLocalAfter");
		
		//Check if the else statement is in the same scope as if statement
		checkFail("ifElseScopes");
		
		//Global variables in while loop.
		check("whileGlobal");
		//Local variables in while loop.
		check("whileLocal");
		//Local variables after while loop.
		checkFail("whileLocalAfter");
	}
	
	
	//Testing scope lists with threads.
	@Test
	public void ThreadScopes() throws IOException, ParseException {
		//Local variables
		//Creating new variables in threads.
		check("ThreadsLocalVars1");
		//Reusing variables in threads.
		checkFail("ThreadsLocalVars2");
		//Local var not working anymore after thread.
		checkFail("ThreadsLocalAfterThread");
		
		//Global variables
		//Creating new global variables in threads.
		checkFail("ThreadsGlobalVars1");
		//Creating new local variable in threads.
		checkFail("ThreadsGlobalVars2");
		//Reusing global variables in threads.
		check("ThreadsGlobalVars3");
		
	}
	
	//Global vars only in top level of main thread.
	@Test
	public void GlobalMainThreadTop() throws IOException, ParseException {
		//global var in top of main thread.
		check("Globalmaintop");
		//global var in if.
		checkFail("Globalif");
		//global var in else part of if.
		checkFail("Globalif2");
		//global var in while.
		checkFail("Globalwhile");
		//global var in thread.
		checkFail("GlobalThread");
	}

	//Global vars only in main thread.
	@Test
	public void JoinMainThreadTop() throws IOException, ParseException {
		//join in top of main thread.
		check("Joinmaintop");
		//join in if.
		check("Joinif");
		//join in else part of if.
		check("Joinif2");
		//join in while.
		check("Joinwhile");
		//join in thread.
		checkFail("JoinThread");
	}
	
	//Global vars only in top level of main thread.
		@Test
		public void UnlockAfterLock() throws IOException, ParseException {
		//unlock after a lock.
		check("UnlockAfterLock");
		//unlocking before a lock.
		checkFail("UnlockBeforeLock");
		//Locking in threads, if or while is ok as declaring variables is a compiler thing.
		check("UnlockIf");
		check("UnlockWhile");
		check("UnlockThread");
		}

	//Checks a file and succeeds if a parseException is thrown.
	private void checkFail(String filename) throws IOException {
		try {
			check(filename);
			fail(filename + " shouldn't check but did");
		} catch (ParseException exc) {
		}
	}
	
	//Checks a file using the type checker.
	private void check(String filename) throws ParseException, IOException {
		new Checker().check(parse(filename));
	}

	//Parses a file.
	private ParseTree parse(String filename) throws IOException, ParseException {
		Lexer lexer = new EmojiLangLexer(CharStreams.fromPath(new File(ABS_PATH + BASE_DIR, filename + EXT).toPath()));
		TokenStream tokens = new CommonTokenStream(lexer);
		EmojiLangParser parser = new EmojiLangParser(tokens);
		ParseTree result = parser.program();
		return result;
	}
}
