package com.mlpprocessor;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;
import weka.core.Utils;

public class MLPProcessor {

	public MLPProcessor(){
		try{
			// Train the model / classifier
			FileReader fr = new FileReader("vehicledata.arff");
			
			Instances training = new Instances(fr);
			
			training.setClassIndex(training.numAttributes() - 1);
			
			MultilayerPerceptron mlp = new MultilayerPerceptron();
			mlp.setOptions(Utils.splitOptions("-L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E"
					+ " 20 -H 4"));
			mlp.buildClassifier(training);
			
			// Evaluate the model
			FileReader tr = new FileReader("testdata.arff");
			Instances testData = new Instances(tr);
			testData.setClassIndex(testData.numAttributes() - 1);
			
			Evaluation eval = new Evaluation(training);
			eval.evaluateModel(mlp, testData);
			
			System.out.println(eval.toSummaryString("\nResults\n**********\n", false));
			tr.close();
			fr.close();
			
		}catch(FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) { 
			e.printStackTrace();
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]){
		MLPProcessor mlp = new MLPProcessor();
	}
	
}
