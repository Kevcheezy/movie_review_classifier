import java.io.*;
import java.util.*;
import java.lang.*;

public class helperFunctions{
	// Returns an ArrayList with 3 values: 
	// numTotalReviews=array[0]
	// numNegReviews=array[1]
	// numPosReviews=array[2]
	public static ArrayList<Double> countNumReviews(String fileName){
		ArrayList<Double> reviewArray = new ArrayList<Double>();
		Double numTotalReviews = 0.0;
		Double numNegReviews = 0.0;
		Double numPosReviews = 0.0;
		String lastChar;

		try{
			String[] wordsInLine;
			String line = null;
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while((line = bufferedReader.readLine()) != null) {
	        	lastChar = line.substring(line.length() - 1);
	        	if (lastChar.equals("0")){
	        		numNegReviews++;
	        	}
	        	if (lastChar.equals("1")){
	        		numPosReviews++;
	        	}
	        	numTotalReviews++;				
	       	}
	       	bufferedReader.close();
	       	reviewArray.add(0,numTotalReviews);
	       	reviewArray.add(1,numNegReviews);
	       	reviewArray.add(2,numPosReviews);
			return reviewArray;
		}
	    catch(FileNotFoundException ex) {
	            System.out.println(
	                "Unable to open file '" + 
	                fileName + "'");                
	    }
	    catch(IOException ex) {
	            ex.printStackTrace();
	    }
	    return reviewArray;
	}

	public static void printAllReviews(Map<Integer,ArrayList<String>> map){
		for(Integer mapCounter = 0; mapCounter< map.size(); mapCounter++){
			ArrayList<String> review = map.get(mapCounter);
			System.out.println("Words in review are: " + review);
			System.out.println("end of review # "+ mapCounter.toString());
		}
	}

	public static void addReviewWordsToKnownWords(Map<String,Double> targetKnownWords, ArrayList<String> wordsInReview){
		Double newValue;
		for (String word : wordsInReview){
			if (targetKnownWords.containsKey(word)){
				newValue = targetKnownWords.get(word) + 1.0;
				targetKnownWords.put(word, newValue);
			}
			else{
				targetKnownWords.put(word, 1.0);
			}
		}

	}

	public static Double probPosReview(Double numPosReviews, Double numTotalReviews){
		return numPosReviews/numTotalReviews;
	}

	public static Double probNegReview(Double numNegReviews, Double numTotalReviews){
		return numNegReviews/numTotalReviews;
	}

	public static Double probWordGivenKnownWords(int posOrNeg, 
												String word,
												HashMap<String,Double> posKnownWords,
												HashMap<String,Double> negKnownWords, 
												HashMap<String,Double> allKnownWords
												){
		Double returnValue = 1.0;
		// if word is in both posKnownWords AND negKnownWords
		
		
		if (posKnownWords.containsKey(word) && negKnownWords.containsKey(word)){
			//System.out.println(word + " is in both known words");
			if (posOrNeg == 1){
				//System.out.println("checking pos");
				return (posKnownWords.get(word)+1)/(allKnownWords.size()+allKnownWords.get(word));
			}
			else{
				//System.out.println("checking neg");
				return (negKnownWords.get(word)+1)/(allKnownWords.size()+allKnownWords.get(word));
			}
		}
		
		// if word is in either pos or neg
		if (posKnownWords.containsKey(word) || negKnownWords.containsKey(word)){
			// if word is ONLY in positive
			if(posKnownWords.containsKey(word)){
				//System.out.println(word+ " is ONLY in pos words");
				return ((posKnownWords.get(word)+1))/(allKnownWords.size()+allKnownWords.get(word));
			}
			// if word is ONLY in negative
			else if(negKnownWords.containsKey(word)){
				//System.out.println(word+ " is ONLY in neg words");
				return ((negKnownWords.get(word)+1))/(allKnownWords.size()+allKnownWords.get(word));
			}

		}
		// if word is in NEITHER pos nor neg
		else{
			//System.out.println(word+ " is NOT in either words");
			return 1/(double)allKnownWords.size();
		}
		return returnValue;
	}

	public static Double totalValuesInWords(HashMap<String,Double> targetKnownWords){
		Double sum = 0.0;
		for(Double val : targetKnownWords.values()){
			sum += val;
		}
		return sum;
	}
	public static void printAllKnownWords(HashMap<String,Double> targetKnownWords){
		Set set = targetKnownWords.entrySet();
		Iterator i = set.iterator();
		while(i.hasNext()){
			Map.Entry me = (Map.Entry)i.next();
			System.out.print(me.getKey() + ": ");
			System.out.println(me.getValue());
		}
		System.out.println();
	}

	public static void printActual(ArrayList<String> actual, HashMap<String,Double> map){
		for (String s : actual){
			System.out.println(s + " : " + map.get(s).toString());
		}
	}

	public static Double compareValues(ArrayList<String> actual, ArrayList<String> theo){
		Double retVal = 0.0;
		if(actual.size() != theo.size()){
			return -1.0;
		}
		for (int i = 0; i<actual.size()-1; i++){
			if((actual.get(i)).equals(theo.get(i))){
				retVal += 1.0;
			}
		}
		return retVal/(double)actual.size();
	}

	public static ArrayList<String> getTenMostCommonWords(HashMap<String,Double> map){
		List<String> stopWords1 = new ArrayList<>(Arrays.asList(
	    	"the", "a","about","as","at","because","been","being",
	    	"but","she","he","it","they","so", "by","doing","to","too","was","me",
	    	"to", "and", "i", "the", "some", "of", "that", "other", "in", "with",
	    	"just","since","film","you","was","who","one","really","have","seen",
	    	"character","wa", "which","even","still", "come", "said", "picture"
	    	));
		ArrayList<String> retArray = new ArrayList<String>();
		for(int i = 0; i<10; i++){
			retArray.add("0");
		}
		Set<String> keys = map.keySet();
		Iterator<String> it = keys.iterator();
		for(int i = 0; i<10; i++){
			String key = it.next();
			retArray.set(i, key);
		}
		int lowest = 0;
		while(it.hasNext()){
			String key = it.next();
			if(stopWords1.contains(key)){
				continue;
			}
			//check for index of lowest freq among retArray
			for(int i = 0; i<retArray.size();i++){
				if(map.get(retArray.get(i)) < map.get(key)){
					lowest = i;
				}
			}
			retArray.set(lowest,key);
		}
		return retArray;
	}

// REMOVE FROM STRING METHODS
	public static String removeSpecialChars(String word){
		return word.replaceAll("[^A-Za-z]", "");
	}

	public static String removeZeroOrOne(String word){
		return word.replaceAll("[A-Za-z]","");
	}

	public static String removeTrailingS(String word){
		return word.replaceAll("s+$","");
	}

	public static String removeTrailingING(String word){
		return word.replaceAll("ing+$","");
	}

	public static String removeTrailingED(String word){
		return word.replaceAll("ed+$", "");
	}

}