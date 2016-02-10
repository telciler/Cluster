import java.util.ArrayList;

public class Centroid {
	static int numberofCentroids;

	private double[] featureSet;
	private int CentroidID;
	private ArrayList<Point> PointsIn = new ArrayList<Point>();
	private double[] Averages; 

	public Centroid(){
		CentroidID=numberofCentroids;
	}

	public Centroid(double[] featureSet){
		this.featureSet = featureSet.clone();
		numberofCentroids++;
		CentroidID=numberofCentroids;
	}

	public ArrayList<Point> getPointsIn() {
		return PointsIn;
	}

	public void setPointsIn(ArrayList<Point> pointsIn) {
		PointsIn = pointsIn;
	}

	public double[] getFeatureSet() {
		return featureSet;
	}

	public void setFeatureSet(double[] featureSet) {
		this.featureSet = featureSet.clone();
	}

	public int getCentroidID() {
		return CentroidID;
	}

	public void setCentroidID(int centroidID) {
		CentroidID = centroidID;
	}

	public void addPoint(Point point){
		PointsIn.add(point);
	}

	public void debug(){
		System.out.print(CentroidID+"\t");
		for (int i=0;i<featureSet.length;i++){
			System.out.printf("%4.2f\t",featureSet[i]);
		}
		System.out.println();
	}

	public double[] getAverages() {
		return Averages;
	}

	public double[] setAverages() {
		Averages = new double[featureSet.length];
		for (int i=0;i<Averages.length;i++){
			for (int j=0;j<PointsIn.size();j++){
				Averages[i]+=PointsIn.get(j).getFeatureSet()[i];
			}
			Averages[i]=Averages[i]/PointsIn.size();
			//			System.out.println(Averages[i]);
		}
		return Averages;
	}
}
