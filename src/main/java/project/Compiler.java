package main.java.project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import main.java.project.antlr.EmojiLangLexer;
import main.java.project.antlr.EmojiLangParser;

public class Compiler {

    //The variables to describe the paths of the input and output folder.
    private final static String ABS_PATH = new File("").getAbsolutePath();
    private final static String BASE_DIR_INPUT = "\\src\\src\\main\\java\\project\\Compiler\\";
    private final static String BASE_DIR_OUTPUT = "\\src\\src\\main\\java\\project\\Compilerout\\";

    //The object that turns parseTrees into sprockell code.
    private static Checker checker;
    private static SprockelMaker sprockelMaker;

    //When run this function parses all the files in the folder "testFiles"
    //and then creates the corresponding .hs files of these files in the folder "outputFiles"
    public static void main(String[] args) {
        for(File file : new File(ABS_PATH + BASE_DIR_INPUT).listFiles()) {
        	try {
        	sprockelMaker = new SprockelMaker();
        	checker = new Checker();
            String filename = file.getName();
            filename = filename.substring(0, filename.length()-6);
        	System.out.println("Parsing: \"" + filename + "\"");
            ParseTree parsed = parse(filename);
           	System.out.println("Type Checking: \"" + filename + "\"");
            checker.check(parsed);
           	System.out.println("Generating code: \"" + filename + "\"");
            String generated = sprockelMaker.generate(parsed);
            File output = new File(ABS_PATH + BASE_DIR_OUTPUT + filename + ".hs");
            output.createNewFile();
            FileOutputStream fos = new FileOutputStream(ABS_PATH + BASE_DIR_OUTPUT + filename + ".hs");
            fos.write(generated.getBytes());
            fos.flush();
            fos.close();
            System.out.println("\"" + filename + "\" generated successfully");
        	} catch (ParseException e) {
        		e.print();
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
        }
    }

    //This function creates the Parsetree from a given filename using the EmojiLangParser
    private static ParseTree parse(String filename) throws IOException, ParseException {
        CharStream s = CharStreams.fromFileName(ABS_PATH + BASE_DIR_INPUT + filename + ".emoji", Charset.forName("utf-8"));
        Lexer lexer = new EmojiLangLexer(s);
        TokenStream tokens = new CommonTokenStream(lexer);
        EmojiLangParser parser = new EmojiLangParser(tokens);
        ParseTree result = parser.program();
        return result;
    }
	
}
