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
public class SimpleCheckerTest {
	private final static String ABS_PATH = new File("").getAbsolutePath();
	private final static String BASE_DIR = "\\src\\src\\main\\java\\project\\test";
	private final static String EXT = ".emoji";
	private final Checker checker = new Checker();

	@Test
	public void testBasicTypes() throws IOException, ParseException {
		System.out.println(ABS_PATH);
		ParseTree tree = parse("basic");
		Result result = check(tree);
		System.out.println(tree + " aaa");
		ParseTree body = tree.getChild(3).getChild(1);
		ParseTree assX = body;
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
		Lexer lexer = new EmojiLangLexer(CharStreams.fromPath(new File(ABS_PATH + BASE_DIR, filename + EXT).toPath()));
		TokenStream tokens = new CommonTokenStream(lexer);
		EmojiLangParser parser = new EmojiLangParser(tokens);
		ParseTree result = parser.program();
		return result;
	}

	private Result check(ParseTree tree) throws ParseException {
		return this.checker.check(tree);
	}
}
