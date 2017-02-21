import java.io.*;
import java.util.*;
import java.lang.*;
public class NaiveBayesClassifier extends helperFunctions {

	public static void main(String[] args){
		String trainingText = args[0];
		String fileName = trainingText;
		Long trainingStartTime = System.currentTimeMillis();

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//@@@@@@@@@@@@@@@@@@@@@@@BEGINNING OF TRAINING@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@	        
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

		// Count number of movie reviews
		ArrayList<Double> reviewArray = countNumReviews(trainingText);
		Double numReviews = reviewArray.get(0);
		Double numNegReviews = reviewArray.get(1);
		Double numPosReviews = reviewArray.get(2);

		// Initialize Map of reviews
		Map<Integer, ArrayList<String>> mapOfReviews = new HashMap<Integer, ArrayList<String>>();
		for (Integer i=0; i< numReviews; i++){
			mapOfReviews.put(i, new ArrayList<String>());
		}
		// Initialize Map for positive/negative-knownWords and their frequency
		HashMap<String, Double> positiveKnownWords = new HashMap<String,Double>();
		HashMap<String, Double> negativeKnownWords = new HashMap<String,Double>();
		HashMap<String, Double> allKnownWords = new HashMap<String,Double>();

		// Map iterator
		Integer reviewCounter = 0; //counts which review OF map
		ArrayList<String> review; // review OF map

		// Read file
		String line = null;
		String[] wordsInLine;
		String lastChar;
		try {
	            FileReader fileReader =  new FileReader(fileName);
	            BufferedReader bufferedReader = new BufferedReader(fileReader);

	            while((line = bufferedReader.readLine()) != null) {
	            	// Get i-th review from map
	            	review = mapOfReviews.get(reviewCounter);
	                wordsInLine = line.split(" ");

	                for (String word : wordsInLine){
	                	word = removeSpecialChars(word);
	                	word = word.toLowerCase();
	                	word = removeTrailingS(word);
	                	word = removeTrailingING(word);
	                	word = removeTrailingED(word);
	                	if (!word.equals("")){
	                		review.add(word);
	                	}	
	                }
	               	// Check if it is a pos/neg review, then add the review into known_words
	            	lastChar = line.substring(line.length() - 1);
	        		if (lastChar.equals("0")){
	                	addReviewWordsToKnownWords(negativeKnownWords, review);
	                	addReviewWordsToKnownWords(allKnownWords, review);
	                	reviewCounter++;
	                	continue;
	        		}

	        		if (lastChar.equals("1")){
	                	addReviewWordsToKnownWords(positiveKnownWords, review);
	                	addReviewWordsToKnownWords(allKnownWords, review);
	                	reviewCounter++;
	                	continue;
	        		}
	        		else{
	        			break;
	        		}

	        	}
	            bufferedReader.close();
	        }
	        catch(FileNotFoundException ex) {
	            System.out.println(
	                "Unable to open file '" + 
	                fileName + "'");                
	        }
	        catch(IOException ex) {
	            ex.printStackTrace();
	        }

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//@@@@@@@@@@@@@@@@@@@@@@@END OF TRAINING@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@	        
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	    Long trainingEstimatedTime = (System.currentTimeMillis() - trainingStartTime)/1000;
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//@@@@@@@@@@@@@@@@@@@@@@@BEGINNING OF TESTING@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@	        
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	    List<String> stopWords = new ArrayList<>(Arrays.asList(
	    	"the", "a","about","as","at","because","been","being",
	    	"but","she","he","it","they","so", "by","doing","to","too","was","me",
	    	"to", "and", "i", "the", "some", "of", "that", "other","in", "with",
	    	"just","since","film", "you","was","who","one","really","have","seen",
	    	"character","wa","which","even","still", "come", "said", "picture"
	    	));

		Long testingStartTime = System.currentTimeMillis();
		String testFileName = args[1];
		ArrayList<String> actualTestValues = new ArrayList<String>();
		ArrayList<String> theoTestValues = new ArrayList<String>();

		Double probPosReviewValue;
		Double probNegReviewValue;

		line = null;
		try{
			FileReader fileReader =  new FileReader(testFileName);
	        BufferedReader bufferedReader = new BufferedReader(fileReader);
			while((line = bufferedReader.readLine()) != null) {
				lastChar = line.substring(line.length() - 1);
				actualTestValues.add(lastChar);
				// Reset probPos/NegVReviewValues at the beginning of every review
				probPosReviewValue = Math.log(probPosReview(numPosReviews,numReviews));
				probNegReviewValue = Math.log(probNegReview(numNegReviews,numReviews));
				wordsInLine = line.split(" ");
	            for (String word : wordsInLine){
	               	word = removeSpecialChars(word);
	                word = word.toLowerCase();
	                word = removeTrailingS(word);
	                word = removeTrailingING(word);
	                word = removeTrailingED(word);
	                if(word.equals("0") || word.equals("1") || word.equals("") || stopWords.contains(word)){
	                	continue;
	                }
	                Double probWordGivenPosWords = probWordGivenKnownWords(
	                								1,
	                								word, 
	                								positiveKnownWords,
	                								negativeKnownWords,
	                								allKnownWords
	                								);
	                Double probWordGivenNegWords = probWordGivenKnownWords(
	                								0,
	                								word,
	                								positiveKnownWords, 
	                								negativeKnownWords,
	                								allKnownWords
	                								);
	                // Determine new probPos/NegReviewValues
	                probPosReviewValue += Math.log(probWordGivenPosWords);
	                probNegReviewValue += Math.log(probWordGivenNegWords);
	            }
	            if(probPosReviewValue != 0.0){
	            	if(probPosReviewValue > probNegReviewValue) {
	            		theoTestValues.add("1");
	            		System.out.println("1");
	            	}
	            	if(probPosReviewValue < probNegReviewValue){
	            		theoTestValues.add("0");
	            		System.out.println("0");
	            	}
	            }
			}
			bufferedReader.close();
		}
		catch(FileNotFoundException ex) {
	            System.out.println(
	                "Unable to open file '" + 
	                testFileName + "'");                
	    }
	    catch(IOException ex) {
	            ex.printStackTrace();
	    }
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//@@@@@@@@@@@@@@@@@@@@@@@END TESTING@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@	        
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//@@@@@@@@@@@@@@@@@@@@@@@BEGINNING OF TESTING (TRAINING)@@@@@@@@@@@@@@@@@@@@@@@@@@
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

		ArrayList<String> theoTrainingValues = new ArrayList<String>();
		ArrayList<String> actualTrainingValues = new ArrayList<String>();

		probPosReviewValue=0.0;
		probNegReviewValue=0.0;

		line = null;

		try{
			FileReader fileReader =  new FileReader(trainingText);
	        BufferedReader bufferedReader = new BufferedReader(fileReader);
			while((line = bufferedReader.readLine()) != null) {
				lastChar = line.substring(line.length() - 1);
				actualTrainingValues.add(lastChar);
				// Reset probPos/NegVReviewValues at the beginning of every review
				probPosReviewValue = Math.log(probPosReview(numPosReviews,numReviews));
				probNegReviewValue = Math.log(probNegReview(numNegReviews,numReviews));
				wordsInLine = line.split(" ");
	            for (String word : wordsInLine){
	               	word = removeSpecialChars(word);
	                word = word.toLowerCase();
	                word = removeTrailingS(word);
	                word = removeTrailingING(word);
	                word = removeTrailingED(word);
	                if(word.equals("0") || word.equals("1") || word.equals("") || stopWords.contains(word)){
	                	continue;
	                }
	                Double probWordGivenPosWords = probWordGivenKnownWords(
	                								1,
	                								word, 
	                								positiveKnownWords,
	                								negativeKnownWords,
	                								allKnownWords
	                								);
	                Double probWordGivenNegWords = probWordGivenKnownWords(
	                								0,
	                								word,
	                								positiveKnownWords, 
	                								negativeKnownWords,
	                								allKnownWords
	                								);
	                // Determine new probPos/NegReviewValues
	                probPosReviewValue += Math.log(probWordGivenPosWords);
	                probNegReviewValue += Math.log(probWordGivenNegWords);
	            }
	            if(probPosReviewValue != 0.0){
	            	if(probPosReviewValue > probNegReviewValue) {
	            		theoTrainingValues.add("1");
	            	}
	            	if(probPosReviewValue < probNegReviewValue){
	            		theoTrainingValues.add("0");
	            	}
	            }
			}
			bufferedReader.close();
		}
		catch(FileNotFoundException ex) {
	            System.out.println(
	                "Unable to open file '" + 
	                testFileName + "'");                
	    }
	    catch(IOException ex) {
	            ex.printStackTrace();
	    }

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//@@@@@@@@@@@@@@@@@@@@@@@END TESTING (TRAINING)@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@	        
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@	


	    /*
	    	printAllKnownWords(positiveKnownWords);
	    	ArrayList<String> commonPosWords = getTenMostCommonWords(positiveKnownWords);
	    	printActual(commonPosWords, positiveKnownWords);
	    	System.out.println("");
	    	ArrayList<String> commonNegWords = getTenMostCommonWords(negativeKnownWords);
	    	printActual(commonNegWords, negativeKnownWords);
	    	System.out.println("");
	    	ArrayList<String> commonAllWords = getTenMostCommonWords(allKnownWords);
	    	printActual(commonAllWords, allKnownWords);
	    	System.out.println("");

		*/
	       	Long totalEstimatedTime = (System.currentTimeMillis() - trainingStartTime)/1000;
	       	Double testingAccuracy = compareValues(actualTestValues, theoTestValues);
	       	Double trainingAccuracy = compareValues(actualTrainingValues, theoTrainingValues);
	        System.out.println(trainingEstimatedTime.toString() + " seconds (training)");
	        System.out.println(totalEstimatedTime.toString() + " seconds (labeling)");
	        System.out.println(trainingAccuracy.toString() + " (training)");
	        System.out.println(testingAccuracy.toString() + " (testing)");

	       
	}
}