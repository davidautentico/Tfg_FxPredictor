package drosa.dl4j.examples;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import drosa.finance.classes.DAO;
import drosa.finance.classes.QuoteShort;
import drosa.finance.features.TradingFeatures;
import drosa.finance.types.DataProvider;
import drosa.finance.utils.DateUtils;
import drosa.finance.utils.TradingUtils;



/**
 * Predecir si es mas probable alcanzar +10 o -10//clases=1 y 0
 * @author DAVID
 *
 */
public class TestCsv {

	public static void main(String[] args) throws IOException, InterruptedException {
		String path0 ="C:\\fxdata\\";
		String path = path0+"EURUSD_5 Mins_Bid_2009.01.01_2019.06.24.csv";
		//1) leemos los datos
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		dataI 		= new ArrayList<QuoteShort>();			
		dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);	
		DateUtils.calculateCalendarAdjustedSinside(dataI);			
		dataS = TradingUtils.cleanWeekendDataS(dataI);  
		ArrayList<QuoteShort> data = dataS;
		
		System.out.println("data size: "+data.size());
		
		//2) ***parte de calculo de features***
		//se evaluaria siempre en el OPEN
		//2.0 maxmins,usamos valores n-1, para evitar dataAhead bias
		ArrayList<Integer> maxMins		= TradingFeatures.calculateMaxMinByBarShortAbsoluteInt(data);		
		//2.1 hour of the day
		ArrayList<Integer> hours 		= TradingFeatures.getHours(data);
		//2.2 atr actual
		ArrayList<Integer> atrs 		= TradingFeatures.getAtrArray(data);
		//2.3 differencia con sma(20)
		ArrayList<Integer> smaDiff20 	= TradingFeatures.getSmaDiff(data,20);
		//2.4 differencia con sma(30)
		ArrayList<Integer> smaDiff30 	= TradingFeatures.getSmaDiff(data,30);
		//2.5 differencia con sma(40)
		ArrayList<Integer> smaDiff40 	= TradingFeatures.getSmaDiff(data,40);
		//2.6 differencia con sma(50)
		ArrayList<Integer> smaDiff50 	= TradingFeatures.getSmaDiff(data,50);
		//componemos en un unico dataset y lo escribimos a disco
		
		
		//3) lectura datasets
		/*DataSetIterator iterator = new RecordReaderDataSetIterator(
				  recordReader, 150, CLASS_INDEX, CLASSES_COUNT);
				DataSet allData = iterator.next();*/
		int batchSize = 5000; 
		//RecordReader recordReader = new CSVRecordReader(0, ',')
		RecordReader rr = new CSVRecordReader();
        rr.initialize(new FileSplit(new File(path)));
        DataSetIterator iterator = new RecordReaderDataSetIterator(rr,batchSize,7,2);//cache,classIndex,
        
        DataSet allData = iterator.next();
        allData.shuffle();
        SplitTestAndTrain testAndTrain = allData.splitTestAndTrain(0.65);  //Use 65% of data for training

        DataSet trainingData = testAndTrain.getTrain();
        DataSet testData = testAndTrain.getTest();
        
        //4)normalizacion de las entradas

        
        System.out.println("Programa finalizado");
	}

}
