import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.encog.ConsoleStatusReportable;
import org.encog.Encog;
import org.encog.app.analyst.csv.sort.SortCSV;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.mathutil.error.ErrorCalculation;
import org.encog.mathutil.error.ErrorCalculationMode;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.market.MarketDataType;
import org.encog.ml.data.market.TickerSymbol;
import org.encog.ml.data.market.loader.LoadedMarketData;
import org.encog.ml.data.market.loader.YahooFinanceLoader;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.columns.ColumnDefinition;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.data.versatile.sources.CSVDataSource;
import org.encog.ml.data.versatile.sources.VersatileDataSource;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.model.EncogModel;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.util.arrayutil.VectorWindow;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

import Predictor.StockPredictor;

public class PredictorMain {

	public static final int WINDOW_SIZE = 5; // 3; //10;
	private static VersatileMLDataSet data;
	private static File fileName;
	private static double correctness = 0;
	private static MLRegression bestMethod;
	private static CSVFormat format;

	public static void main(final String[] parameters) throws ParseException {

		predict();
		//StockPredictor predictor = new StockPredictor();
		//predictor.predict(20);
		//Encog.getInstance().shutdown();
	}

	private static void predict() throws ParseException {
		// Define the format of the data file.
		// This area will change, depending on the columns and
		// format of the file that you are trying to model.
		format = new CSVFormat('.', ','); // decimal point and	space separated
				
		do{
			trainModel();
			evaluateModel();
		}while(correctness <= 70);
		

		// Delete data file and shut down.
		// fileName.delete();
		Encog.getInstance().shutdown();
	}

	public static void trainModel() throws ParseException {

		ErrorCalculation.setMode(ErrorCalculationMode.RMS);

		// Download the data that we will attempt to model.
		fileName = downloadData();

		VersatileDataSource source = new CSVDataSource(fileName, true, format);

		data = new VersatileMLDataSet(source);
		data.getNormHelper().setFormat(format);

		ColumnDefinition columnOpen = data.defineSourceColumn("Open",
				ColumnType.continuous);
		ColumnDefinition columnHigh = data.defineSourceColumn("High",
				ColumnType.continuous);
		ColumnDefinition columnLow = data.defineSourceColumn("Low",
				ColumnType.continuous);

		ColumnDefinition columnAdjClose = data.defineSourceColumn("Adj Close",
				ColumnType.continuous);

		// Analyze the data, determine the min/max/mean/sd of every column.
		data.analyze();

		// Use Open, High, Low, and Adj Close to predict Adj
		// Close. For time-series it is okay to have
		// Adj Close both as
		// an input and an output.
		data.defineInput(columnOpen);
		data.defineInput(columnHigh);
		data.defineInput(columnLow);
		data.defineInput(columnAdjClose);
		data.defineOutput(columnAdjClose);

		// Create feedforward neural network as the model type.
		// MLMethodFactory.TYPE_FEEDFORWARD.
		// You could also other model types, such as:
		// MLMethodFactory.SVM: Support Vector Machine (SVM)
		// MLMethodFactory.TYPE_RBFNETWORK: RBF Neural Network
		// MLMethodFactor.TYPE_NEAT: NEAT Neural Network
		// MLMethodFactor.TYPE_PNN: Probabilistic Neural Network
		EncogModel model = new EncogModel(data);
		model.selectMethod(data, MLMethodFactory.TYPE_FEEDFORWARD);

		// Send any output to the console.
		model.setReport(new ConsoleStatusReportable());

		// Now normalize the data. Encog will automatically determine the
		// correct normalization
		// type based on the model you chose in the last step.
		data.normalize();

		// Set time series.
		data.setLeadWindowSize(1);
		data.setLagWindowSize(WINDOW_SIZE);
		//data.setLagWindowSize(10);

		// Hold back some data for a final validation.
		// Do not shuffle the data into a random ordering. (never shuffle
		// time series)
		// Use a seed of 1001 so that we always use the same holdback and
		// will get more consistent results.
		model.holdBackValidation(0.3, false, 1001);

		// Choose whatever is the default training type for this model.
		model.selectTrainingType(data);

		// Use a 5-fold cross-validation train. Return the best method found.
		// (never shuffle time series)
		bestMethod = (MLRegression) model.crossvalidate(5, false);

		// Display the training and validation errors.
		System.out.println("Training error: "
				+ model.calculateError(bestMethod, model.getTrainingDataset()));
		System.out
				.println("Validation error: "
						+ model.calculateError(bestMethod,
								model.getValidationDataset()));

		// Display our normalization parameters.
		NormalizationHelper helper = data.getNormHelper();
		System.out.println(helper.toString());

		// Display the final model.
		System.out.println("Final model: " + bestMethod);

	}
	
	public static void evaluateModel() {

		NormalizationHelper helper = data.getNormHelper();

		// Loop over the entire, original, dataset and feed it through the
		// model. This also shows how you would process new data, that was
		// not part of your training set. You do not need to retrain, simply
		// use the NormalizationHelper class. After you train, you can save
		// the NormalizationHelper to later normalize and denormalize your
		// data.
		try {

			String[] line = new String[4];

			// Create a vector to hold each time-slice, as we build them.
			// These will be grouped together into windows.
			double[] slice = new double[4];
			VectorWindow window = new VectorWindow(WINDOW_SIZE + 1);
			MLData input = helper.allocateInputVector(900); // 9 18

			// Only display the first 100
			int stopAfter = 200;

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",
					Locale.ENGLISH);
			Date from = dateFormat.parse("2015-12-15");
			Date to = dateFormat.parse("2016-01-30");
			correctness = 0;

			double totalValidationSamples = 0;
			double correctPredictions = 0;
			ReadCSV csv = new ReadCSV(fileName, true, format);

			while (csv.next()) {

				String date = csv.get(0); // date
				Date sampleDate = dateFormat.parse(date);

				if (!(sampleDate.getTime() >= from.getTime() && sampleDate
						.getTime() <= to.getTime())) {
					continue;
				}

				StringBuilder result = new StringBuilder();

				line[0] = csv.get(1); // open
				// line[1] = csv.get(6);
				line[1] = csv.get(2); // High
				line[2] = csv.get(3); // Low
				// line[3] = csv.get(4); //Close
				// line[4] = csv.get(5); //Volume
				line[3] = csv.get(6); // AdjClose*/
				//line[1] = csv.get(6); // AdjClose*/

				helper.normalizeInputVector(line, slice, false);

				if (window.isReady()) {
					window.copyWindow(input.getData(), 0);
					String correct = csv.get(6); // trying to predict SSN.
					double correctN = Double.parseDouble(correct);
					double openN = Double.parseDouble(csv.get(1));
					MLData output = bestMethod.compute(input);
					String predicted = helper
							.denormalizeOutputVectorToString(output)[0];
					double predictedN = Double.parseDouble(predicted);

					result.append(date + " - ");
					result.append(Arrays.toString(line));
					result.append(" -> predicted: ");
					result.append(predicted);
					result.append("(correct: ");
					result.append(correct);
					result.append(")");
					result.append(" (Result: ");

					boolean prediction = false;
					if (predictCorrectDirection(openN, correctN, predictedN)) {
						prediction = true;
						correctPredictions++;
					}

					result.append(prediction ? 1 : 0);
					result.append(")");
					System.out.println(result.toString());
					totalValidationSamples++;
				}

				// Add the normalized slice to the window. We do this just after
				// the after checking to see if the window is ready so that the
				// window is always one behind the current row. This is because
				// we are trying to predict next row.
				window.add(slice);

				// stopAfter--;
			}
			correctness = (correctPredictions / totalValidationSamples) * 100;
			System.out
					.printf("Correctness : %f \n, Correct Predictions : %f, Total Validation Samples : %f",
							correctness, correctPredictions,
							totalValidationSamples);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	
	public static boolean predictCorrectDirection(double open, double correct,
			double predicted) {
		return (((open - predicted) > 0 && (open - correct) > 0))
				|| ((open - predicted) < 0 && (open - correct) < 0) ? true
				: false;
	}

	public static File downloadData() throws ParseException {

		String outputFile = "dataOutput.csv";
		String outputFile2 = "dataCME_GC1.csv";

		/*
		 * YahooFinanceLoader loader = new YahooFinanceLoader(); DateFormat
		 * format = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH); Date
		 * from = format.parse("2015-01-01"); Date to =
		 * format.parse("2015-12-17");
		 * 
		 * final Collection<LoadedMarketData> result = loader.load(new
		 * TickerSymbol("dow", null), null, from, to);
		 * System.out.println(result.size());
		 * 
		 * Collections.sort((List<LoadedMarketData>) result);
		 * writeToCsvFile(result, "dataOutput.csv");
		 */

		File fileName = new File(outputFile2);

		return fileName;
	}

	public BasicNetwork createNetwork() {
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(null, true, 2));
		network.addLayer(new BasicLayer(new ActivationTANH(), true, 3));
		network.addLayer(new BasicLayer(new ActivationTANH(), false, 1));
		network.getStructure().finalizeStructure();
		network.reset();
		return network;
	}

	public static void writeToCsvFile(
			final Collection<LoadedMarketData> dataList, String fileName) {
		try {
			FileWriter writer = new FileWriter(fileName);

			writer.append("Date");
			writer.append(',');
			writer.append("Open");
			writer.append(',');
			writer.append("High");
			writer.append(',');
			writer.append("Low");
			writer.append(',');
			writer.append("Close");
			writer.append(',');
			writer.append("Volume");
			writer.append(',');
			writer.append("Adj Close");
			writer.append('\n');

			Format formatter = new SimpleDateFormat("yyyy-MM-dd");

			for (LoadedMarketData data : dataList) {
				writer.append(formatter.format(data.getWhen()));
				writer.append(',');
				writer.append(String.valueOf(data.getData(MarketDataType.OPEN)));
				writer.append(',');
				writer.append(String.valueOf(data.getData(MarketDataType.HIGH)));
				writer.append(',');
				writer.append(String.valueOf(data.getData(MarketDataType.LOW)));
				writer.append(',');
				writer.append(String.valueOf(data.getData(MarketDataType.CLOSE)));
				writer.append(',');
				writer.append(String.valueOf(data
						.getData(MarketDataType.VOLUME)));
				writer.append(',');
				writer.append(String.valueOf(data
						.getData(MarketDataType.ADJUSTED_CLOSE)));
				writer.append('\n');
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
