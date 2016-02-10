
public class Point {
	static int numberofPoints;

	private double[] featureSet; 
	private int closestCentroid;
	private int PointID;

	public Point(){

		PointID=numberofPoints;
	}

	public Point(double[] featureSet){
		this.setFeatureSet(featureSet);
		numberofPoints++;
		PointID=numberofPoints;
	}

	public int getPointID() {
		return PointID;
	}

	public void setPointID(int pointID) {
		PointID = pointID;
	}

	public double[] getFeatureSet() {
		return featureSet;
	}
	public void setFeatureSet(double[] FeatureSet) {
		this.featureSet=FeatureSet.clone();	
	}
	public int getClosestCentroid() {
		return closestCentroid;
	}
	public void setClosestCentroid(int closestCentroid) {
		this.closestCentroid = closestCentroid;
	}
	public void debug(){
		System.out.print(PointID+"\t");
		for (int i=0;i<featureSet.length;i++){
			System.out.printf("%4.2f\t",featureSet[i]);
		}
		System.out.print("Closest:" + this.closestCentroid );
		System.out.println();
	}


}
