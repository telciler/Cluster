import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import java.lang.*;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;

public class Cluster {

	private static ArrayList<Point> PointList = new ArrayList<Point>();
	private static ArrayList<Centroid> CentroidList = new ArrayList<Centroid>();
	private static int FeatureDimension;
	private static int PointNumber;
	private static int CentroidNumber;
	private static ArrayList<Integer> InitialLocations;
	private static Color[] colorList;
	private static double[][] Features;
	//iterations
	private static double BestObjValue=Double.POSITIVE_INFINITY;
	private static ArrayList<Centroid> BestCentroids;
	private static ArrayList<Point> BestPoints;
	private static double Obj1;
	private static double PercentChange=1;
	private static double Epsilon=0.000001;
	private static int NumOfIterations=0;
	private static int NumOfTrials=0;
	private static int TrialLimit=50;
	private static long StartTime;
	private static long GlobalStart;
	private static long EndTime;
	private static long GlobalEndTime;

	public Cluster(){

	}

	public void clusterPoints(double[][] Features, int numOfCentroids) throws IOException{
		//Generate Points
		DetermineParameters(Features,numOfCentroids);
		PointGenerator();
		ListPoints(PointList);
		for (int i=0;i<TrialLimit;i++){
			InitialLocations=RandomNumberGenerator();
			CentroidGenerator(InitialLocations);
			AssignPoints();
			while (PercentChange>Epsilon){
				MoveCentroids();
				AssignPoints();
				CalculateObjective();
			}
			CheckSolution();
		}
		Display();
		ListCentroids(BestCentroids);
	}

		public static double[][] ExcelReader(String filename,String Sheetname) throws IOException{
			File excel =  new File (filename);
			FileInputStream fis = new FileInputStream(excel);
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet ws = wb.getSheet(Sheetname);
			//get dimensions
			int RowNumber = ws.getLastRowNum()+1;
			int ColNumber = ws.getRow(0).getPhysicalNumberOfCells();
			System.out.println("length:" + RowNumber);
			XSSFRow row = ws.getRow(0);
			XSSFCell cell = row.getCell(0);
			double value;
			double [][] FeatureSet=new double [RowNumber][ColNumber];
			for  (int i = 0 ;i<RowNumber;i++){
				row=ws.getRow(i);
				for (int j=0;j < ColNumber; j++ ){				
					cell=row.getCell(j);
					value=cell.getNumericCellValue();
//					System.out.println(i + "\t" + j + "\t" + value);
					FeatureSet[i][j]=value;
				}
			}
			return FeatureSet;
		}

	public void DetermineParameters(double[][] Features,int numOfCentroids){
		this.Features=Features;
		this.FeatureDimension=Features[0].length;
		this.PointNumber=Features.length;
		System.out.println("numofpoints:"+PointNumber);
		System.out.println("Dim:" + FeatureDimension);
		CentroidNumber=numOfCentroids;
	}

	public static void PointGenerator(){
		Point point = new Point();
		double[] FeatureSet = new double[FeatureDimension];
		for (int j=0;j<PointNumber;j++){	
			for (int i=0;i<FeatureDimension;i++){
				FeatureSet[i]=Features[j][i];
			}
			point = new Point(FeatureSet);
			PointList.add(point);		
		}
		ListPoints(PointList);
	}

	public static void ListPoints(ArrayList<Point> PointList){
		System.out.println();
		for (int i=0;i<PointList.size();i++){
			PointList.get(i).debug();
		}
	}

	public static void ListCentroids(ArrayList<Centroid> CentroidList){
		System.out.println();
		for (int i=0;i<CentroidList.size();i++){
			CentroidList.get(i).debug();
		}
	}

	public static ArrayList<Integer> RandomNumberGenerator(){
		Centroid.numberofCentroids=0;
		ArrayList<Integer> randomNumbers = new ArrayList<Integer>();
		int rnd;
		for (int i=0;i<CentroidNumber;i++){
			rnd=(int) (Math.random()*PointNumber);
			while(randomNumbers.contains(rnd)==true){	
				rnd=(int) (Math.random()*PointNumber);
			}
			randomNumbers.add(rnd);
		}
		return randomNumbers;		
	}

	public static void CentroidGenerator(ArrayList<Integer> InitialLocations){
		CentroidList.clear();
		Centroid centroid = new Centroid();
		for (int i=0;i<InitialLocations.size();i++){
			centroid = new Centroid(PointList.get(InitialLocations.get(i)).getFeatureSet());
			CentroidList.add(centroid);
		}
	}

	public static double CalculateDistance(Point point,Centroid centroid){
		double Distance=0;
		double sum=0;
		double diff=0;
		for (int i=0;i<point.getFeatureSet().length;i++){
			diff=point.getFeatureSet()[i]-centroid.getFeatureSet()[i];
			sum+=Math.pow(diff, 2);
		}
		Distance = Math.sqrt(sum);
		return Distance;
	}

	public static void AssignPoints(){
		double min=Double.POSITIVE_INFINITY;
		int minindex=0;
		double dist;
		for (int i=0;i<PointList.size();i++){
			min=Double.POSITIVE_INFINITY;
			minindex=0;
			for (int j=0;j<CentroidList.size();j++){
				dist=CalculateDistance(PointList.get(i),CentroidList.get(j));
				if (dist<min){
					min = dist;
					minindex=j;
				}
			}
			PointList.get(i).setClosestCentroid(CentroidList.get(minindex).getCentroidID());
			CentroidList.get(minindex).addPoint(PointList.get(i));
		}
	}

	public static void printClusters(){
		System.out.println();
		for (int i=0;i<CentroidList.size();i++){
			System.out.println("center:" + CentroidList.get(i).getCentroidID()+" points: ");
			for (int j=0;j<CentroidList.get(i).getPointsIn().size();j++){
				CentroidList.get(i).getPointsIn().get(j).debug();
			}
			System.out.println();
		}
	}

	public static void MoveCentroids(){
		double[] Destination;
		for (int i = 0;i<CentroidList.size();i++){
			Destination = CentroidList.get(i).setAverages();
			CentroidList.get(i).setFeatureSet(Destination);
			CentroidList.get(i).getPointsIn().clear();
		}
	}

	public static double CalculateObjective(){
		double ObjValue=0;
		int Clustersize;
		for (int i=0;i<CentroidList.size();i++){
			Clustersize=CentroidList.get(i).getPointsIn().size();
			for (int j=0;j<Clustersize;j++){
				ObjValue+=CalculateDistance(CentroidList.get(i).getPointsIn().get(j),CentroidList.get(i));					
			}
		}
		ObjValue=ObjValue/PointNumber;
		PercentChange=CalculatePercentChange(ObjValue);
		Obj1=ObjValue;
		NumOfIterations++;
		return ObjValue;
	}

	public static double CalculatePercentChange(double Obj){
		double Change=Math.abs((Obj1-Obj)/Obj1);
		return Change;
	}

	private static void CheckSolution() {
		if (Obj1<BestObjValue){
			BestObjValue=Obj1;
			BestCentroids = new ArrayList<Centroid>(CentroidList);
			BestPoints = new ArrayList<Point>(PointList);
		}
		else{

		}
		NumOfTrials++;
		Centroid.numberofCentroids=0;
		NumOfIterations=0;
		PercentChange=1;
		System.out.println("Trial No: "+ NumOfTrials +"\t Obj: "+ Obj1);
	}

	public static void Display(){
		//display
		if (FeatureDimension==2){
			colorList = new Color[CentroidNumber];
			int[] randomcolor = new int[3];
			for (int i=0;i<colorList.length;i++){
				for (int j=0;j<randomcolor.length;j++){
					randomcolor[j]=(int)(Math.random()*256);
				}
				colorList[i] = new Color(randomcolor[0],randomcolor[1],randomcolor[2]);
			}

			Graph<Integer, String> basis = new SparseMultigraph<Integer, String>();
			for (int i=0;i<PointNumber;i++){
				basis.addVertex(Integer.valueOf(i));
			}

			Transformer<Integer, Point2D> locationTransformer = new Transformer<Integer, Point2D>() {

				public Point2D transform(Integer vertex) {
					double x = BestPoints.get(vertex.intValue()).getFeatureSet()[0]*7+100;
					double y = BestPoints.get(vertex.intValue()).getFeatureSet()[1]*7+100;
					return new Point2D.Double((double) x, (double) y);
				}
			};

			Transformer<Integer,Paint> vertexPaint = new Transformer<Integer,Paint>() {
				public Paint transform(Integer i) {
					return colorList[BestPoints.get(i).getClosestCentroid()-1];
				}
			};

			StaticLayout<Integer, String> layout = new StaticLayout<Integer, String>(
					basis, locationTransformer);
			layout.setSize(new Dimension(1000, 1000));
			VisualizationViewer<Integer, String> vv = new VisualizationViewer<Integer, String>(
					layout);
			vv.setPreferredSize(new Dimension(1000, 1000));
			vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
			JFrame frame = new JFrame("Simple Graph View 2");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().add(vv);
			vv.setOpaque(false);
			frame.pack();
			frame.setVisible(true);
		}
		else {
		}
	}
}
