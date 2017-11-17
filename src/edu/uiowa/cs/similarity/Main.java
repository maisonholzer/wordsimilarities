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
                    int sentenceCount = 0;
                    
                    // Create Scanner
                    Scanner file = new Scanner(new File(filename));
                    file.useDelimiter("[.?!]");
                    
                    // Create array to put sentence arrays into
                    List<List<String>> retVal = new ArrayList<>();
                    
                    String sentence;
                    while (file.hasNext()) {
                        sentence = file.next();
                        
                        List<String> sent = stemAndRemoveStopWords(sentence);
                        
                        if (!(sent.toString().contains("[]"))) {
                            retVal.add(sent);
                            sentenceCount++;
                        }
                    }
                    
                    // If "s" is added to the argument, print the array.
                    if (cmd.hasOption("s")) { // move this back down somehow once finished
                        System.out.println(retVal.toString());
                        System.out.println("Number of Sentences: " + sentenceCount);
                    }
                    file.close();
        }

        if (cmd.hasOption("h")) {
            HelpFormatter helpf = new HelpFormatter();
            helpf.printHelp("Main", options, true);
            System.exit(0);
        

        
        }
    }

    private static List<String> stemAndRemoveStopWords(String sentence) throws IOException {
        // Create Stop Words List
        Set<String> stopWordList = new HashSet<String>();
        Scanner stopWordsScanner = new Scanner(new File("../stopwords.txt"));
        String x = "";
        while (stopWordsScanner.hasNext()) {
            x = stopWordsScanner.nextLine();
            stopWordList.add(x);
        }
        
        sentence = sentence.replaceAll("\\r?\\n", " ");
        sentence = sentence.replace("--", "");
        sentence = sentence.replace(",", "");
        sentence = sentence.replace(":", "");
        sentence = sentence.replace(";", "");
        sentence = sentence.replace("\"", "");
        sentence = sentence.toLowerCase();
        sentence = sentence.trim();
                        
        List<String> sent = new ArrayList<>();
        // Stem the words using PorterStemmer and remove StopWords
        PorterStemmer stemmer = new PorterStemmer();
        for (String word : sentence.split(" ")) {
            if (!stopWordList.contains(word)) {
                word = stemmer.stem(word);
                word.replace("\'", "");
                sent.add(word);
            }
        }
        
        return sent;
    }
}
