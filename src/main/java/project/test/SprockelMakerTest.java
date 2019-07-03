package main.java.project.test;

import main.java.project.ParseException;
import main.java.project.SprockelMaker;
import main.java.project.antlr.EmojiLangLexer;
import main.java.project.antlr.EmojiLangParser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;


public class SprockelMakerTest {
    private final static String ABS_PATH = new File("").getAbsolutePath();
    private final static String BASE_DIR_INPUT = "\\src\\main\\java\\project\\test\\testFiles\\";
    private final static String BASE_DIR_OUTPUT = "\\src\\main\\java\\project\\test\\outputFiles\\";
    private static SprockelMaker sprockelMaker = new SprockelMaker();

    public static void main(String[] args) throws IOException, ParseException {
        for(File file : new File(ABS_PATH + BASE_DIR_INPUT).listFiles()) {
            String filename = file.getName();
            filename = filename.substring(0, filename.length()-6);
            ParseTree parsed = parse(filename);
            String generated = sprockelMaker.generate(parsed);
            File output = new File(ABS_PATH + BASE_DIR_OUTPUT + filename + ".hs");
            output.createNewFile();
            FileOutputStream fos = new FileOutputStream(ABS_PATH + BASE_DIR_OUTPUT + filename + ".hs");
            fos.write(generated.getBytes());
            fos.flush();
            fos.close();
            System.out.println("Test \"" + filename + "\" files generated successfully");
        }
    }

    private static ParseTree parse(String filename) throws IOException, ParseException {
        CharStream s = CharStreams.fromFileName(ABS_PATH + BASE_DIR_INPUT + filename + ".emoji", Charset.forName("utf-8"));
        Lexer lexer = new EmojiLangLexer(s);
        TokenStream tokens = new CommonTokenStream(lexer);
        EmojiLangParser parser = new EmojiLangParser(tokens);
        ParseTree result = parser.program();
        return result;
    }

}
