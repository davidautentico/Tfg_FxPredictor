package drosa.experimental.claudia;

import drosa.phil.DataCleaning;

public class DataPreparation {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String folder = "c:\\fxdata\\ScalpingTraining\\data";
		
		DataCleaning.convertToPepperFolderShort("GBPUSD", folder, folder,"_10s_data.csv");
		
	}

}
