package drosa.data;

import java.io.File;
import java.util.ArrayList;

import drosa.DAO.DAO;
import drosa.finances.Quote;
import drosa.finances.QuoteBidAsk;
import drosa.phil.DataCleaning;
import drosa.phil.TestLines;
import drosa.utils.TradingUtils;

public class ConvertTrueFX {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String filePath = "C:\\Users\\david\\Documents\\trading\\data\\truefx\\";
		String filePath = "C:\\fxdata\\truefx\\";
		String fileName = filePath+"EURUSD-2013-02.csv";
		
		int y1 = 2011;
		int y2 = 2014;
		ArrayList<QuoteBidAsk> data1mTotal = new ArrayList<QuoteBidAsk>();
		for (int year=y1;year<=y2;year++){
			for (int i=1;i<=12;i++){
				fileName = filePath+"EURUSD-"+year+"-"+DataCleaning.intToStr(i)+".csv";
				System.out.println(fileName);
				File f = new File(fileName);
				if (f.exists()){
					ArrayList<QuoteBidAsk> data1mIgaps = DAO.retrieveDataBidAsk1m(fileName, DataProvider.TRUEFX);
					
					//System.out.println("****data1mIgaps******");
					//for (int h=0;h<data1mIgaps.size();h++){
						//System.out.println(data1mIgaps.get(h).toString());
					//}
					//ArrayList<QuoteBidAsk> data1mI = DataCleaning.fillGaps(data1mIgaps,1);
					/*if (DataCleaning.testGaps(data1mI,1)){
						System.out.println("GAPS 0");
						return;
					}*/
					//System.out.println("****data1mI******");
					//for (int h=0;h<data1mI.size();h++){
						//System.out.println(data1mI.get(h).toString());
					//}
					for (int j=0;j<data1mIgaps.size();j++) data1mTotal.add(data1mIgaps.get(j));
				}else{
					System.out.println(fileName+" NO EXISTE");
				}
			}
		}			
		//ArrayList<QuoteBidAsk> data1m = DataCleaning.fillGaps(data1mTotal,1);
		DataCleaning.fillGaps(data1mTotal,1);
		if (DataCleaning.testGaps(data1mTotal,1)){
			System.out.println("GAPS");
			return;
		}		
		ArrayList<QuoteBidAsk> dataS =  TestLines.calculateCalendarAdjustedBidAsk(data1mTotal);
		ArrayList<QuoteBidAsk> data5m = ConvertLib.convertBidAsk(dataS, 5);
		ArrayList<QuoteBidAsk> data5mClean = TradingUtils.cleanWeekendDataBidAsk(data5m);
		//ArrayList<QuoteBidAsk> data5m = ConvertLib.convertBidAsk(data1mTotal, 5);
		//ArrayList<QuoteBidAsk> data5m = ConvertLib.convertBidAsk(dataS, 5);
		System.out.println("****data clean 5m******");
		for (int i=0;i<data5mClean.size();i++){
			System.out.println(data5mClean.get(i).toString());
		}				
		DataCleaning.writeFileBidAsk(filePath+"EURUSD_TrueFX_5m_"+y1+"_"+y2+".csv", data5mClean);
		
		
		//System.out.println(data5mClean.size());
		
		/*ArrayList<QuoteBidAsk> data1mClean = TradingUtils.cleanWeekendDataBidAsk(dataS);
		System.out.println("data1m clean: "+data1mClean.size());

		ArrayList<QuoteBidAsk> data5m = ConvertLib.convertBidAsk(data1mClean, 5);
		for (int i=0;i<data5m.size();i++){
			System.out.println(data5m.get(i).toString());
		}
		DataCleaning.writeFileBidAsk(filePath+"EURUSD_TrueFX_2013.csv", data5m);*/
	}

}
