/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uiowa.cs.similarity;

import java.util.HashMap;
/**
 *
 * @author User
 */
public class IntegerVectorMap {

public HashMap<String, Integer> MapOfCoOccurances;  
public String name;

public IntegerVectorMap(String MapName){

MapOfCoOccurances = new HashMap<>();
this.name = MapName;
}    
 
public HashMap<String, Integer> addEntry(String word, Integer CoOccur){

MapOfCoOccurances.put(word, CoOccur);    

return MapOfCoOccurances;} 
    

public HashMap<String, Integer> getMap()
{
return this.MapOfCoOccurances;
}
}
