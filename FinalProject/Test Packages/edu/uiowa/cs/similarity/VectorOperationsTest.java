/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uiowa.cs.similarity;

import java.util.TreeMap;

import org.junit.Test;
import static org.junit.Assert.*;

public class VectorOperationsTest {
    
    public VectorOperationsTest() {
    }

    /**
     * Test of negEuc method, of class VectorOperations.
     */
    @Test
    public void testNegEuc() {
        System.out.println("negEuc");
        // build map1
        TreeMap<String, Integer> map1 = null;
        map1.put("blank", 1);
        map1.put("blank", 4);
        map1.put("blank", 1);
        map1.put("blank", 0);
        map1.put("blank", 0);
        map1.put("blank", 0);
        
        // build map2
        TreeMap<String, Integer> map2 = null;
        map2.put("blank", 3);
        map2.put("blank", 0);
        map2.put("blank", 0);
        map2.put("blank", 1);
        map2.put("blank", 1);
        map2.put("blank", 2);
        
        double result = negEuc(map1, map2);
        
        assertEquals(-5.1961524227, result, 0.00001);
    }

    /**
     * Test of eucNorm method, of class VectorOperations.
     */
    @Test
    public void testEucNorm() {
        System.out.println("eucNorm");
        // build map1
        TreeMap<String, Integer> map1 = null;
        map1.put("blank", 1);
        map1.put("blank", 4);
        map1.put("blank", 1);
        map1.put("blank", 0);
        map1.put("blank", 0);
        map1.put("blank", 0);
        
        // build map2
        TreeMap<String, Integer> map2 = null;
        map2.put("blank", 3);
        map2.put("blank", 0);
        map2.put("blank", 0);
        map2.put("blank", 1);
        map2.put("blank", 1);
        map2.put("blank", 2);
        
        double result = eucNorm(map1, map2);
        
        assertEquals(-1.27861316602, result, 0.00001);
    }
    
}
