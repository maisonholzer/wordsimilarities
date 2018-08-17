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
        options.addOption("k", true, "compute k means clustering for iterations");
        options.addOption("j", true, "compute k means clustering for iterations and return only top J words");
        options.addOption("i", false, "interactive part");
        CommandLineParser parser = new DefaultParser();

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            new HelpFormatter().printHelp("Main", options, true);
            System.exit(1);
        }
        String mode = "cosine"; 
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
                    
                    //extra credit interactive i
                    if (cmd.hasOption("i"))
                    {
                        Scanner Interactive = new Scanner(System.in);
                        System.out.println("Vectors have been created");
                        System.out.println("Type a command!");
	
                        String typed = Interactive.next(); 
                        
                        
                        List<String> ProcessedCommand = ProcessingCommandInput(typed);
                      while (!typed.equals("quit")){
                        if (typed.toLowerCase().equals("topj"))//(ProcessedCommand.get(0).toLowerCase().equals("topj"))
                              
                            {
                                String QueryWord = Interactive.next();
                            int NumberToDisplay = Integer.parseInt(Interactive.next());
                            
                            if (!SemanticVectorDimension.contains(QueryWord))
                                {System.err.println("Cannot compute top " + NumberToDisplay + " similarity to " + QueryWord);}
                            else{
                            //start vector operations to compare similarity of QueryWord with all unique words in text
                            //Create a VectorOperations function called m which can do vector multiplications
                            VectorOperations m = new VectorOperations();
                            Iterator<Map.Entry<Double, String>> DescendingSimilarityRanking = null;
                            //Create a map containing (cosine similarity, unique word) pair stored in natural ordering                       
                            DescendingSimilarityRanking = MapOfSimilarityScoresCosine(SemanticVectorDimension, DescriptorVectorsforAllUniqueWords, QueryWord, m);
                                
                            //print only TopJ similar words, and exclude query word itself (which has similarity score 1.0)
                            int count = 0;
                            while (DescendingSimilarityRanking.hasNext() && count < NumberToDisplay)
                                {   Map.Entry<Double, String> NextPrint = DescendingSimilarityRanking.next();
                                    if (!NextPrint.getValue().equals(QueryWord)) {System.out.println(NextPrint);count++;}
                                }
                            }
                            System.out.println("Type a command!");
                            typed = Interactive.next();
                            }//end of topJ part
                        if (typed.toLowerCase().equals("kmeans"))
                            {
                             
                            int k = Integer.parseInt(Interactive.next());
                            int iterations = Integer.parseInt(Interactive.next());
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
                            
                            System.out.println("Type a command!");
                            typed = Interactive.next();
                            }//end of k means part
                        
                        if (typed.toLowerCase().equals("setfunction"))
                            {
                            mode = Interactive.next().toLowerCase();
                            System.out.println("similarity mode is now: " + mode);
                            System.out.println("Type a command!");
                            typed = Interactive.next();
                            }
                        
                        
                        if (typed.toLowerCase().equals("similarity"))
                            {
                            String word1 = Interactive.next();
                            String word2 = Interactive.next();
                            VectorOperations m = new VectorOperations();
                            
                            if (mode.equals("cosine"))
                                
                               {double dot = m.DotMultiply(DescriptorVectorsforAllUniqueWords.get(word1).getMap(), DescriptorVectorsforAllUniqueWords.get(word2).getMap());
                                double abs = m.absMultiply(DescriptorVectorsforAllUniqueWords.get(word1).getMap(), DescriptorVectorsforAllUniqueWords.get(word2).getMap());
                                double CosineSimilarity = dot/abs;         
                                System.out.println("score is " + CosineSimilarity);}
                            
                            if (mode.equals("euc"))
                                {
                                double score = m.negEuc(DescriptorVectorsforAllUniqueWords.get(word1).getMap(), DescriptorVectorsforAllUniqueWords.get(word2).getMap());
                                System.out.println("score is " + score);
                                }
                            if (mode.equals("eucnorm"))   
                                {
                                double score = m.eucNorm(DescriptorVectorsforAllUniqueWords.get(word1).getMap(), DescriptorVectorsforAllUniqueWords.get(word2).getMap());
                                System.out.println("score is " + score);    
                                }
                            System.out.println("Type a command!");
                            typed = Interactive.next();
                            }//end of similarity part
                      }//end pf while
                }
                    
                    
                    
                    
                    //end of extra credit interactive portion
                    
                    // If "s" is added to the argument, print the array.
                    if (cmd.hasOption("s")) { // move this back down somehow once finished
                        System.out.println(retVal.toString());
                        System.out.println("Number of Sentences: " + sentenceCount);}
                        
                    if (cmd.hasOption("v")){
                        System.out.println("Unique words and their descriptor vectors");
                        for (int i = 0; i < SemanticVectorDimension.size(); i++)
                            //{System.out.println(SemanticVectorDimension.get(i) + " has semantic vector "+ DescriptorVectorsforAllUniqueWords.get(SemanticVectorDimension.get(i)).getMap().descendingMap());}}
                             {System.out.println(SemanticVectorDimension.get(i) + " has semantic vector "+ DescriptorVectorsforAllUniqueWords.get(SemanticVectorDimension.get(i)).getMap().entrySet());}}
                    
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
                                
                            }
                            //print only TopJ similar words, and exclude query word itself (which has similarity score 1.0)
                            int count = 0;
                            while (DescendingSimilarityRanking.hasNext() && count < NumberToDisplay)
                                {   Map.Entry<Double, String> NextPrint = DescendingSimilarityRanking.next();
                                    if (!NextPrint.getValue().equals(QueryWord)) {System.out.println(NextPrint);count++;}
                                }
                        }
                    }
                    
                    //compute k means for iterations 
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
                    {

                    
                    }
                    

                    if (cmd.hasOption("j")){
                    String KMeansTopJInput = cmd.getOptionValue("j");
                    List<String> ProcessedInputJ = ProcessingQueryInput(KMeansTopJInput);
                    int kTopJ = Integer.parseInt(ProcessedInputJ.get(0));
                    int iterationsTopJ = Integer.parseInt(ProcessedInputJ.get(1));
                    int TopJ = Integer.parseInt(ProcessedInputJ.get(2));
                    List<IntegerVectorMap> InitialPointsTopJ = getKInitialPoints(SemanticVectorDimension, DescriptorVectorsforAllUniqueWords, kTopJ);
                    List<TreeMap> ClustersOfWordsTopJ = kMeansClustering(InitialPointsTopJ, SemanticVectorDimension, DescriptorVectorsforAllUniqueWords, kTopJ, iterationsTopJ);
                    
                    //for each cluster, return an iterator of IntegerWordMaps of closest - to -furthest distance 
                    for (int i = 0; i < kTopJ; i ++)
                        {
                        Iterator <Map.Entry<Double, IntegerVectorMap>> WordsCloseToMean = iteratorOfWordsCloseToMean(ClustersOfWordsTopJ.get(i)); 
                        int count= 0;
                        while (WordsCloseToMean.hasNext() && count < TopJ){
                            Map.Entry<Double, IntegerVectorMap> Next = WordsCloseToMean.next();count++; 
                            System.out.println("Next closest word for cluster " + i + " is :" + Next.getValue().getName());
                            }
                        }
                    }//end of if has option j
                    
                     
                file.close();   final long endTime = System.currentTimeMillis();
                
                }
        
        if (cmd.hasOption("h")) {
            HelpFormatter helpf = new HelpFormatter();
            helpf.printHelp("Main", options, true);
            System.exit(0);
        }
    }
    //new methods
    public static Iterator<Map.Entry<Double, IntegerVectorMap>> iteratorOfWordsCloseToMean(TreeMap TreeMapofOneWordCluster){
    Map<Double, IntegerVectorMap> TreeValues = TreeMapofOneWordCluster.descendingMap();    
    Iterator<Map.Entry<Double, IntegerVectorMap>> ClosestWords = TreeValues.entrySet().iterator();
    return ClosestWords;    
    }
    
    
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
    //System.out.println("A set of random number is: " + Indices);
    //get the IntegerVectorMaps associated with these indices
    List<IntegerVectorMap> InitialPoints= new ArrayList<>();
    int Index = 0;
    while (!Indices.isEmpty()){
        Index = Indices.pollFirst();
        String key = SemanticVec.get(Index);
        InitialPoints.add(VectorsAllWords.get(key));
        }
    /*
    List<String> InitialNames = new ArrayList<>();
    for (int l = 0; l < k; l++)
        {InitialNames.add(InitialPoints.get(l).getName());}
    
    System.out.println("initial points are   " + InitialNames);
    */
    return InitialPoints;
    }
    
    
    public static List<TreeMap> kMeansClustering(List<IntegerVectorMap> InitialPoints, List<String> UniqueWords, TreeMap<String, IntegerVectorMap> VectorsAllWords, Integer k, Integer iterations){
    //System.out.println("++++++++++++Distance to means++++++++++++++++++++");
    VectorOperations m = new VectorOperations();
    
    //each mean is a DoubleVectorMap storing <String(word), Double>
    List<DoubleVectorMap> ListofMeans = new ArrayList<>();
    //Words in each cluster is stored in a treemap
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
    
    // update for i iterations
    for (int i = 0; i<iterations; i++)
        {
        //initialize k new TreeMaps for storing key (distance) and value (String: word) for words in a cluster       
        ListofTreeMapClusters.clear();
        //Create a treeMap for each cluster, storing <distance, word> 
        for (int r = 0; r < k; r ++)
        {   
            TreeMap<Double, IntegerVectorMap> cluster = new TreeMap<>(new ComparatorForDuplicates());
            ListofTreeMapClusters.add(cluster);
        }      
            
        //calculate distances to all means for each unique word    
        for (int j = 0; j < VectorsAllWords.size(); j ++){
            
            TreeMap<Double, DoubleVectorMap> TreeofDistances = new TreeMap<>(new ComparatorForDuplicatesRandomization());
            for (int h = 0; h < k; h ++)//compute distances to all means, there is k means 
                { 
                double distanceToMean = m.negEucD(VectorsAllWords.get(UniqueWords.get(j)).getMap(), ListofMeans.get(h).getMap());//InitialPoints.get(h).getMap()
                //each word has a TreeofDistances, containing <Distance, Mean<DoubleVectorMap>> to store distance to each mean
                TreeofDistances.put(distanceToMean, ListofMeans.get(h));
                
                }
            //return the <distance, mean point> pair with the smallest distance.
            Map.Entry<Double, DoubleVectorMap> ClosestMean = TreeofDistances.lastEntry();
            String ClusterWordBelongsTo = ClosestMean.getValue().getName();
            
            //put Distance, IntegerWordMap into the right cluster Word belongs to 
            ListofTreeMapClusters.get(Integer.parseInt(ClusterWordBelongsTo)).put(ClosestMean.getKey(), VectorsAllWords.get(UniqueWords.get(j))); 
            
            }//end of for loop for all words, now there are k TreeMaps with disntances, IntegerVectorMaps for words
        
        List<Double> AveragedDistancesWordsToMean = computeEuclideanDistanceEachCluster(ListofMeans, ListofTreeMapClusters);
        ListofMeans = updateMeans(ListofMeans, ListofTreeMapClusters);
        List<List<Map.Entry<String, Double>>> ChangingMeans = storeChangingMeans(ListofMeans); 
        
        } 
    //System.out.println("++++++++++++++++++++++++End of distance to means");
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
    
    public static List<Double> computeEuclideanDistanceEachCluster(List<DoubleVectorMap> ListofMeans, List<TreeMap> ListTreeMaps) {
    List<Double> Distances = new ArrayList<>();    
    VectorOperations m = new VectorOperations();     
    //for every cluster mean    
    for (int i = 0; i < ListofMeans.size(); i++)
        {//for every word in the cluster compute euclidean distance with mean 
        Map<Double, IntegerVectorMap> WordsInCluster = ListTreeMaps.get(i).descendingMap();
        Iterator<Map.Entry<Double, IntegerVectorMap>> EachWordInCluster = WordsInCluster.entrySet().iterator();
        double sum = 0;
            while (EachWordInCluster.hasNext())
            {   Map.Entry<Double, IntegerVectorMap> ThisWord = EachWordInCluster.next();
                double currentDistance = m.negEucD(ThisWord.getValue().getMap(), ListofMeans.get(i).getMap());
                sum = sum + currentDistance; 
            }
        double averageDistance = sum/((double) ListTreeMaps.get(i).size());
        Distances.add(averageDistance);
        
        }
    //System.out.println(Distances);
    return Distances;    
    }
    
    public static List<DoubleVectorMap> updateMeans(List<DoubleVectorMap> ListofMeans, List<TreeMap> ListofTreeMapClusters){
    //System.out.println("Update means is called");
    //update every semantic dimension of every mean
    for (int i = 0; i < ListofMeans.size(); i++){
        //for every mean, get a set to store words that need to be computed and updated
        TreeSet<String> NeedsUpdate = new TreeSet<>();
        //iterate over each word<IntegerVectorMap> in each cluster
        Iterator<IntegerVectorMap> AllWordsMaps = ListofTreeMapClusters.get(i).values().iterator();
        
        while (AllWordsMaps.hasNext() == true){
            IntegerVectorMap currentWordVec = AllWordsMaps.next();
            Set<String> DimensionsOneWord = currentWordVec.getMap().keySet();
            NeedsUpdate.addAll(DimensionsOneWord);
            }//now the Needs Update set contains all semantic dimensions for that cluster
        //now, average values for all vectors for each word in that cluster
        Iterator<String> WordsUpdate = NeedsUpdate.iterator();
        
        while (WordsUpdate.hasNext() == true){
            String key = WordsUpdate.next();
            
            //for each IntegerVectorMap in that cluster, get value for key
            //iterate over all IntegerVectorMaps in one cluster
            Iterator<IntegerVectorMap> WordsMapsInCluster = ListofTreeMapClusters.get(i).values().iterator();
            
            int sum = 0;
            while (WordsMapsInCluster.hasNext() == true){
                
                IntegerVectorMap currentIntegerMap = WordsMapsInCluster.next();
                
                //need to check if Map even contains an entry for the key word 
                if (currentIntegerMap.getMap().containsKey(key) == true){
                    sum = sum + currentIntegerMap.getMap().get(key);}
                
                }//now sum is the sum of all values for one word, Dog, in the cluster
            
            double averageScoreThisWord = ((double) sum)/((double) ListofTreeMapClusters.get(i).size());
            
            ListofMeans.get(i).addEntry(key, averageScoreThisWord);//now, updated one dimension for the mean vector
        }       
    }
    return ListofMeans;
    }

    public static List<String> ProcessingCommandInput(String queryInput){
        
        String[] QueryInput = queryInput.split(" ");
        List<String> QueryInputList = Arrays.asList(QueryInput);
    return QueryInputList;
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
    
    Map<Double, String> TreeValues= SimilarityRanking.descendingMap();  
    Iterator<Map.Entry<Double, String>> SimilarityToQuery = TreeValues.entrySet().iterator();
    return SimilarityToQuery;
    }

    public static Iterator<Map.Entry<Double, String>> MapOfSimilarityScoresNegEuc(List<String> SemanticDimension, TreeMap<String, IntegerVectorMap> SemanticVecs, String queryword, VectorOperations m){
    
    IntegerVectorMap QueryWordVector = SemanticVecs.get(queryword);    
    TreeMap<Double, String> SimilarityRanking = new TreeMap<>(new ComparatorForDuplicates());  
    //compare query word with every word 
    for (int i = 0; i < SemanticDimension.size(); i++)
        {
        double negEucSimilarity = m.negEuc(QueryWordVector.getMap(), SemanticVecs.get(SemanticDimension.get(i)).getMap()); 
        SimilarityRanking.put(negEucSimilarity, SemanticDimension.get(i));
        }
    
    Map<Double, String> TreeValues= SimilarityRanking.descendingMap();  
    Iterator<Map.Entry<Double, String>> SimilarityToQuery = TreeValues.entrySet().iterator();
    return SimilarityToQuery;
    }
    
    public static Iterator<Map.Entry<Double, String>> MapOfSimilarityScoresEucNorm(List<String> SemanticDimension, TreeMap<String, IntegerVectorMap> SemanticVecs, String queryword, VectorOperations m){
    
    IntegerVectorMap QueryWordVector = SemanticVecs.get(queryword);    
    TreeMap<Double, String> SimilarityRanking = new TreeMap<>(new ComparatorForDuplicates());  
    //compare query word with every word 
    for (int i = 0; i < SemanticDimension.size(); i++)
        {
        double EucNormSimilarity = m.eucNorm(QueryWordVector.getMap(), SemanticVecs.get(SemanticDimension.get(i)).getMap()); 
        SimilarityRanking.put(EucNormSimilarity, SemanticDimension.get(i));
        }
    
    Map<Double, String> TreeValues= SimilarityRanking.descendingMap();  
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
                        }
    return VectorsForAllWords;
    }
    
    //create semantic descriptor vector for each unique word.
    private static TreeMap<String, IntegerVectorMap> CreateSemanticVectorforOneWord(List<List<String>> sentences, List<String> SemanticVec, String word, TreeMap<String, IntegerVectorMap> VectorsForAllWords){
    //only need to check unique words occuring after this word in the list
    int index = SemanticVec.indexOf(word);
    for (int i = index; i < SemanticVec.size(); i ++) //check this word + every combination in the Semantic Vector
        {//only start checking CoOccurances if the map doesn't already have an entry with that key 
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
        
            if (CoOccur != 0){    
                //update Map for target "word"
                VectorsForAllWords.get(word).addEntry(SemanticVec.get(i), CoOccur);
                //update map for the word being checked against 
                VectorsForAllWords.get(SemanticVec.get(i)).addEntry(word, CoOccur);}
           
        }
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
