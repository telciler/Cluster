import java.io.IOException;

public class Test {

	public static void main(String[] args) throws IOException {
		
		Cluster cluster = new Cluster();
		double[][] Features=cluster.ExcelReader("data2.xlsx", "Sheet1"); 
		cluster.clusterPoints(Features,2);
	}

}
