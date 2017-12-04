package edu.uiowa.cs.similarity;
import java.util.List;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class VectorOperations implements VectorFunctions<HashMap<String, Integer>>{
        @Override
        public Double DotMultiply(HashMap<String, Integer> map1, HashMap<String, Integer> map2)
        {   
            int dotProduct = 0;
            Set<String> KeysMap1 = new TreeSet<>();
            KeysMap1 = map1.keySet();
            Iterator<String> KeysForMap1 = KeysMap1.iterator();
            List<String> KeyListMap1 = new ArrayList<>();
            while (KeysForMap1.hasNext()) {KeyListMap1.add(KeysForMap1.next());}
            //List<String> KeyListMap1 = Arrays.asList(KeysMap1.toArray());
            for (int i = 0; i < map1.size(); i++)
                //check if map2 contains the word too with an associated co-Occurrance score
                {if (map2.containsKey(KeyListMap1.get(i)) == true)
                    {dotProduct = dotProduct + map1.get(KeyListMap1.get(i))*map2.get(KeyListMap1.get(i));}
                }    
            return (double) dotProduct;
        }
        
        @Override
        public Double absMultiply(HashMap<String, Integer> map1, HashMap<String, Integer> map2)
        {
            int sumSquares1 = 0;
            int sumSquares2 = 0;
            //for map1
            Collection<Integer> Map1Values = map1.values();
            Iterator<Integer> IterMap1Values = Map1Values.iterator();
            while (IterMap1Values.hasNext())
                {int NextOne1 = IterMap1Values.next();sumSquares1 = sumSquares1 + (NextOne1*NextOne1);}
            //for map2
            Collection<Integer> Map2Values = map2.values();
            Iterator<Integer> IterMap2Values = Map2Values.iterator();
            while (IterMap2Values.hasNext())
                {int NextOne2 = IterMap2Values.next();sumSquares2 = sumSquares2 + (NextOne2*NextOne2);}
            double product = java.lang.Math.sqrt(sumSquares1*sumSquares2);
            return product;
        }
    }

