package edu.uiowa.cs.similarity;

import java.util.List;

//This interface contains potential operations using vectors
public interface VectorFunctions<InT, OutT> {
//        public List<OutT> CreateVector(Integer n);
    
        
        public OutT DotMultiply(List<InT> x, List<InT> y);
        
        public OutT absMultiply(List<InT> x, List<InT> y);


}
