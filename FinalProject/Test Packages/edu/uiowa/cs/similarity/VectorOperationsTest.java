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
        
        // build map1
        TreeMap<String, Integer> map1 = new TreeMap<>();
        map1.put("a", 1);
        map1.put("b", 4);
        map1.put("c", 1);
        
        // build map2
        TreeMap<String, Integer> map2 = new TreeMap<>();
        map2.put("a", 3);
        //map2.put("b", 0);
        //map2.put("c", 0);
        map2.put("d", 1);
        map2.put("e", 1);
        map2.put("f", 2);
        
        VectorOperations m = new VectorOperations();
        double result = m.negEuc(map1, map2);
        
        assertEquals(-5.1961524227, result, 0.00001);
    }
    
    // Test empty trees
    @Test
    public void testNegEucEmpty() {
        TreeMap<String, Integer> map1 = new TreeMap<>();
        TreeMap<String, Integer> map2 = new TreeMap<>();
        
        VectorOperations m = new VectorOperations();
        double result = m.negEuc(map1, map2);
        assertEquals(0, result, 0.00001);
    }
    /**
     * Test of eucNorm method, of class VectorOperations.
     */
    @Test
    public void testEucNorm() {
        
        // build map1
        TreeMap<String, Integer> map1 = new TreeMap<>();
        map1.put("a", 1);
        map1.put("b", 4);
        map1.put("c", 1);
        
        // build map2
        TreeMap<String, Integer> map2 = new TreeMap<>();
        map2.put("a", 3);
        
        map2.put("d", 1);
        map2.put("e", 1);
        map2.put("f", 2);
        
        VectorOperations m = new VectorOperations();
        double result = m.eucNorm(map1, map2);
        
        assertEquals(-1.27861316602, result, 0.00001);
    }
    

    // Test empty trees

    
// Test empty trees

    @Test
    public void testEucNormEmpty() {
        TreeMap<String, Integer> map1 = new TreeMap<>();
        TreeMap<String, Integer> map2 = new TreeMap<>();
        
        VectorOperations m = new VectorOperations();
        double result = m.eucNorm(map1, map2);
        assertEquals(0, result, 0.00001);
    }
    
}
