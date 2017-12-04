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
        final long startTime = System.currentTimeMillis();
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
                    
                    TreeMap<String, IntegerVectorMap> DescriptorVectorsforAllUniqueWords= SemanticVectorsforAllUniqueWords(retVal, SemanticVectorDimension); 
                    
                    // If "s" is added to the argument, print the array.
                    if (cmd.hasOption("s")) { // move this back down somehow once finished
                        System.out.println(retVal.toString());
                        System.out.println("Number of Sentences: " + sentenceCount);}
                        
                    if (cmd.hasOption("v")){//this had error
                        System.out.println("Unique words and their descriptor vectors");
                        for (int i = 0; i < SemanticVectorDimension.size(); i++)
                            //{System.out.println(SemanticVectorDimension.get(i) + " has semantic vector "+ DescriptorVectorsforAllUniqueWords.get(SemanticVectorDimension.get(i)).getMap().descendingMap());}}
                             {System.out.println(SemanticVectorDimension.get(i) + " has semantic vector "+ DescriptorVectorsforAllUniqueWords.get(SemanticVectorDimension.get(i)).getMap().entrySet());}}
                    System.out.println("completed up to the cosine similarity part");
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
                file.close();   final long endTime = System.currentTimeMillis();
                System.out.println("Excution time is:" + (endTime - startTime));
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
    public static Iterator<Map.Entry<Double, String>> MapOfSimilarityScores(List<String> SemanticDimension, TreeMap<String, IntegerVectorMap> SemanticVecs, String queryword, VectorOperations m){
    //int index = SemanticDimension.indexOf(queryword);
    IntegerVectorMap QueryWordVector = SemanticVecs.get(queryword);    
    TreeMap<Double, String> SimilarityRanking = new TreeMap<>(new ComparatorForDuplicates());  
    //compare query word with every word 
    for (int i = 0; i < SemanticDimension.size(); i++)
        {
        double DotProduct = m.DotMultiply(QueryWordVector.getMap(), SemanticVecs.get(SemanticDimension.get(i)).getMap()); 
        double absProduct = m.absMultiply(QueryWordVector.getMap(), SemanticVecs.get(SemanticDimension.get(i)).getMap());
        double CosineSimilarity = DotProduct/absProduct;    
        SimilarityRanking.put(CosineSimilarity, SemanticDimension.get(i));
        
        }
    //System.out.println("Words are" + Names);
    //System.out.println("Scores are" + Scores);
    Map<Double, String> TreeValues= SimilarityRanking.descendingMap();  
    //System.out.println("Mappings(Score, word) in this tree is" + TreeValues);
    Iterator<Map.Entry<Double, String>> SimilarityToQuery = TreeValues.entrySet().iterator();
    return SimilarityToQuery;
    }
    
    private static TreeMap<String, IntegerVectorMap> SemanticVectorsforAllUniqueWords(List<List<String>> sentences, List<String> SemanticVec){
    //A TreeMap containing words (keys) and their respective IntegerVectors which are TreeMaps <String, Integer> 
    TreeMap<String, IntegerVectorMap> VectorsForAllWords = new TreeMap<>();
    //initialize a map for every unique word
        for (int i = 0; i < SemanticVec.size(); i++)
            {//start a new map for each word named after itself 
             //For example, "Dog" is a map containing <word, coOccurance> for the word Dog with every other unique word
            IntegerVectorMap WordMap = new IntegerVectorMap(SemanticVec.get(i));
            VectorsForAllWords.put(SemanticVec.get(i), WordMap);
            }
    
    for (int i = 0; i<SemanticVec.size(); i++)
                        {                    
                        VectorsForAllWords = CreateSemanticVectorforOneWord(sentences,SemanticVec, SemanticVec.get(i), VectorsForAllWords);       
                        //DescriptorVectors.add(WordVector);
                        }
    return VectorsForAllWords;
    }
    
    //create semantic descriptor vector for each unique word.
    private static TreeMap<String, IntegerVectorMap> CreateSemanticVectorforOneWord(List<List<String>> sentences, List<String> SemanticVec, String word, TreeMap<String, IntegerVectorMap> VectorsForAllWords){
    //create a new IntegerVector(specified dimension)
    //IntegerVector WordVector = new IntegerVector(SemanticVec.size()); 
    //only need to check unique words occuring after this word in the list
    int index = SemanticVec.indexOf(word);
    for (int i = index; i < SemanticVec.size(); i ++) //check this word + every combination in the Semantic Vector
        {//only start checking CoOccurances if the map doesn't already have an entry with that key 
        //if (!VectorsForAllWords.get(word).getMap().containsKey(SemanticVec.get(i))) {
            int CoOccur = 0;
                             
            for (int j = 0; j < sentences.size(); j++)//check if they co-occur for every sentence
            {
                if (sentences.get(j).contains(word)){
                    List<String> ThisSentence = new ArrayList<>();
                    ThisSentence.addAll(sentences.get(j));
                    ThisSentence.remove(word);//remove the current word so we can detect co-occurance of the same word
                    if (ThisSentence.contains(SemanticVec.get(i)))
                        {CoOccur++; }
                }    
            }//have checked every sentence for co-occurance
            //This word co-occur "CoOccur" times with word i in the Semantic Vector
        
            //WordVector.addArgument(i, CoOccur);
            //add coOccurance for both words
            if (CoOccur != 0){    
                //update Map for target "word"
                VectorsForAllWords.get(word).addEntry(SemanticVec.get(i), CoOccur);
                //update map for the word being checked against 
                VectorsForAllWords.get(SemanticVec.get(i)).addEntry(word, CoOccur);}
            //} if does not already have entry
        }
    //return WordVector.getVector();
    return VectorsForAllWords;
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
        //System.out.println(sentence);
        sentence = sentence.replaceAll("\\r?\\n", " ");
        sentence = sentence.replace("--", "");
        sentence = sentence.replace(",", "");
        sentence = sentence.replace(":", "");
        sentence = sentence.replace(";", "");
        sentence = sentence.replace('“',' ');
        sentence = sentence.replace('”',' ');
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
