
This repo contains a number of studies evaluating machine learning algorithms and libraries.

# NeuralNetStudy 

TODO

# Bayesian Network to predict surgical outcome

BayesNet folder contains a Java project using JavaBayes library (https://github.com/jasebell/JavaBayesAPI) that predict surgery outcome given some (hypotetical) probabilities.

## Nodes used on the network:

- Age of patient (A)
- Does the patient smoke? (S)
- Duration of symptoms (D)
- Surgical outcome success (SS)

## Nodes Probabilities

```
AGE(A)
<55   >55
0.8   0.2
```

```
SMOKER(S)
Value of (A)  Smokes  Does Not Smoke
<55           0.4     0.6
>55           0.8     0.2
```

```
DURATION OF SYMPTOMS (DD
<2 Years      > 2 Years
0.9           0.1
```

```
SURGICAL OUTCOME SUCCESS (S)
(S)           (D)     Positive    Negative
Smoker        <2Y     0.1         0.9
Smoker        >2Y     0.01        0.99
Non Smoker    <2Y     0.8         0.2
Non Smoker    >2Y     0.58        0.42
```

## Testing the model

After applying observations the following are the overral results:

```
The probability of surgery being positive: 0.44823999999999997
The probability of surgery being positive and patient is younger than 55 : 0.5032
The probability of surgery being positive for a smoker, younger than 55: 0.09100000000000001
The probability of surgery being positive for a smoker, younger than 55 with symptoms over 2 years: 0.01
```

## Conclusions

Bayesian Network is a very powerfull tool and as you can see on sample code it is very straightforward to create a network, creates the nodes and connect them, and then assign probabilities and conditional probabilities using 3rd party libraries like JavaBayes. It is also very simple to evaluate different probability scenarios by applying existing observations.

It is also very important to do proper planning in paper and to have a domain expert to help with the initial values of the probabilities as it will make final prediction more accurate.

# Decision Tree

This folder contains a Java project using Weka API to implement a Decision tree model to predict CD Sales depending on CD's placement in the Store.

## Training Data

```
'@relation data_train

@attribute Placement {'  end_rack','  cd_spec','  std_rack'}
@attribute prominence numeric
@attribute ' pricing' numeric
@attribute ' eye_level' {FALSE,TRUE}
@attribute ' customer_purchase' {yes,no}

@data
'  end_rack',85,85,FALSE,yes
'  end_rack',80,90,TRUE,yes
'  cd_spec',83,86,FALSE,no
'  std_rack',70,96,FALSE,no
'  std_rack',68,80,FALSE,no
'  std_rack',65,70,TRUE,yes
'  cd_spec',64,65,TRUE,yes
'  end_rack',72,95,FALSE,yes
'  end_rack',69,70,FALSE,yes
'  std_rack',75,80,FALSE,no
'  end_rack',75,70,TRUE,no
'  cd_spec',72,90,TRUE,no
'  cd_spec',81,75,FALSE,yes
'  std_rack',71,91,TRUE,yes
'
```

## Attributes

There are five attributes in this set:

- Placement: What type of stand the CD is displayed on: an end rack, special offer bucket, or a standard rack?
- Prominence: What percentage of the CDs on display are Lady Gaga CDs?
- Pricing: What percentage of the full price was the CD at the time of pur- chase? Very rarely is a CD sold at full price, unless it is an old, back catalog title.
- Eye Level: Was the product displayed at eye level position? The majority of sales will happen when a product is displayed at eye level.
- Customer Purchase: What was the outcome? Did the customer purchase?

## Evaluation outcome

```
0.0 -> yes
0.0 -> yes
1.0 -> no
1.0 -> no
1.0 -> no
0.0 -> yes
0.0 -> yes
0.0 -> yes
0.0 -> yes
1.0 -> no
0.0 -> yes
1.0 -> no
0.0 -> yes
0.0 -> yes
```

## Conclusions

Although decision trees is perceived as simple solution, we cannot understimate its usages. Here we can see how easy to create a classifier based on C4.5 algorithm (https://en.wikipedia.org/wiki/C4.5_algorithm) to predict customer purchasing bahavior on products based on placement, pricing and prominence. They can also be used regardless of whether you have category or numerical data.

# Stock_Predictor

This folder contains a Java project implementing a Stock price predictor specifically for GOLD Future commodity. This project uses Encog library.

TODO

# XOR

This folder contains a Java project implementing a XOR operation using encog library. 

The project contains following two solutions:

### 1) Using Support Vector Machine as suppervised learning method.

### 2) Using a basic Neural Network with following structure:

  - input layer with 2 Nodes.
  - one hidden layer with 3 nodes.
  - output layer with 1 node.
  
It also uses the Resilient Propagation as lerning algorithm.
