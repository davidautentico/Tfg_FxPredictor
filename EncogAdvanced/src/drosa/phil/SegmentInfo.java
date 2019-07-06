package drosa.phil;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

public class SegmentInfo {

	Calendar beginDate = Calendar.getInstance();
	Calendar endDate = Calendar.getInstance();
	double beginValue = -1;
	double endValue = -1;
	int totalPips = 0;
	int upDown=-1;
	int pos1 = -1;
	int pos2 = -1;
	
	public Calendar getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(Calendar beginDate) {
		this.beginDate = beginDate;
	}
	public Calendar getEndDate() {
		return endDate;
	}
	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}
	public double getBeginValue() {
		return beginValue;
	}
	public void setBeginValue(double beginValue) {
		this.beginValue = beginValue;
	}
	public double getEndValue() {
		return endValue;
	}
	public void setEndValue(double endValue) {
		this.endValue = endValue;
	}
	public int getTotalPips() {
		return totalPips;
	}
	public void setTotalPips(int totalPips) {
		this.totalPips = totalPips;
	}
		
	
	public int getUpDown() {
		return upDown;
	}
	public void setUpDown(int upDown) {
		this.upDown = upDown;
	}
	
	
	
	public int getPos1() {
		return pos1;
	}
	public void setPos1(int pos1) {
		this.pos1 = pos1;
	}
	public int getPos2() {
		return pos2;
	}
	public void setPos2(int pos2) {
		this.pos2 = pos2;
	}
	public static ArrayList<Integer> extractPipArray(
			ArrayList<SegmentInfo> segments) {
		// TODO Auto-generated method stub
		 ArrayList<Integer> values = new  ArrayList<Integer>();
		 for (int i=0;i<segments.size();i++){
			 SegmentInfo info = segments.get(i);
			 values.add(info.getTotalPips());
		 }
		return values;
	}
	
	public static ArrayList<SegmentInfo> printSegments(String header,ArrayList<SegmentInfo> segments,int max,boolean print,boolean print2){
		ArrayList<SegmentInfo> filters = new ArrayList<SegmentInfo> ();
		for (int i=0;i<segments.size();i++){
			SegmentInfo seg = segments.get(i);
			if (seg.getTotalPips()>=max){
				if (print)
					System.out.println(DateUtils.datePrint(seg.getBeginDate())
					+" "+DateUtils.datePrint(seg.getEndDate())
					+" "+seg.getTotalPips()
					+" "+seg.getUpDown()
					);
				filters.add(seg);
			}
		}
		if (print2){
			System.out.println(header+" >="+max+"= "
					+filters.size()+"/"+segments.size()
					+" "+PrintUtils.Print2dec(filters.size()*100.0/segments.size(), false)
					);
		}
		return filters;
	}
	
	public static SegmentInfo findSegment(ArrayList<SegmentInfo> segments, int index){
		
		for (int i=0;i<segments.size();i++){
			SegmentInfo seg = segments.get(i);
			if (seg.getPos1()<=index && index<=seg.getPos2())
				return seg;			
		}
		return null;
	}
	
	
	
}
