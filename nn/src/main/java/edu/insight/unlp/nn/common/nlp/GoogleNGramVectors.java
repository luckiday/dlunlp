package edu.insight.unlp.nn.common.nlp;

import java.io.File;
import java.io.IOException;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

public class GoogleNGramVectors implements Word2Vector {

	private static String gModelPath = "/Users/kartik/Work/dhundo-dobara/Corpus/ML/Corpus/GoogleNews-vectors-negative300.bin.gz";
	private static Word2Vec word2Vec;

	static {
		try {
			System.err.print("Loading GoogleNews-vectors-negative300 Word2Vec model . . .");
			word2Vec = WordVectorSerializer.loadGoogleModel(new File(gModelPath), true);
			System.err.println("Done");
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	@Override
	public double[] getWordVector(String word) {
		return word2Vec.getWordVector(word);
	}

	//	public double getSim(String word1, String word2) {
	//		return word2Vec.similarity(word1, word2);
	//	}

	@Override
	public double getSim(String word1, String word2) {
		return word2Vec.similarity(word1, word2);	
		//		if(word1.equals(word2))
		//			return 1.0;
		//		INDArray array1 = Nd4j.create(getWordVector(word1));
		//		INDArray array2 = Nd4j.create(getWordVector(word2));
		//		INDArray vector1 = Transforms.unitVec(array1);
		//		INDArray vector2 = Transforms.unitVec(array2);
		//		if(vector1 == null || vector2 == null)
		//			return -1;
		//		return  Nd4j.getBlasWrapper().dot(vector1, vector2);
	}
	
	public static void main(String[] args) {
		
	}

}