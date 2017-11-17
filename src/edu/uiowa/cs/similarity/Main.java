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
		}

        if (cmd.hasOption("h")) {
            HelpFormatter helpf = new HelpFormatter();
            helpf.printHelp("Main", options, true);
            System.exit(0);
            
        // Part 1 
        
        String s = "";
        
        // Create Scanner
        Scanner file = new Scanner(new File(filename)).useDelimiter(".!?\\s");
        
        // Create array to put sentences into
        List<String> sentence = new ArrayList<>();
        
        while (file.hasNext()) {
            s = file.next();
            sentence.add(s);
        }
        file.close();
        
        String[] sentenceArray = sentence.toArray(new String[0]);
        
        for (String x : sentenceArray) {
            System.out.println(x);
        }
        }
    }
}
