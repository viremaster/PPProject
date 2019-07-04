package main.java.project.test;


import java.io.File;
import java.io.IOException;

import main.java.project.antlr.EmojiLangLexer;
import main.java.project.antlr.EmojiLangParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import org.junit.Test;

import main.java.project.Checker;
import main.java.project.ParseException;
import main.java.project.Result;
import main.java.project.Type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@SuppressWarnings("javadoc")
public class CheckerTest {
	private final static String ABS_PATH = new File("").getAbsolutePath();
	private final static String BASE_DIR = "\\src\\src\\main\\java\\project\\test\\TypeCheckertestFiles\\";
	private final static String EXT = ".emoji";

	//Scopes inside if and while
	@Test
	public void IfWhileScopes() throws IOException, ParseException {
		//Local variables
		//Creating new variables in threads.
		check("ThreadsLocalVars1");
		//Reusing variables in threads.
		checkFail("ThreadsLocalVars2");
		
		//Global variables
		//Creating new global variables in threads.
		checkFail("ThreadsGlobalVars1");
		//Creating new local variable in threads.
		checkFail("ThreadsGlobalVars2");
		//Reusing global variables in threads.
		check("ThreadsGlobalVars3");
	}
	
	
	//Testing scope lists with threads.
	@Test
	public void ThreadScopes() throws IOException, ParseException {
		//Local variables
		//Creating new variables in threads.
		check("ThreadsLocalVars1");
		//Reusing variables in threads.
		checkFail("ThreadsLocalVars2");
		
		//Global variables
		//Creating new global variables in threads.
		checkFail("ThreadsGlobalVars1");
		//Creating new local variable in threads.
		checkFail("ThreadsGlobalVars2");
		//Reusing global variables in threads.
		check("ThreadsGlobalVars3");
	}
	


	private void checkFail(String filename) throws IOException {
		try {
			check(filename);
			fail(filename + " shouldn't check but did");
		} catch (ParseException exc) {
		}
	}
	
	private void check(String filename) throws ParseException, IOException {
		new Checker().check(parse(filename));
	}

	private ParseTree parse(String filename) throws IOException, ParseException {
		Lexer lexer = new EmojiLangLexer(CharStreams.fromPath(new File(ABS_PATH + BASE_DIR, filename + EXT).toPath()));
		TokenStream tokens = new CommonTokenStream(lexer);
		EmojiLangParser parser = new EmojiLangParser(tokens);
		ParseTree result = parser.program();
		return result;
	}
}
