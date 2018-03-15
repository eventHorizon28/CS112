package search;

import java.io.*;
import java.util.*;


/**
 * This class encapsulates an occurrence of a keyword in a document. It stores the
 * document name, and the frequency of occurrence in that document. Occurrences are
 * associated with keywords in an index hash table.
 * 
 * @author Sesh Venugopal
 * 
 */
class Occurrence {
	/**
	 * Document in which a keyword occurs.
	 */
	String document;
	
	/**
	 * The frequency (number of times) the keyword occurs in the above document.
	 */
	int frequency;
	
	/**
	 * Initializes this occurrence with the given document,frequency pair.
	 * 
	 * @param doc Document name
	 * @param freq Frequency
	 */
	public Occurrence(String doc, int freq) {
		document = doc;
		frequency = freq;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + document + "," + frequency + ")";
	}
}

/**
 * This class builds an index of keywords. Each keyword maps to a set of documents in
 * which it occurs, with frequency of occurrence in each document. Once the index is built,
 * the documents can searched on for keywords.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in descending
	 * order of occurrence frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash table of all noise words - mapping is from word to itself.
	 */
	HashMap<String,String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashMap<String,String>(100,2.0f);
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.put(word,word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeyWords(docFile);
			mergeKeyWords(kws);
		}
		
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeyWords(String docFile) 
	throws FileNotFoundException {
		
		//System.out.println("loadKeywords begins");
		
		HashMap<String, Occurrence> lTable = new HashMap<String, Occurrence>(1000,2.0f);
		Scanner scnr = new Scanner(new File(docFile));
		
		while(scnr.hasNext())
		{
			String x = scnr.next();
			//System.out.println("initial x = "+x);
			x = getKeyWord(x);
			//System.out.println("getKeywords x = "+x);
			if(x == null)
				continue;
			else if(lTable.containsKey(x))	//if table already contains key, increase the frequency
			{
				Occurrence v = lTable.get(x);
				//System.out.println("initial: "+v.toString());
				v.frequency++;
				lTable.put(x, v);	//put replaces the Occurrence automatically if it exists already
				//System.out.println("final: "+lTable.get(x).toString());
			}
			else
			{
				Occurrence newocc = new Occurrence(docFile, 1);	//newocc has the name of docfile and frequency = 1
				lTable.put(x, newocc);
				//System.out.println("new: "+lTable.get(x).toString());
			}
		}
		//System.out.println("loadKeywords ends");
		return lTable;
	}
	//////////////////////////not adding occurences from both////////////////////////////////////////
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeyWords(HashMap<String,Occurrence> kws) {
		
		System.out.println(keywordsIndex);
		for(String key : kws.keySet())
		{
			if(keywordsIndex.containsKey(key))
			{
				ArrayList<Occurrence> frmKI = keywordsIndex.get(key);
				frmKI.add(kws.get(key));
				insertLastOccurrence(frmKI);
				keywordsIndex.put(key, frmKI);
			}
			else
			{
				ArrayList<Occurrence> idk = new ArrayList<Occurrence>();
				idk.add(kws.get(key));
				insertLastOccurrence(idk);
				keywordsIndex.put(key, idk);
			}
		}
		System.out.println(keywordsIndex);
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * TRAILING punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 *Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyWord(String word) {
		
		String first = "";
		String puncs = "";
		int i = 0;
		for(i = 0; i<word.length(); i++)
		{
			if(first.isEmpty() && word.charAt(i) == ' ')
				continue;
			
			if(!Character.isLetter(word.charAt(i)))
				break;
			
			else
				first = first+word.charAt(i);
		}
		//System.out.println("first: "+first);
		int j = i;
		for(j = i; j<word.length(); j++)
		{
			if(Character.isLetter(word.charAt(i)))
				break;
			else
				puncs+=word.charAt(j);
		}
		//System.out.println("punctuation: "+puncs);
		for(int k = 0; k<puncs.length(); k++)
		{
			if(puncs.charAt(k) != '.' && puncs.charAt(k) != ',' && puncs.charAt(k) != ';' && puncs.charAt(k) != ':' && puncs.charAt(k) != '?' && puncs.charAt(k) != '!')
			{
				return null;
			}
		}
		if(j == word.length() && first.length()>0 && !noiseWords.containsKey(first))
		{
			first = first.toLowerCase();
			if(!noiseWords.containsKey(first))
				return first;
		}
		return null;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * same list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion of the last element
	 * (the one at index n-1) is done by first finding the correct spot using binary search, 
	 * then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	//basically this method will be called everytime a document word search is completed to sort the list of words according to their frequencies
	//since every other occurrence will be sorted, only the last added occurrence list has to be sorted
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		
		/*System.out.println("occs size:      "+occs.size());
		for(int i = 0; i<occs.size(); i++){
			System.out.println(occs.get(i).toString());
		}*/
		if(occs.size() == 1)
			return null;
		
		Occurrence last = occs.get(occs.size()-1);
		//System.out.println("ini size: "+occs.size());
		occs.remove(occs.size()-1);		//these lines store and delete the last (unsorted) occurrence from the main list
		//System.out.println("final size: "+occs.size());
		ArrayList<Integer> result = new ArrayList<Integer>(6);
		
		//binary search
		int left = 0;
		int right = occs.size()-1;
		int mid = 0;
		
		while(left<=right)
		{
			mid = (left+right)/2;
			
			if(last.frequency == occs.get(mid).frequency) //if frequencies equal then result will be mid only
			{
			//	System.out.println(mid);
				result.add(mid);
				break;
			}
			
			else if(last.frequency<occs.get(mid).frequency)
			{
	//			System.out.println(mid);
				result.add(mid);
				left = mid +1;
			}
			else if(last.frequency>occs.get(mid).frequency)
			{
		//		System.out.println(mid);
				result.add(mid);
				right = mid-1;
			}
		}
//		System.out.println("ans: "+result);
		if(last.frequency == occs.get(mid).frequency)
			occs.add(mid+1, last);
		else
			occs.add(mid, last);
		
		return result;
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of occurrence frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will appear before doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matching documents, the result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of NAMES of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matching documents,
	 *         the result is null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {

		ArrayList<String> result = new ArrayList<String>(5);
		ArrayList<Occurrence> kwkw = keywordsIndex.get(kw1);
		
		//adding all the occurrences in an arrayList
		for(int i = 0; i<keywordsIndex.get(kw2).size(); i++)
		{
			kwkw.add(keywordsIndex.get(kw2).get(i));		
		}
		System.out.println(kwkw.size());
		
		if(kwkw.size() ==0)
			return null;
		
		//sort ??!!if frequencies are same, place after
		for(int i = 1; i<kwkw.size(); i++)
        {
			while(i!=0 && (kwkw.get(i).frequency > kwkw.get(i-1).frequency))
            {
                Occurrence tmp = kwkw.get(i);
                kwkw.set(i, kwkw.get(i-1));
                kwkw.set(i-1, tmp);
                i--;
            }
        }

		/*System.out.println("Sorted List")
		 for(int i = 0; i<kwkw.size(); i++)
		{
			System.out.println(kwkw.get(i).toString());
		}*/
		
		//deletion if docNames are same, delete the low frequency one
		for(int i = 0; i<kwkw.size(); i++)
		{
			int j = i+1;
			while(j<kwkw.size())
			{
				if(kwkw.get(i).document.equals(kwkw.get(j).document))
					kwkw.remove(j);
				j++;
			}
		}

		//delete greater than 5 and
		//put in result List
		for(int i = 0; i<5 && i<kwkw.size();i++)
		{
			//System.out.println(kwkw.get(i));
			result.add(kwkw.get(i).document);
		}
		System.out.println(result);
	return result;
	}
}
