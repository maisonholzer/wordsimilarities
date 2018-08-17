/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uiowa.cs.similarity;
import java.util.Comparator;
import java.util.Random;
/**
 *
 * @author User
 */
public class ComparatorForDuplicatesRandomization implements Comparator<Double>{
    public int compare(Double a, Double b)
    {
    if (a>b) {return 1;}
    if (a==b) {
        Random Chance = new Random();
        int Dice = Chance.nextInt(100);
        if (Dice < 50) {return -1;}
        else {return 1;}
        }
    else {return -1;}
    }    
    
    
    
}
