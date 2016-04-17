package com.neuralnet.learn;

public abstract class Training {

	private int epochs;
	private double error;
	private double mse; //mean square error
	
	public enum TrainingTypesENUM {
		PERCEPTRON, 
		ADALINE;
	}
	
	public enum ActivationFncENUM {
		STEP,
		LINEAR,
		SIGLOG,
		HYPERTAN
	}
	
	private double activationFnc(ActivationFncENUM fnc, double value){
		switch (fnc) {
		case STEP:
			return fncStep(value);
		case LINEAR:
			return fncLinear(value);
		case SIGLOG:
			return fncSigLog(value);
		case HYPERTAN:
			return fncHyperTan(value);
		default:
			throw new IllegalArgumentException(fnc
					+ " does not exist in ActivationFncENUM");
		}
	}
	
	private double fncStep(double v){
		if ( v >= 0 ){
			return 1.0;
		} else{
			return 0.0;
		}
	}
	
	private double fncLinear(double v){
		return v;
	}
	
	private double fncSigLog(double v){
		return 1.0 / ( 1.0 + Math.exp(-v));
	}
	
	private double fncHyperTan(double v){
		return Math.tanh(v);
	}
	
	private double derivativeFncLinear(double v) {
		return 1.0;
	}
	private double derivativeFncSigLog(double v) {
		return v * (1.0 - v);
	}
	private double derivativeFncHyperTan(double v) {
		return (1.0 / Math.pow(Math.cosh(v), 2.0));
	}

	public int getEpochs() {
		return epochs;
	}

	public void setEpochs(int epochs) {
		this.epochs = epochs;
	}

	public double getError() {
		return error;
	}

	public void setError(double error) {
		this.error = error;
	}

	public double getMse() {
		return mse;
	}

	public void setMse(double mse) {
		this.mse = mse;
	}
	
	
}
