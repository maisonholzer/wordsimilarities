package edu.uiowa.cs.similarity;

import org.apache.commons.cli.*;
import java.util.Scanner;
import opennlp.tools.stemmer.*;
import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        Options options = new Options();
        options.addRequiredOption("f", "file", true, "input file to process");
        options.addOption("h", false, "print this help message");
        // Add print statement with -s 
        options.addOption("s", false, "print final array of words");

        CommandLineParser parser = new DefaultParser();

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            new HelpFormatter().printHelp("Main", options, true);
            System.exit(1);
        }

        String filename = cmd.getOptionValue("f");
		if (!new File(filename).exists()) {
			System.err.println("file does not exist "+filename);
			System.exit(1);
		} else {
                    // Part 1 
        
                    String s = "";
                    int sentenceCount = 0;
                    
                    // Create Scanner
                    Scanner file = new Scanner(new File(filename));
                    file.useDelimiter("[.?!]");
                    // Create array to put words into
                    List<String> retVal = new ArrayList<>();
                    
                    String sentence;
                    while (file.hasNext()) {
                        List<String> sent = new ArrayList<>();
                        sentence = file.next();
                        sentence = sentence.replaceAll("\\r?\\n", " ");
                        sentence = sentence.replace("--", "");
                        sentence = sentence.replace(",", "");
                        sentence = sentence.replace(":", "");
                        sentence = sentence.replace(";", "");
                        sentence = sentence.replace("\"", "");
                        sentence = sentence.replace("\'", "");
                        sentence = sentence.toLowerCase();
                        sentence = sentence.trim();
                        if (cmd.hasOption("s")) {
                            System.out.println(sentence);
                        }
                    }
                    
        
                    // String[] sentenceArray = sentence.toArray(new String[0]);
        

                    // If "s" is added to the argument, print the array.
                    
                    file.close();
        }

        if (cmd.hasOption("h")) {
            HelpFormatter helpf = new HelpFormatter();
            helpf.printHelp("Main", options, true);
            System.exit(0);
        

        
        }
    }
}
