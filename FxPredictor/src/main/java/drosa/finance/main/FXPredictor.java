package drosa.finance.main;

import java.io.File;
import java.io.IOException;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

public class FXPredictor {
	
	
	/**
	 * Leemos los datos y los convertimos a la clase fuinanciera
	 * @param fileName
	 */
	private static void readData(String fileName) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Aquí creamos el modelo a entrenar y evaluar posteriormente
	 * @return 
	 */
	private static MultiLayerNetwork buildModel() {
		// TODO Auto-generated method stub
		MultiLayerNetwork model = null;
		
		return model;
	}
	
	private static void doTrain(MultiLayerNetwork model,DataSetIterator trainIter, int nEpochs) {
		// TODO Auto-generated method stub
		
	}

	private static void doEvaluate(MultiLayerNetwork model,DataSetIterator dataSet) {
		// TODO Auto-generated method stub
		Evaluation eval = model.evaluate(dataSet);
        System.out.println(eval.stats());
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		//lectura de datos financieros
		//habra que partir los datos en varios años para probar
		String fileNameTrainRaw = "";
		String fileNameTestRaw = "";
		String fileNameTrainPro = "";
		String fileNameTestPro = "";
		
		readData(fileNameTrainRaw);
		readData(fileNameTestRaw);
		
		//ajuste de datos
		
		//preprocesamiento calculando indicadores del dataset
		
		//guardamos los datos preprocesados
		//en fileNameTrainPro y fileNameTestPro
		
		//obtenemos los dataset de los datos preprocesados
		int batchSize = 50;
		
        RecordReader rr = new CSVRecordReader();
        rr.initialize(new FileSplit(new File(fileNameTrainPro)));
        DataSetIterator trainIter = new RecordReaderDataSetIterator(rr,batchSize,0,2);

        //Load the test/evaluation data:
        RecordReader rrTest = new CSVRecordReader();
        rrTest.initialize(new FileSplit(new File(fileNameTestPro)));
        DataSetIterator testIter = new RecordReaderDataSetIterator(rrTest,batchSize,0,2);
		
		//elegimos algoritmo		
		MultiLayerNetwork model = buildModel();		
		//aplicamos algoritmo al train
		int nEpochs = 30;
		doTrain(model,trainIter,nEpochs);		
		//aplicamos algoritmo al test
		doEvaluate(model,testIter);
		
		System.out.println("Programa finalizado");
	}

	


}
