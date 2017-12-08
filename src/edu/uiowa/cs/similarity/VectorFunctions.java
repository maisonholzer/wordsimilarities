package edu.uiowa.cs.similarity;

//This interface contains potential operations using vectors
public interface VectorFunctions<InT, InT2> {
        
        public Double DotMultiply(InT vector1, InT vector2);
        
        public Double absMultiply(InT vector1, InT vector2);

        public Double negEuc(InT vector1, InT vector2);
        
        public Double negEucD(InT vector1, InT2 vector2);
        
        
        public Double eucNorm(InT vector1, InT vector2);
}
