# project-team-51

*****Semantic vectors and vector operations
The semantic vector is implemented by an IntegerVector class. 
"VectorFunctions" is an interface containing two multiplication methods. 
A VectorOperations class implements these two multiplication methods in VectorFunctions for multiplying integer vectors.
The output of the VectorOperations function is a Double. 

*****Ordering of similarity scores of unique words
The cosine similarity scores of all unique words are stored as a <key, value> pair in a TreeMap. 
Since Treemap provides natural ordering, the similarity scores (keys) would be ordered in this tree. 
However, the default comparator causes all words with duplicate keys to be replaced. 
For example, <0.0, cat>, <0.0, dog>, both having 0 similarity to query word, cannot be simultaneously stored in the tree. 
So, a comparator “ComparatorForDuplicates” with a new compare method is provided at time of tree generation to allow duplicate keys.
Therefore, multiple words with the same similarity score to the query word can all be stored. 
For returning “Top J similar words to Query Q”, the entries in the TreeMap is output in descending order and converted into an Iterator that iterates over entries<Double, String>. 
Depending on the number specified at the query, this iterator would output top J similar words based on cosine similarity score. 

