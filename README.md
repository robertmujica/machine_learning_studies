
This repo contains a number of studies evaluating machine learning algorithms and libraries.

# NeuralNetStudy 

TODO

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

# Stock_Predictor

This folder contains a Java project implementing a Stock price predictor specifically for GOLD Future commodity. This project uses Encog library.

TODO

# XOR

This folder contains a Java project implementing a XOR operation using encog library. 

The project contains following two solutions:

## Using Support Vector Machine as suppervised learning method.

## Using a basic Neural Network with following structure:

  - input layer with 2 Nodes.
  - one hidden layer with 3 nodes.
  - output layer with 1 node.
  
It also uses the Resilient Propagation as lerning algorithm.
