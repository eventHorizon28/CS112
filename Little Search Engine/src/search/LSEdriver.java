package search;

import java.io.FileNotFoundException;

public class LSEdriver {

	public static void main(String[] args) throws FileNotFoundException{
			
		LittleSearchEngine a = new LittleSearchEngine();
		System.out.println(a.getKeyWord("test-case"));
		a.makeIndex("docs.txt", "noisewords.txt");
		a.top5search("mohini", "kshitij");
			
	}
}
