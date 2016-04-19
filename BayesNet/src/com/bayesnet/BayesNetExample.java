package com.bayesnet;

import javabayes.Helpers.BayesNetHelper;
import javabayes.InferenceGraphs.InferenceGraph;
import javabayes.InferenceGraphs.InferenceGraphNode;

public class BayesNetExample {

	public BayesNetExample(){
		InferenceGraph inferenceGraph = new InferenceGraph();
		
		/* Creating the Nodes */
		InferenceGraphNode age = BayesNetHelper.createNode(inferenceGraph, "under55", 
				"<55", ">55");
		
		InferenceGraphNode smoker = BayesNetHelper.createNode(inferenceGraph, "smoker", 
				"smokes", "doesnotsmoke");
		
		InferenceGraphNode duration = BayesNetHelper.createNode(inferenceGraph, "duration", 
				"<2Y", ">2Y");
		
		InferenceGraphNode surgical = BayesNetHelper.createNode(inferenceGraph, "surgicalOutcome", 
				"positive", "negative");
		
		/* Connecting the Nodes */
		inferenceGraph.create_arc(age, smoker);
		inferenceGraph.create_arc(smoker, surgical);
		inferenceGraph.create_arc(duration, surgical);
		
		/* Assigning Probabilities */
		/*
		 * if age < 55 then smoker(smokes) = 0.4, smoker(doesnotsmoke) = 0.6 
		 * if age > 55 then smoker(smokes) = 0.8, smoker(doesnotsmoke) = 0.2
		 * */
		BayesNetHelper.setProbabilityValues(smoker, "<55", 0.4, 0.6);
		BayesNetHelper.setProbabilityValues(smoker, ">55", 0.8, 0.2);
		
		/*
		 * if smokes and duration < 2Y then surgical(positive) = 0.1, surgical(negative) = 0.9 
		 * if smokes and duration > 2Y then surgical(positive) = 0.01, surgical(negative) = 0.99 
		 * if does not smoke and duration < 2Y then surgical(positive) = 0.8, surgical(negative) = 0.2 
		 * if does not smoke and duration > 2Y then surgical(positive) = 0.58, surgical(negative) = 0.42
		 * */
		BayesNetHelper.setProbabilityValues(surgical, "smokes", "<2Y", 0.1, 0.9);
		BayesNetHelper.setProbabilityValues(surgical, "smokes", ">2Y", 0.01, 0.99);
		BayesNetHelper.setProbabilityValues(surgical, "doesnotsmoke", "<2Y", 0.8, 0.2);
		BayesNetHelper.setProbabilityValues(surgical, "doesnotsmoke", ">2Y", 0.58, 0.42);
		
		BayesNetHelper.setProbabilityValues(duration, 0.9, 0.1);
		BayesNetHelper.setProbabilityValues(age, 0.8, 0.2);
		
		double belief = BayesNetHelper.getBelief(inferenceGraph, surgical);
		System.out.println("The probability of surgery being positive: " + belief);
		
		age.set_observation_value("<55");
		belief = BayesNetHelper.getBelief(inferenceGraph, surgical); 
		System.out.println("The probability of surgery being positive and patient "
				+ "is younger than 55 : " + belief);
		
		smoker.set_observation_value("smokes");
		belief = BayesNetHelper.getBelief(inferenceGraph, surgical);
		System.out.println("The probability of surgery being positive for a smoker"
				+ ", younger than 55: " + belief);
		
		duration.set_observation_value(">2Y");
		belief = BayesNetHelper.getBelief(inferenceGraph, surgical); 
		System.out.println("The probability of surgery being positive for a smoker, "
				+ "younger than 55 with symptoms over 2 years: " + belief);
	}
	
	public static void main(String[] args){
		BayesNetExample bne = new BayesNetExample();
		
		
		
		
	}
}
