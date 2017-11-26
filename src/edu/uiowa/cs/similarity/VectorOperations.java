package edu.uiowa.cs.similarity;
import java.util.List;


public class VectorOperations implements VectorFunctions<Integer, Double>{
        @Override
        public Double DotMultiply(List<Integer> x, List<Integer> y)
        {   
            int dotProduct = 0;
            for (int i = 0; i < x.size(); i++)
                {dotProduct = dotProduct + x.get(i)*y.get(i);}
            return (double) dotProduct;
        }
        
        @Override
        public Double absMultiply(List<Integer> x, List<Integer> y)
        {
            int sumSquaresx = 0;
            int sumSquaresy = 0;
            for (int i = 0; i < x.size(); i++)
                {sumSquaresx = sumSquaresx + (x.get(i)*x.get(i));
                 sumSquaresy = sumSquaresy + (y.get(i)*y.get(i));}
            double product = java.lang.Math.sqrt(sumSquaresx*sumSquaresy);
            return product;
        }
    }

