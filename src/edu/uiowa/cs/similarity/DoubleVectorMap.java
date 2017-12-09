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
public class DoubleVectorMap {
public TreeMap<String, Double> MapOfCoOccurances;  
public String name;

public DoubleVectorMap(String MapName){

MapOfCoOccurances = new TreeMap<>();
this.name = MapName;
}    
 
public TreeMap<String, Double> addEntry(String word, Double mean){

MapOfCoOccurances.put(word, mean);    

return MapOfCoOccurances;} 
    

public TreeMap<String, Double> getMap()
{
return this.MapOfCoOccurances;
}    

public String getName(){
return this.name;}

}
