/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uiowa.cs.similarity;

import java.util.TreeMap;
/**
 *
 * @author User
 */
public class IntegerVectorMap {

public TreeMap<String, Integer> MapOfCoOccurances;  
public String name;

public IntegerVectorMap(String MapName){

MapOfCoOccurances = new TreeMap<>();
this.name = MapName;
}    
 
public TreeMap<String, Integer> addEntry(String word, Integer CoOccur){

MapOfCoOccurances.put(word, CoOccur);    

return MapOfCoOccurances;} 
    

public TreeMap<String, Integer> getMap()
{
return this.MapOfCoOccurances;
}

public String getName(){
return this.name;}
}
