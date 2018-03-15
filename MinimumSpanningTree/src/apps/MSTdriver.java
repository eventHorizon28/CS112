package apps;

import java.io.IOException;
import structures.Graph;

public class MSTdriver {

	public static void main(String[] args) throws IOException {
		Graph g = new Graph("graph1.txt");
		
		System.out.println("Final Answers = "+MST.execute(MST.initialize(g)));
		
	}

}
