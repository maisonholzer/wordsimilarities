package edu.uiowa.cs.similarity;

import org.apache.commons.cli.*;
import java.util.Scanner;
import opennlp.tools.stemmer.*;
import java.io.*;
import java.util.*;
import java.util.Iterator;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws IOException {
        Options options = new Options();
        options.addRequiredOption("f", "file", true, "input file to process");
        options.addOption("h", false, "print this help message");
        // Add print statement with -s 
        options.addOption("s", false, "print final array of words");
        options.addOption("v", false, "print unique words along with their semantic vectors");
        options.addOption("t", "query", true, "query word for similarity");
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
                    
                    // Create Scanner for processing file into sentences
                    Scanner file = new Scanner(new File(filename));
                    file.useDelimiter("[.?!]");
           
                    // Create array to put sentence arrays into
                    List<List<String>> retVal = new ArrayList<>();
                    
                    String sentence;
                    
                    //Create a set that contains unique words
                    Set<String> SemanticVecDimension = new LinkedHashSet<>();
                    
                    while (file.hasNext()) {
                        sentence = file.next();
                        
                        List<String> sent = stemAndRemoveStopWords(sentence);
                        
                        if (!(sent.toString().contains("[]"))) { 
                            //check if unique and add word to Semantic Descriptor Dimension Vector
                            for (int i= 0; i<sent.size(); i++)
                                {SemanticVecDimension.add(sent.get(i));}
                            
                            retVal.add(sent);
                            sentenceCount++;
                        }
                    }
                    //turn unique words in HashSet into List 
                    Iterator<String> SemanticVDimension = SemanticVecDimension.iterator(); 
                    List<String> SemanticVectorDimension = new ArrayList();
                    
                    while (SemanticVDimension.hasNext())
                            {SemanticVectorDimension.add(SemanticVDimension.next());}
                    
                    List<List<Integer>> DescriptorVectorsforAllUniqueWords= SemanticVectorsforAllUniqueWords(retVal, SemanticVectorDimension); 
                    
                    // If "s" is added to the argument, print the array.
                    if (cmd.hasOption("s")) { // move this back down somehow once finished
                        System.out.println(retVal.toString());
                        System.out.println("Number of Sentences: " + sentenceCount);}
                        
                    if (cmd.hasOption("v")){
                        System.out.println("Unique words and their descriptor vectors");
                        for (int i = 0; i < SemanticVectorDimension.size(); i++)
                            {System.out.println(SemanticVectorDimension.get(i) + " has semantic vector "+ DescriptorVectorsforAllUniqueWords.get(i));}}
                    
                    if (cmd.hasOption("t")){
                        String queryInput = cmd.getOptionValue("t");
                        List<String> QueryInputList = ProcessingQueryInput(queryInput);
                        String QueryWord = getQueryWord(QueryInputList);
                        int NumberToDisplay = Integer.parseInt(QueryInputList.get(1));
 
                        if (!SemanticVectorDimension.contains(QueryWord))
                            {System.err.println("Cannot compute top " + NumberToDisplay + " similarity to " + QueryWord);}
                        else{
                            //start vector operations to compare similarity of QueryWord with all unique words in text
                            //Create a VectorOperations function called m which can do vector multiplications
                            VectorOperations m = new VectorOperations();
                        
                            //Create a map containing (cosine similarity, unique word) pair stored in natural ordering                       
                            Iterator<Map.Entry<Double, String>> DescendingSimilarityRanking = MapOfSimilarityScores(SemanticVectorDimension, DescriptorVectorsforAllUniqueWords, QueryWord, m);
                        
                            //print only the number of similar words required by command line input, and exclude query word itself (which has similarity score 1.0)
                            int count = 0;
                            while (DescendingSimilarityRanking.hasNext() && count < NumberToDisplay)
                                {   Map.Entry<Double, String> NextPrint = DescendingSimilarityRanking.next();
                                    if (!NextPrint.getValue().equals(QueryWord)) {System.out.println(NextPrint);count++;}
                                }
                        }
                    }      
                file.close();   
                }
        
        if (cmd.hasOption("h")) {
            HelpFormatter helpf = new HelpFormatter();
            helpf.printHelp("Main", options, true);
            System.exit(0);
        }
    }
    
    public static List<String> ProcessingQueryInput(String queryInput){
        queryInput = queryInput.replace(" ","");
        queryInput = queryInput.replace(","," ");
        String[] QueryInput = queryInput.split(" ");
        List<String> QueryInputList = Arrays.asList(QueryInput);
    return QueryInputList;
    }  
    
    public static String getQueryWord(List<String> QueryInputList){
        String QueryWord = QueryInputList.get(0);
        QueryWord = QueryWord.toLowerCase();
        
        PorterStemmer Qstemmer = new PorterStemmer();
        String QueryWordStemmed = Qstemmer.stem(QueryWord);
    return QueryWordStemmed;    
    }
    
    
    // 
    public static Iterator<Map.Entry<Double, String>> MapOfSimilarityScores(List<String> SemanticDimension, List<List<Integer>> SemanticVecs, String queryword, VectorOperations m){
    int index = SemanticDimension.indexOf(queryword);
    List<Integer> QueryWordVector = SemanticVecs.get(index);    
    TreeMap<Double, String> SimilarityRanking = new TreeMap<>(new ComparatorForDuplicates());  
    List<Double> Scores = new ArrayList<>();
    List<String> Names = new ArrayList<>();
    for (int i = 0; i < SemanticDimension.size(); i++)
        {
        double DotProduct = m.DotMultiply(QueryWordVector, SemanticVecs.get(i)); 
        double absProduct = m.absMultiply(QueryWordVector, SemanticVecs.get(i));
        double CosineSimilarity = DotProduct/absProduct;    
        SimilarityRanking.put(CosineSimilarity, SemanticDimension.get(i));
        Names.add(SemanticDimension.get(i));
        Scores.add(CosineSimilarity);
        }
    //System.out.println("Words are" + Names);
    //System.out.println("Scores are" + Scores);
    Map<Double, String> TreeValues= SimilarityRanking.descendingMap();  
    System.out.println("Mappings(Score, word) in this tree is" + TreeValues);
    Iterator<Map.Entry<Double, String>> SimilarityToQuery = TreeValues.entrySet().iterator();
    return SimilarityToQuery;
    }
    
    private static List<List<Integer>> SemanticVectorsforAllUniqueWords(List<List<String>> sentences, List<String> SemanticVec){
    List<List<Integer>> DescriptorVectors = new ArrayList<>();
                    for (int i = 0; i<SemanticVec.size(); i++)
                        {                    
                        List<Integer> WordVector = CreateSemanticVectorforOneWord(sentences,SemanticVec, SemanticVec.get(i));       
                        DescriptorVectors.add(WordVector);
                        }
    return DescriptorVectors;
    }
    
    //create semantic descriptor vector for each unique word.
    private static List<Integer> CreateSemanticVectorforOneWord(List<List<String>> sentences, List<String> SemanticVec, String word){
    //create a new IntegerVector(specified dimension)
    IntegerVector WordVector = new IntegerVector(SemanticVec.size()); 
    
    for (int i = 0; i < SemanticVec.size(); i ++) //check this word + every combination in the Semantic Vector
        {int CoOccur = 0; 
        for (int j = 0; j < sentences.size(); j++)//check if they co-occur for every sentence
        {
            if (sentences.get(j).contains(word)){
                List<String> ThisSentence = new ArrayList<>();
                ThisSentence.addAll(sentences.get(j));
                ThisSentence.remove(word);//remove the current word so we can detect co-occurance of the same word
                if (ThisSentence.contains(SemanticVec.get(i)))
                    {CoOccur++;}
            }    
        }//have checked every sentence for co-occurance
        //This word co-occur "CoOccur" times with word i in the Semantic Vector
        WordVector.addArgument(i, CoOccur);
        }
    return WordVector.getVector();
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
        System.out.println(sentence);
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
        for (String word : sentence.split(" ")) {//I don't understand this line
            if (!stopWordList.contains(word)) {
                word = stemmer.stem(word); 
                word.replace("\'", "");
                sent.add(word);
            }
        }    
        return sent;
    }
}
