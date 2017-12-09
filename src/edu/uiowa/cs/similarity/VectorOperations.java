package edu.uiowa.cs.similarity;
import java.util.*;

public class VectorOperations implements VectorFunctions<TreeMap<String, Integer>, TreeMap<String, Double>>{
        @Override
        public Double DotMultiply(TreeMap<String, Integer> map1, TreeMap<String, Integer> map2)
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
        public Double absMultiply(TreeMap<String, Integer> map1, TreeMap<String, Integer> map2)
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
        
        @Override
        public Double negEuc(TreeMap<String, Integer> map1, TreeMap<String, Integer> map2) {
            double sum = 0;
            // Map 1
            Set<String> KeysMap1 = new TreeSet<>();
            KeysMap1 = map1.keySet();
            Iterator<String> KeysForMap1 = KeysMap1.iterator();
            List<String> KeyListMap1 = new ArrayList<>();
            while (KeysForMap1.hasNext()) {
                KeyListMap1.add(KeysForMap1.next());
            }
            
            for (int i = 0; i < KeyListMap1.size(); i++) {
                //check if map2 contains the word too with an associated co-Occurrance score
                if (map2.containsKey(KeyListMap1.get(i))) {
                    sum += java.lang.Math.pow(map1.get(KeyListMap1.get(i)) - map2.get(KeyListMap1.get(i)), 2);
                }
                else {
                    sum += java.lang.Math.pow(map1.get(KeyListMap1.get(i)), 2);
                }
            }
            
            // Map 2
            Set<String> KeysMap2 = new TreeSet<>();
            KeysMap2 = map2.keySet();
            Iterator<String> KeysForMap2 = KeysMap2.iterator();
            List<String> KeyListMap2 = new ArrayList<>();
            while (KeysForMap2.hasNext()) {
                KeyListMap2.add(KeysForMap2.next());
            }
            for (int j = 0; j < KeyListMap2.size(); j++) {
                if (!map1.containsKey(KeyListMap2.get(j))) {
                    sum += java.lang.Math.pow(map2.get(KeyListMap2.get(j)), 2);
                }
            }
            
            return java.lang.Math.sqrt(sum) * (-1);
        }
        
        @Override
        public Double eucNorm(TreeMap<String, Integer> map1, TreeMap<String, Integer> map2) {
            // Map 1 v1 distance
            Collection<Integer> Map1Values = map1.values();
            Iterator<Integer> IterMap1Values = Map1Values.iterator();
            double v1temp = 0;
            while (IterMap1Values.hasNext()) {
                v1temp += java.lang.Math.pow(IterMap1Values.next(), 2);
            }
            double v1dist = java.lang.Math.sqrt(v1temp);
            // Map 2 v2 distance
            Collection<Integer> Map2Values = map2.values();
            Iterator<Integer> IterMap2Values = Map2Values.iterator();
            double v2temp = 0;
            while (IterMap2Values.hasNext()) {
                v2temp += java.lang.Math.pow(IterMap2Values.next(), 2);
            }
            double v2dist = java.lang.Math.sqrt(v2temp);
            
            double sum = 0;
            // Map 1
            Set<String> KeysMap1 = new TreeSet<>();
            KeysMap1 = map1.keySet();
            Iterator<String> KeysForMap1 = KeysMap1.iterator();
            List<String> KeyListMap1 = new ArrayList<>();
            while (KeysForMap1.hasNext()) {
                KeyListMap1.add(KeysForMap1.next());
            }
            
            for (int i = 0; i < KeyListMap1.size(); i++) {
                //check if map2 contains the word too with an associated co-Occurrance score
                if (map2.containsKey(KeyListMap1.get(i))) {
                    sum += java.lang.Math.pow(map1.get(KeyListMap1.get(i))/v1dist - map2.get(KeyListMap1.get(i))/v2dist, 2);
                }
                else {
                    sum += java.lang.Math.pow(map1.get(KeyListMap1.get(i))/v1dist, 2);
                }
            }
            // Map 2
            Set<String> KeysMap2 = new TreeSet<>();
            KeysMap2 = map2.keySet();
            Iterator<String> KeysForMap2 = KeysMap2.iterator();
            List<String> KeyListMap2 = new ArrayList<>();
            while (KeysForMap2.hasNext()) {
                KeyListMap2.add(KeysForMap2.next());
            }
            for (int j = 0; j < KeyListMap2.size(); j++) {
                if (!map1.containsKey(KeyListMap2.get(j))) {
                    sum += java.lang.Math.pow(map2.get(KeyListMap2.get(j))/v2dist, 2);
                }                
            }
            return java.lang.Math.sqrt(sum) * (-1);
        }
    
        ///Eucluean for one integer map and one double map (for computing Semantic Vectors with means) 
        @Override
        public Double negEucD(TreeMap<String, Integer> map1, TreeMap<String, Double> map2) {
            double sum = 0;
            // Map 1
            Set<String> KeysMap1 = new TreeSet<>();
            KeysMap1 = map1.keySet();
            Iterator<String> KeysForMap1 = KeysMap1.iterator();
            List<String> KeyListMap1 = new ArrayList<>();
            while (KeysForMap1.hasNext()) {
                KeyListMap1.add(KeysForMap1.next());
            }
            
            for (int i = 0; i < KeyListMap1.size(); i++) {
                //check if map2 contains the word too with an associated co-Occurrance score
                if (map2.containsKey(KeyListMap1.get(i))) {
                    sum += java.lang.Math.pow(map1.get(KeyListMap1.get(i)) - map2.get(KeyListMap1.get(i)), 2);
                }
                else {
                    sum += java.lang.Math.pow(map1.get(KeyListMap1.get(i)), 2);
                }
            }
            
            // Map 2
            Set<String> KeysMap2 = new TreeSet<>();
            KeysMap2 = map2.keySet();
            Iterator<String> KeysForMap2 = KeysMap2.iterator();
            List<String> KeyListMap2 = new ArrayList<>();
            while (KeysForMap2.hasNext()) {
                KeyListMap2.add(KeysForMap2.next());
            }
            for (int j = 0; j < KeyListMap2.size(); j++) {
                if (!map1.containsKey(KeyListMap2.get(j))) {
                    sum += java.lang.Math.pow(map2.get(KeyListMap2.get(j)), 2);
                }
            }
            
            return java.lang.Math.sqrt(sum) * (-1);
        }





}

