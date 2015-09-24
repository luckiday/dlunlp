package edu.insight.unlp.nn.rnn;

import java.util.Collections;
import java.util.List;

import edu.insight.unlp.nn.ErrorFunction;
import edu.insight.unlp.nn.NNLayer;
import edu.insight.unlp.nn.RNN;
import edu.insight.unlp.nn.common.Sequence;

/*
 * RNNs
 */
public class RNNImpl implements RNN {

	public List<NNLayer> layers;
	public ErrorFunction ef;
	public double[][] networkOutput;

	public RNNImpl(ErrorFunction ef){
		this.ef = ef;
	}

	@Override
	public void update(double learningRate) {
		for(NNLayer layer : layers){
			int index = layers.indexOf(layer);
			if(index!=layers.size()-1){
				layers.get(index+1).update(learningRate);
			}
		}
	}

	//	public double sgdTrain(List<SequenceM21> training, double learningRate, boolean shuffle){
	//		double overallError = 0.0;
	//		if(shuffle){
	//			Collections.shuffle(training);
	//		}
	//		resetActivationCounter();
	//		for(SequenceM21 seq : training){
	//			double[][] inputSeq = seq.inputSeq;
	//			double[] target = seq.target;
	//			ff(inputSeq);
	//			double[][] eg = new double[networkOutput.length][];
	//			for(int i=0; i<networkOutput.length; i++){
	//				eg[i] = ef.error(target, networkOutput[i]);
	//			}
	//			double[] bp = bp(eg);
	//			double error = bp[bp.length-1];
	//			overallError = overallError + error;
	//			update(learningRate);
	//			resetActivationCounter();
	//		}
	//		return overallError / training.size();
	//	}

	public double sgdTrain(List<Sequence> training, double learningRate, boolean shuffle){
		double overallError = 0.0;
		if(shuffle){
			Collections.shuffle(training);
		}
		resetActivationCounter(true);
		for(Sequence seq : training){
			double[][] inputSeq = seq.inputSeq;
			double[][] target = seq.target;
			ff(inputSeq);
			double[][] eg = new double[networkOutput.length][];
			for(int i=0; i<networkOutput.length; i++){
				eg[i] = ef.error(target[i], networkOutput[i]);
			}
			double[] bp = bp(eg);
			double error = bp[bp.length-1];
			overallError = overallError + error;
			update(learningRate);
			resetActivationCounter(true);
		}
		return overallError / training.size();
	}


	//	public double sgdTrainSeqErrorAtLast(List<Sequence> training, double learningRate, int batchSize, boolean shuffle, double momentum){
	//		double overallError = 0.0;
	//		for(SequenceM seq : training){
	//			double[][] inputSeq = seq.inputSeq;
	//			double[] target = seq.target;
	//			double[] networkOutput = ff(inputSeq);
	//			double[] eg = ef.error(target, networkOutput);
	//			eg = bp(eg);
	//			double error = eg[networkOutput.length];
	//			overallError = overallError + error;
	//			update(learningRate, momentum);
	//			resetActivationCounter();
	//		}
	//		return overallError / training.size();
	//	}

	private double[] ff(double[][] inputSeq){
		double[] finalActivations = null;
		networkOutput = new double[inputSeq.length][];
		int i = 0;
		for(double[] input : inputSeq){
			double[] activations = null;
			activations = input;		
			for(NNLayer layer : layers){
				activations = layer.computeActivations(activations, true);
			}
			networkOutput[i++] = activations;
			finalActivations = activations;
		}
		return finalActivations;
	}

	private double[] bp(double[][] errorGradient){
		int o = errorGradient[0].length-1;
		double[] stageErrorGradient = null;
		for(int j=layers.get(layers.size()-1).getActivationCounterVal(); j>=0; j--){
			stageErrorGradient = errorGradient[j];
			for(int i = layers.size() - 1; i>0; i--){
				stageErrorGradient = layers.get(i).errorGradient(stageErrorGradient);
			}
			double totalError = stageErrorGradient[stageErrorGradient.length-1];
			if(j-1>-1){
				errorGradient[j-1][o] = totalError;
			}
		}
		return stageErrorGradient;
	}

	private double[] bp(double[] errorGradient){
		int o = errorGradient.length;
		for(int j=layers.get(layers.size()-1).getActivationCounterVal(); j>=0; j--){
			for(int i = layers.size() - 1; i>0; i--){
				errorGradient = layers.get(i).errorGradient(errorGradient);
			}
			double totalError = errorGradient[errorGradient.length-1];
			errorGradient = new double[o]; errorGradient[o-1] = totalError;
		}
		return errorGradient;
	}

	public void resetActivationCounter(boolean training){
		for(NNLayer layer : layers){
			layer.resetActivationCounter(training);
		}
	}

	@Override
	public int numOutputUnits() {
		return layers.get(layers.size()-1).numNeuronUnits();
	}

	@Override
	public void setLayers(List<edu.insight.unlp.nn.NNLayer> layers) {
		this.layers = layers;
	}

	@Override
	public void initializeNN() {
		int prevLayerUnits = -1;
		for(int i=0; i<layers.size(); i++){
			layers.get(i).initializeLayer(prevLayerUnits);
			prevLayerUnits = layers.get(i).numNeuronUnits();
		}
	}

	@Override
	public List<NNLayer> getLayers() {
		return layers;
	}

	//	public double[] output(double[][] inputSeq) {
	//		double[] result = null;
	//		for(double[] input : inputSeq){
	//			result = input;		
	//			for(NNLayer layer : layers){
	//				result = layer.computeActivations(result, false);
	//			}
	//		}
	//		return result;
	//	}

	@Override
	public double[][] output(double[][] inputSeq) {
		double[][] target = new double[inputSeq.length][];
		double[] result = null;
		int i = 0;
		for(double[] input : inputSeq){
			result = input;		
			for(NNLayer layer : layers){
				result = layer.computeActivations(result, false);
			}
			target[i++] = result;
		}
		return target;
	}

}
