package edu.uiowa.cs.similarity;

//This interface contains potential operations using vectors
public interface VectorFunctions<InT> {
        
        public Double DotMultiply(InT vector1, InT vector2);
        
        public Double absMultiply(InT vector1, InT vector2);

        public Double negEuc(InT vector1, InT vector2);
        
        public Double eucNorm(InT vector1, InT vector2);
}
