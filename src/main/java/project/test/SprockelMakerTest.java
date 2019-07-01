package main.java.project.test;

import main.java.project.ParseException;
import main.java.project.SprockelMaker;
import main.java.project.antlr.EmojiLangLexer;
import main.java.project.antlr.EmojiLangParser;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;
import java.io.IOException;


public class SprockelMakerTest {
    private final static String ABS_PATH = new File("").getAbsolutePath();
    private final static String BASE_DIR = "\\src\\main\\java\\project\\test";
    private final static String EXT = ".emoji";
    private static SprockelMaker sprockelMaker = new SprockelMaker();

    public static void main(String[] args) throws IOException, ParseException {
        ParseTree parsed = parse("whileTest");
        String generated = sprockelMaker.generate(parsed);
        System.out.println(generated);
    }

    private static ParseTree parse(String filename) throws IOException, ParseException {
        Lexer lexer = new EmojiLangLexer(CharStreams.fromPath(new File(ABS_PATH + BASE_DIR, filename + EXT).toPath()));
        TokenStream tokens = new CommonTokenStream(lexer);
        EmojiLangParser parser = new EmojiLangParser(tokens);
        ParseTree result = parser.program();
        return result;
    }

}
