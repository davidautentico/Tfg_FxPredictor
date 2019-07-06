package drosa.apuestas;

import java.io.File;

import org.encog.ConsoleStatusReportable;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
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
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;
import org.encog.util.simple.EncogUtility;

public class NhlEncog {

	
	
	
	/**
	 * The input necessary for XOR.
	 */
	public static double XOR_INPUT[][] = { { 0.0, 0.0 }, { 1.0, 0.0 },
			{ 0.0, 1.0 }, { 1.0, 1.0 } };

	/**
	 * The ideal data necessary for XOR.
	 */
	public static double XOR_IDEAL[][] = { { 0.0 }, { 1.0 }, { 1.0 }, { 0.0 } };
	
	/**
	 * The main method.
	 * @param args No arguments are used.
	 */
	public static void main(final String args[]) {
		
		//String fileName = "C:\\nhl\\inputsNeuronsNHL.csv";
		String fileName = "C:\\nhl\\input2.csv";
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(null,true,6));
		network.addLayer(new BasicLayer(new ActivationSigmoid(),true,6));
		network.addLayer(new BasicLayer(new ActivationSigmoid(),false,1));
		network.getStructure().finalizeStructure();
		network.reset();
		
		ReadCSV csv = new ReadCSV(fileName, false, CSVFormat.DECIMAL_POINT);
		int total = 0;
		while(csv.next()) {			
			total++;
		}
		
		double[][] input = new double[total][6]; 
		double[][] output = new double[total][1]; 
		
		int i = 0;
		csv = new ReadCSV(fileName, false, CSVFormat.DECIMAL_POINT);
		while(csv.next()) {
			input[i][0] = csv.getDouble(0);
			input[i][1] = csv.getDouble(1);
			input[i][2] = csv.getDouble(2);
			input[i][3] = csv.getDouble(3);
			input[i][4] = csv.getDouble(4);
			input[i][5] = csv.getDouble(5);
			output[i][0] = csv.getDouble(6);
			
			i++;
		}
		
		System.out.println("size: "+i);
		
		
		
		// create training data
		MLDataSet trainingSet = new BasicMLDataSet(input,output);
		
		// train the neural network
		final ResilientPropagation train = new ResilientPropagation(network, trainingSet);

		int epoch = 1;

		do {
			train.iteration();
			System.out.println("Epoch #" + epoch + " Error:" + train.getError());
			epoch++;
		} while(train.getError() > 0.002);//como minimo 0.01 
		train.finishTraining();
		
		// test the neural network
		/*System.out.println("Neural Network Results:");
		for(MLDataPair pair: trainingSet ) {
			final MLData outputData = network.compute(pair.getInput());
			System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1)
					+ ", actual=" + outputData.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
		}*/
		
		Encog.getInstance().shutdown();
				
		/*ReadCSV csv = new ReadCSV(fileName, false, CSVFormat.DECIMAL_POINT);
		String[] line = new String[7];
		
		while(csv.next()) {
			System.out.println(csv.get(0)+".."+csv.get(6));
		}*/
		
				
		/*File f = new File(fileName);		
		VersatileDataSource source = new CSVDataSource(f, false,CSVFormat.DECIMAL_POINT);		
		VersatileMLDataSet data = new VersatileMLDataSet(source);
		data.defineSourceColumn("sepal-length", 0, ColumnType.continuous);
		data.defineSourceColumn("sepal-width", 1, ColumnType.continuous);
		data.defineSourceColumn("petal-length", 2, ColumnType.continuous);
		data.defineSourceColumn("petal-width", 3, ColumnType.continuous);
		data.defineSourceColumn("petal-length", 4, ColumnType.continuous);
		data.defineSourceColumn("petal-width", 5, ColumnType.continuous);
		// Define the column that we are trying to predict.
		ColumnDefinition outputColumn = data.defineSourceColumn("result", 6,
				ColumnType.continuous);		
		// Analyze the data, determine the min/max/mean/sd of every column.
		data.analyze();		
		// Map the prediction column to the output of the model, and all
		// other columns to the input.
		data.defineSingleOutputOthersInput(outputColumn);		
		// Create feedforward neural network as the model type. MLMethodFactory.TYPE_FEEDFORWARD.
		// You could also other model types, such as:
		// MLMethodFactory.SVM:  Support Vector Machine (SVM)
		// MLMethodFactory.TYPE_RBFNETWORK: RBF Neural Network
		// MLMethodFactor.TYPE_NEAT: NEAT Neural Network
		// MLMethodFactor.TYPE_PNN: Probabilistic Neural Network
		EncogModel model = new EncogModel(data);
		model.selectMethod(data, MLMethodFactory.TYPE_FEEDFORWARD);		
		// Send any output to the console.
		model.setReport(new ConsoleStatusReportable());		
		// Now normalize the data.  Encog will automatically determine the correct normalization
		// type based on the model you chose in the last step.
		data.normalize();		
		// Hold back some data for a final validation.
		// Shuffle the data into a random ordering.
		// Use a seed of 1001 so that we always use the same holdback and will get more consistent results.
		model.holdBackValidation(0.8, false, 1001);		
		// Choose whatever is the default training type for this model.
		model.selectTrainingType(data);		
		// Use a 5-fold cross-validated train.  Return the best method found.
		MLRegression bestMethod = (MLRegression)model.crossvalidate(5, true);
		// Display the training and validation errors.
		System.out.println( "Training error: " + EncogUtility.calculateRegressionError(bestMethod, model.getTrainingDataset()));
		System.out.println( "Validation error: " + EncogUtility.calculateRegressionError(bestMethod, model.getValidationDataset()));
		
	
		// Display our normalization parameters.
		NormalizationHelper helper = data.getNormHelper();
		System.out.println(helper.toString());
		
		// Display the final model.
		System.out.println("Final model: " + bestMethod);
		*/
		// create a neural network, without using a factory
		/*BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(null,true,2));
		network.addLayer(new BasicLayer(new ActivationSigmoid(),true,3));
		network.addLayer(new BasicLayer(new ActivationSigmoid(),false,1));
		network.getStructure().finalizeStructure();
		network.reset();

		// create training data
		MLDataSet trainingSet = new BasicMLDataSet(XOR_INPUT, XOR_IDEAL);
		
		// train the neural network
		final ResilientPropagation train = new ResilientPropagation(network, trainingSet);

		int epoch = 1;

		do {
			train.iteration();
			System.out.println("Epoch #" + epoch + " Error:" + train.getError());
			epoch++;
		} while(train.getError() > 0.002);//como minimo 0.01 
		train.finishTraining();

		// test the neural network
		System.out.println("Neural Network Results:");
		for(MLDataPair pair: trainingSet ) {
			final MLData output = network.compute(pair.getInput());
			System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1)
					+ ", actual=" + output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
		}
		
		Encog.getInstance().shutdown();*/
	}

}
