package Predictor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.temporal.TemporalDataDescription;
import org.encog.ml.data.temporal.TemporalMLDataSet;
import org.encog.ml.data.temporal.TemporalPoint;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.neural.pattern.FeedForwardPattern;
import org.encog.util.arrayutil.NormalizeArray;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;
import org.encog.util.simple.EncogUtility;

public class StockPredictor {
	
	private double NormalizeHigh = 1.0;
    private double NormalizeLow = -1.0;
    private double MaxError = 0.00026;
    private NormalizeArray norm;
    private TemporalMLDataSet trainingSet;
    private BasicNetwork network;
    public int PastWindowSize = 50;
    public int FutureWindowSize = 1;
    
	static List<StockData> data = new ArrayList<StockData>();
	
	public void predict(int hiddenLayers) throws ParseException{
		
		// read data
		readData();
		
		// normalize Data
		normalizeData();
		
		// generate temporal data
		generateTemporalData();
		
		// create & train network
		createAndTrainNetwork(hiddenLayers);
		
		// evaluate network
		evaluateNetwork();
	}
	
	private void readData(){
		ReadCSV csvReader = new ReadCSV("dataCME_GC1.csv", true, CSVFormat.ENGLISH);
		
		int count = 0;
		
		while(csvReader.next()){
			count++;
			
			StockData stock = new StockData();
			stock.setId(count);
			stock.setAdjClose(csvReader.getDouble("Adj Close"));
			stock.setDateIndex(csvReader.getDate("Date"));
			
			data.add(stock);
		}
	}
	
	private void normalizeData(){
		norm = new NormalizeArray();
		norm.setNormalizedHigh(NormalizeHigh);
		norm.setNormalizedLow(NormalizeLow);
		
        double[] normalizedArray = norm.process(getActualValues());
        for (int i = 0; i < normalizedArray.length; i++)
        {
            data.get(i).setNormalizedActual(normalizedArray[i]);
        }
	}
	
	private void generateTemporalData(){
		
		// temporal dataset
		trainingSet = new TemporalMLDataSet(PastWindowSize, FutureWindowSize);
		
		// description
		TemporalDataDescription desc = new TemporalDataDescription(
				TemporalDataDescription.Type.RAW, true, true);
		trainingSet.addDescription(desc);
		
		for(int i = 0; i < data.size(); i++){
			
			StockData stockData = data.get(i);
			TemporalPoint point = new TemporalPoint(1);
			point.setSequence(stockData.getId());
			point.setData(0, stockData.getNormalizedActual());
			trainingSet.getPoints().add(point);
		}
		trainingSet.generate();
	}
	
	private void createAndTrainNetwork(int hiddenLayers){
		System.out.println("createAndTrainNetwork - start ");
		FeedForwardPattern pattern = new FeedForwardPattern();
		pattern.setActivationFunction(new ActivationTANH());
		pattern.setInputNeurons(PastWindowSize);
		pattern.setOutputNeurons(FutureWindowSize);
		pattern.addHiddenLayer(hiddenLayers);
		network = (BasicNetwork)pattern.generate();
		
		ResilientPropagation train = new ResilientPropagation(network, trainingSet);
		EncogUtility.trainToError(train, MaxError);
		System.out.println("createAndTrainNetwork - end ");
	}
	
	private void evaluateNetwork() throws ParseException{
		System.out.println("evaluateNetwork - start ");
		int evaluateStart = minId() + PastWindowSize;
		int evaluateStop = maxId();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",
				Locale.ENGLISH);
		Date from = dateFormat.parse("2015-12-15");
		
		for(int currentId = evaluateStart; currentId <= evaluateStop; currentId++){
			BasicMLData input = new BasicMLData(PastWindowSize);
			StockData currentStockData = getNormalizedData(currentId);
			Date stockDate = currentStockData.getDateIndex();
			
			if (!(stockDate.getTime() >= from.getTime())) {
				continue;
			}
			
			for(int i = 0; i < PastWindowSize; i++){
				StockData stockData = getNormalizedData((currentId - PastWindowSize) + 1);
				input.setData(0, stockData.getNormalizedActual());
			}
			MLData output = network.compute(input);
			double normalizedPredicted = output.getData(0);
			double normalizedActual = currentStockData.getNormalizedActual();
			
			double actual = norm.getStats().deNormalize(normalizedActual);
			double predicted = norm.getStats().deNormalize(normalizedPredicted);
			StringBuilder result = new StringBuilder();
			result.append(stockDate + " - ");
			result.append(" predicted: " + predicted);
			result.append(" actual: " + actual);
			System.out.println(result.toString());
		}
		System.out.println("evaluateNetwork - end ");
	}
	
	private StockData getNormalizedData(int id){
		boolean found = false;
		StockData value = null;
		for(int i = 0; i < data.size() && !found; i++){
			StockData stockData = data.get(i);
			if(stockData.getId() == id){
				found = true;
				value = stockData;
			}
		}
		return value;
	}
	
	private int minId(){
		return data.get(0).getId();
	}
	
	private int maxId(){
		return data.get(data.size() - 1).getId();
	}
	
	private double[] getActualValues(){
		
		double[] actualData = new double[data.size()];
		for(int i = 0; i < data.size(); i++){
			actualData[i] = data.get(i).getAdjClose();
		}
		return actualData;
	}
}
