
package edu.uiowa.cs.similarity;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


//This class instantiate an Integer Vector, which has a dimension and a vector implemented by a List 
public class IntegerVector{
    public VectorFunctions v;
    public List<Integer> vector;
    public Integer dimension;

//make an integer vector with dimension n reresented by a List of integers
    public IntegerVector(Integer dimension) {
        
        this.dimension = dimension;
        //make a vector of zeros with n dimension
        this.vector = new ArrayList<>(Collections.nCopies(dimension, 0));
    }
  
        public List<Integer> getVector(){
               return this.vector;
        }
    
        //@Override
        public void addArgument(Integer index, Integer y){
            this.vector.set(index, y);
        }     
}
    

