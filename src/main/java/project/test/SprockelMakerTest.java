package main.java.project.test;

import main.java.project.SprockelMaker;
import main.java.project.antlr.EmojiLangLexer;
import main.java.project.antlr.EmojiLangParser;
import main.java.project.tobedeleted.ErrorListener;
import main.java.project.tobedeleted.ParseException;
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
        ParseTree parsed = parse("basic");
        String generated = sprockelMaker.generate(parsed);
        System.out.println(generated);
    }

    private static ParseTree parse(String filename) throws IOException, ParseException {
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

}
