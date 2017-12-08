package edu.uiowa.cs.similarity;

import org.apache.commons.cli.*;
import java.util.Scanner;
import opennlp.tools.stemmer.*;
import java.io.*;
import java.util.*;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Random;
import java.util.TreeSet;

public class Main {

    public static void main(String[] args) throws IOException {
        final long startTime = System.currentTimeMillis();
        Options options = new Options();
        options.addRequiredOption("f", "file", true, "input file to process");
        options.addOption("h", false, "print this help message");
        options.addOption("s", false, "print final array of words");
        options.addOption("v", false, "print unique words along with their semantic vectors");
        options.addOption("t", "query", true, "query word for similarity");
        options.addOption("m", true, "use choice of similarity measure (cosine, euclidean distance, or normalized distance");
        options.addOption("k", true, "compute k means");
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
                            Iterator<Map.Entry<Double, String>> DescendingSimilarityRanking = null;
                            // Add -m options here
                            if (cmd.hasOption("m")) {
                                String input = cmd.getOptionValue("m");
                                switch (input) {
                                    case "euc":
                                        //Create a map containing (euc similarity, unique word) pair stored in natural ordering
                                        DescendingSimilarityRanking = MapOfSimilarityScoresNegEuc(SemanticVectorDimension, DescriptorVectorsforAllUniqueWords, QueryWord, m);
                                        break;
                                    case "eucnorm":
                                        //Create a map containing (negeuc similarity, unique word) pair stored in natural ordering
                                        DescendingSimilarityRanking = MapOfSimilarityScoresEucNorm(SemanticVectorDimension, DescriptorVectorsforAllUniqueWords, QueryWord, m);
                                        break;
                                    case "cosine":
                                        //Create a map containing (cosine similarity, unique word) pair stored in natural ordering
                                        DescendingSimilarityRanking = MapOfSimilarityScoresCosine(SemanticVectorDimension, DescriptorVectorsforAllUniqueWords, QueryWord, m);
                                        break;
                                    default:
                                        System.out.println("Similarity method could not be found. Defaulted to Cosine.");
                                        //Create a map containing (cosine similarity, unique word) pair stored in natural ordering
                                        DescendingSimilarityRanking = MapOfSimilarityScoresCosine(SemanticVectorDimension, DescriptorVectorsforAllUniqueWords, QueryWord, m);
                                        break;
                                }
                            }
                            else {
                                //Create a map containing (cosine similarity, unique word) pair stored in natural ordering                       
                                DescendingSimilarityRanking = MapOfSimilarityScoresCosine(SemanticVectorDimension, DescriptorVectorsforAllUniqueWords, QueryWord, m);
                                //DescendingSimilarityRanking = MapOfSimilarityScoresEucNorm(SemanticVectorDimension, DescriptorVectorsforAllUniqueWords, QueryWord, m);
                            }
                            //print only the number of similar words required by command line input, and exclude query word itself (which has similarity score 1.0)
                            int count = 0;
                            while (DescendingSimilarityRanking.hasNext() && count < NumberToDisplay)
                                {   Map.Entry<Double, String> NextPrint = DescendingSimilarityRanking.next();
                                    if (!NextPrint.getValue().equals(QueryWord)) {System.out.println(NextPrint);count++;}
                                }
                        }
                    }
                    
                    //add option k, iter to compute k means 
                    if (cmd.hasOption("k")) {
                    String KMeansInput = cmd.getOptionValue("k");
                    List<String> ProcessedInput = ProcessingQueryInput(KMeansInput);
                    int k = Integer.parseInt(ProcessedInput.get(0));
                    int iterations = Integer.parseInt(ProcessedInput.get(1));
                    List<IntegerVectorMap> InitialPoints = getKInitialPoints(SemanticVectorDimension, DescriptorVectorsforAllUniqueWords, k);
                    List<TreeMap> ClustersOfWords = kMeansClustering(InitialPoints, SemanticVectorDimension, DescriptorVectorsforAllUniqueWords, k, iterations);
                    Iterator<IntegerVectorMap> cluster1 = ClustersOfWords.get(0).values().iterator();
                    while (cluster1.hasNext())
                        {System.out.println("Cluster 1 - " + cluster1.next().getName());}
                    Iterator<IntegerVectorMap> cluster2 = ClustersOfWords.get(1).values().iterator();
                    while (cluster2.hasNext())
                        {System.out.println("Cluster 2 - " + cluster2.next().getName());}
                    Iterator<IntegerVectorMap> cluster3 = ClustersOfWords.get(2).values().iterator();
                    while (cluster3.hasNext())
                        {System.out.println("Cluster 3 - " + cluster3.next().getName());}
                    //System.out.println("Cluster 2" + ClustersOfWords.get(1).values());
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
    //new methods
    public static List<IntegerVectorMap> getKInitialPoints(List<String> SemanticVec, TreeMap<String, IntegerVectorMap> VectorsAllWords, Integer k){
    //get a set of Keys for the 
    int size = SemanticVec.size();
    TreeSet<Integer> Indices = new TreeSet<>();
    Random RandomInitial = new Random();
    //put k random numbers in set 
    while (Indices.size() < k){
        int Random = RandomInitial.nextInt(size);
        Indices.add(Random);
    }
    System.out.println("A set of random number is: " + Indices);
    //get the IntegerVectorMaps associated with these indices
    List<IntegerVectorMap> InitialPoints= new ArrayList<>();
    int Index = 0;
    while (!Indices.isEmpty()){
        Index = Indices.pollFirst();
        String key = SemanticVec.get(Index);
        InitialPoints.add(VectorsAllWords.get(key));
        }
    //can delete this
    List<String> InitialNames = new ArrayList<>();
    for (int l = 0; l < k; l++)
        {InitialNames.add(InitialPoints.get(l).getName());}
    
    System.out.println("initial points are   " + InitialNames);
    return InitialPoints;
    }
    
    //public static TreeMap<> CopyTreeMap()
    
    public static List<TreeMap> kMeansClustering(List<IntegerVectorMap> InitialPoints, List<String> UniqueWords, TreeMap<String, IntegerVectorMap> VectorsAllWords, Integer k, Integer iterations){
    VectorOperations m = new VectorOperations();
    //Words in each cluster is stored in a treemap
    
    //each mean is a DoubleVectorMap
    List<DoubleVectorMap> ListofMeans = new ArrayList<>();
    
    List<TreeMap> ListofTreeMapClusters = new ArrayList<>();
    
    
    
    //make List of initial points into List of treeMaps with String, Double as Key, Value    
    //create a list of means storing TreeMaps<String, Double> for each mean 
    for (int o = 0; o < k; o++)
        {
        DoubleVectorMap n = new DoubleVectorMap(Integer.toString(o));    
        Set<String> Keys = new TreeSet<>();
        Keys = InitialPoints.get(o).getMap().keySet();
        Iterator<String> KeysIterator = Keys.iterator();
        List<String> KeyList = new ArrayList<>();
        
        while (KeysIterator.hasNext()) {KeyList.add(KeysIterator.next());}
            //for every key, add this key (key, value (double))
            for (int h = 0; h < KeyList.size(); h ++)
                {
                n.addEntry(KeyList.get(h), (double) InitialPoints.get(o).getMap().get(KeyList.get(h)));    
                }
        ListofMeans.add(n);
        }//Finished creating a List of Means as TreeMaps<String, Double> 
    
    System.out.println("Initial point " + InitialPoints.get(0).getMap().descendingMap());
    System.out.println("Initial point " + ListofMeans.get(0).getMap().descendingMap());
    System.out.println("Initial point " + InitialPoints.get(1).getMap().descendingMap());
    System.out.println("Initial point " + ListofMeans.get(1).getMap().descendingMap());
    // update for i iterations
    for (int i = 0; i<iterations; i++)
        {
        //initialize k new TreeMaps for storing key (distance) and value (String: word)        
        ListofTreeMapClusters.clear();
        //Create a treeMap for each cluster, storing <distance, word> 
        for (int r = 0; r < k; r ++)
        {   
            TreeMap<Double, String> cluster = new TreeMap<>(new ComparatorForDuplicates());
            ListofTreeMapClusters.add(cluster);
        }      
            
        //calculate distances to all means for each unique word    
        for (int j = 0; j < VectorsAllWords.size(); j ++){
            //System.out.println("+++++++++++++++++++++++++++++++++");
            TreeMap<Double, DoubleVectorMap> TreeofDistances = new TreeMap<>();
            for (int h = 0; h < k; h ++)//compute distances to all means, there is k means 
                { 
                double distanceToMean = m.negEucD(VectorsAllWords.get(UniqueWords.get(j)).getMap(), ListofMeans.get(h).getMap());//InitialPoints.get(h).getMap()
                //each word has a TreeofDistances, containing <Distance, Mean<DoubleVectorMap>> to store distance to each mean
                //System.out.println("computing distance of word " + UniqueWords.get(j) + " to Mean " + ListofMeans.get(h).getName() + " distance is " + distanceToMean); 
                TreeofDistances.put(distanceToMean, ListofMeans.get(h));
                
                }
            //return the <distance, mean point> pair with the smallest distance.
            Map.Entry<Double, DoubleVectorMap> ClosestMean = TreeofDistances.lastEntry();
            String ClusterWordBelongsTo = ClosestMean.getValue().getName();
            //System.out.println("this word belongs to mean " + ClusterWordBelongsTo);
            //put Distance, IntegerWordMap into the right cluster Word belongs to 
            ListofTreeMapClusters.get(Integer.parseInt(ClusterWordBelongsTo)).put(ClosestMean.getKey(), VectorsAllWords.get(UniqueWords.get(j))); 
            //System.out.println("for this word put in TreeMap key " + ClosestMean.getKey() + "Value" + VectorsAllWords.get(UniqueWords.get(j)));
            }//end of for loop for all words, now there are k TreeMaps with disntances, IntegerVectorMaps for words
        //after adding all the words, call update Mean
        //List of Means is a List of <String, Double>
        ListofMeans = updateMeans(ListofMeans, ListofTreeMapClusters);
        List<List<Map.Entry<String, Double>>> ChangingMeans = storeChangingMeans(ListofMeans); 
        
        System.out.println("Means updated for iteration " + i + ChangingMeans);
        //ListofTreeMapClusters.clear();
        }//end of iterations for loop 
    
    return ListofTreeMapClusters;
    }

    public static List<List<Map.Entry<String, Double>>> storeChangingMeans(List<DoubleVectorMap> ListofMeans){
    List<List<Map.Entry<String, Double>>> MeansforAllClusters = new ArrayList<>();
    for (int i = 0; i < ListofMeans.size(); i++)
        {
        List<Map.Entry<String, Double>> MeansforOneCluster = new ArrayList<>();
        Map<String, Double> MeansforThisCluster = ListofMeans.get(i).getMap().descendingMap();
        Iterator<Map.Entry<String, Double>> PrintMeans = MeansforThisCluster.entrySet().iterator();
            //add the means DoubleVectorMap mappings for one cluster
            while (PrintMeans.hasNext() == true){
            MeansforOneCluster.add(PrintMeans.next());}
        MeansforAllClusters.add(MeansforOneCluster); 
            
        
        }
    return MeansforAllClusters; 
    }
    
    
    public static List<DoubleVectorMap> updateMeans(List<DoubleVectorMap> ListofMeans, List<TreeMap> ListofTreeMapClusters){
    //System.out.println("Update means is called");
    //update every mean
    for (int i = 0; i < ListofMeans.size(); i++){
        //for every mean, get a set to store words that need to be computed and updated
        TreeSet<String> NeedsUpdate = new TreeSet<>();
        //iterate over each word<IntegerVectorMap> in each cluster
        Iterator<IntegerVectorMap> AllWordsMaps = ListofTreeMapClusters.get(i).values().iterator();
        while (AllWordsMaps.hasNext() == true){
            IntegerVectorMap currentWordVec = AllWordsMaps.next();
            Set<String> DimensionsOneWord = currentWordVec.getMap().keySet();
            //System.out.println("Update Mean cluster" + i + DimensionsOneWord);
            NeedsUpdate.addAll(DimensionsOneWord);
            //Iterator<String> DimensionsOneWord = currentWordVec.getMap().values().iterator();//a collection of all dimensions associated with one word
            
            }//now the Needs Update set contains all dimensions for that cluster
        //now, average values for all vectors for each word in that cluster
        Iterator<String> WordsUpdate = NeedsUpdate.iterator();
        //System.out.println("needs update set" + NeedsUpdate);
        while (WordsUpdate.hasNext() == true){
            String key = WordsUpdate.next();
            //System.out.println("For cluster" + i + "dimensions used in updatemMean are " + key);
            //for each IntegerVectorMap in that cluster, get value for key
            //iterate over all IntegerVectorMaps in one cluster
            Iterator<IntegerVectorMap> WordsMapsInCluster = ListofTreeMapClusters.get(i).values().iterator();
            int sum = 0;
            while (WordsMapsInCluster.hasNext() == true){
                //System.out.println("Integer Vec iterator has next");
                IntegerVectorMap currentIntegerMap = WordsMapsInCluster.next();
                //System.out.println("current interger word map in cluster is " + currentIntegerMap.getName());
                //need to check if Map even contains an entry for the key word 
                if (currentIntegerMap.getMap().containsKey(key) == true){
                    sum = sum + currentIntegerMap.getMap().get(key);}
                //System.out.println("sum is now" + sum);
                }//now sum is the sum of all values for one word, Dog, in the cluster
            //System.out.println("done for key word " + key + "for cluster" + i + " and sum is" + sum);
            
            double averageScoreThisWord = ((double) sum)/((double) ListofTreeMapClusters.get(i).size());
            //System.out.println("Averaged score for word" + averageScoreThisWord);
            //System.out.println("+++++++++++Mean before update for word" + key + " is " + ListofMeans.get(i).getMap().get(key));
            ListofMeans.get(i).addEntry(key, averageScoreThisWord);//now, updated one dimension for the mean vector
            //System.out.println("+++++++++++Mean after update for word" + key + " is " + ListofMeans.get(i).getMap().get(key));
        }
            
    }
    
    return ListofMeans;
    }





    //end new methods
    
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
    
    public static Iterator<Map.Entry<Double, String>> MapOfSimilarityScoresCosine(List<String> SemanticDimension, TreeMap<String, IntegerVectorMap> SemanticVecs, String queryword, VectorOperations m){
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

    public static Iterator<Map.Entry<Double, String>> MapOfSimilarityScoresNegEuc(List<String> SemanticDimension, TreeMap<String, IntegerVectorMap> SemanticVecs, String queryword, VectorOperations m){
    //int index = SemanticDimension.indexOf(queryword);
    IntegerVectorMap QueryWordVector = SemanticVecs.get(queryword);    
    TreeMap<Double, String> SimilarityRanking = new TreeMap<>(new ComparatorForDuplicates());  
    //compare query word with every word 
    for (int i = 0; i < SemanticDimension.size(); i++)
        {
        double negEucSimilarity = m.negEuc(QueryWordVector.getMap(), SemanticVecs.get(SemanticDimension.get(i)).getMap()); 
        SimilarityRanking.put(negEucSimilarity, SemanticDimension.get(i));
        }
    //System.out.println("Words are" + Names);
    //System.out.println("Scores are" + Scores);
    Map<Double, String> TreeValues= SimilarityRanking.descendingMap();  
    //System.out.println("Mappings(Score, word) in this tree is" + TreeValues);
    Iterator<Map.Entry<Double, String>> SimilarityToQuery = TreeValues.entrySet().iterator();
    return SimilarityToQuery;
    }
    
    public static Iterator<Map.Entry<Double, String>> MapOfSimilarityScoresEucNorm(List<String> SemanticDimension, TreeMap<String, IntegerVectorMap> SemanticVecs, String queryword, VectorOperations m){
    //int index = SemanticDimension.indexOf(queryword);
    IntegerVectorMap QueryWordVector = SemanticVecs.get(queryword);    
    TreeMap<Double, String> SimilarityRanking = new TreeMap<>(new ComparatorForDuplicates());  
    //compare query word with every word 
    for (int i = 0; i < SemanticDimension.size(); i++)
        {
        double EucNormSimilarity = m.eucNorm(QueryWordVector.getMap(), SemanticVecs.get(SemanticDimension.get(i)).getMap()); 
        SimilarityRanking.put(EucNormSimilarity, SemanticDimension.get(i));
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
