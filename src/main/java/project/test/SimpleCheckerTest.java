package main.java.project.test;


import java.io.File;
import java.io.IOException;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import org.junit.Test;

import main.java.project.Checker;
import main.java.project.Type;
import main.java.project.tobedeleted.ErrorListener;
import main.java.project.tobedeleted.ParseException;
import main.java.project.tobedeleted.Result;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import pp.block6.cc.pascal.*;

@SuppressWarnings("javadoc")
public class SimpleCheckerTest {
	private final static String ABS_PATH = new File("").getAbsolutePath();
	private final static String BASE_DIR = "\\src\\src\\main\\java\\project\\test";
	private final static String EXT = ".pascal";
	private final Checker checker = new Checker();

	@Test
	public void testBasicTypes() throws IOException, ParseException {
		System.out.println(ABS_PATH);
		ParseTree tree = parse("basic");
		Result result = check(tree);
		System.out.println(tree + " aaa");
		ParseTree body = tree.getChild(3).getChild(1);
		ParseTree assX = body;
		assertEquals(Type.BOOL, result.getType(assX.getChild(0)));
		assertEquals(Type.BOOL, result.getType(assX.getChild(3)));
	}

	private void checkFail(String filename) throws IOException {
		try {
			check(parse(filename));
			fail(filename + " shouldn't check but did");
		} catch (ParseException exc) {
			// this is the expected behaviour
		}
	}

	private ParseTree parse(String filename) throws IOException, ParseException {
		ErrorListener listener = new ErrorListener();
		Lexer lexer = new EmojiLangLexer(CharStreams.fromPath(new File(ABS_PATH + BASE_DIR, filename + EXT).toPath()));
		lexer.removeErrorListeners();
		lexer.addErrorListener(listener);
		TokenStream tokens = new CommonTokenStream(lexer);
		EmojiLangParser parser = new EmojiLangParser(tokens);
		parser.removeErrorListeners();
		parser.addErrorListener(listener);
		ParseTree result = parser.program();
		listener.throwException();
		return result;
	}

	private Result check(ParseTree tree) throws ParseException {
		return this.checker.check(tree);
	}
}
