/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uiowa.cs.similarity;
import java.util.Comparator;

//This comparator allows duplicate keys to be stored in the TreeMap 
public class ComparatorForDuplicates implements Comparator<Double>{
    
public int compare(Double a, Double b)
    {
    if (a>b) {return 1;}
    if (a==b) {return 1;}
    else {return -1;}
    }    
    
}
