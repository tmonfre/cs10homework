import java.io.*;
import java.util.*;

/**
 * program to recognize parts of speech of words
 * makes use of a Hidden Markov Model (HMM) by training tags then applying them
 * 
 * @author Thomas Monfre, Dartmouth College CS 10, Winter 2018
 */
public class Sudi {
	// declare instance variables:
	// map of each possible part of speech to all the words that fit that part of speech and how frequently they occur - updated to logarithmic probability in normalizeFrequencies method
	Map<String, Map<String,Double>> tagsToWords;
	
	// map of each possible part of speech to each next observed part of speech and how frequently they occur - updated to logarithmic probability in normalizeFrequencies method
	Map<String, Map<String,Double>> tagsToTags;

	/**
	 * constructor method for class Sudi - instantiates maps, fills maps, then normalizes values to logarithmic probability
	 * @param sent		file path for sentences file
	 * @param tags		file path for tags file
	 */
	public Sudi(String sent, String tags) {
		// instantiate the maps
		tagsToWords = new HashMap<String, Map<String,Double>>();
		tagsToTags = new HashMap<String, Map<String, Double>>();
		
		// fill maps by running through training files and counting observances
		createTagsToWords(sent, tags);
		createTagsToTags(tags);
		
		// change frequencies to logarithmic probabilities
		normalizeObservationFrequencies();
		normalizeTransitionFrequencies();
	}
	
	/**
	 * training method
	 * fills the map of parts of speech to words to their frequencies of observation
	 * @param sent		path for file containing sentences
	 * @param t			path for file containing parts of speech
	 */
	private void createTagsToWords(String sent, String t) {
		try {
			// create a BufferedReader for the sentences and tags files
			BufferedReader sentences = new BufferedReader(new FileReader(sent));
			BufferedReader tags = new BufferedReader(new FileReader(t));
			
			// create a string for the line of each file
			String sentLine = sentences.readLine();
			String tagLine = tags.readLine();
			
			// while there are lines left to read
			while(sentLine != null && tagLine != null) {
				// create an array of strings for each line in the files
				String[] sentList = sentLine.split("\\ ");
				String[] tagList = tagLine.split("\\ ");
				
				// loop through the lists
				for (int i=0; i<sentList.length; i++) {
					// grab the word and its tag
					String word = sentList[i].toLowerCase();
					String tag = tagList[i];
					
					// if the map of parts of speech to words doesn't have this tag, add it
					if (!tagsToWords.containsKey(tag)) {
						tagsToWords.put(tag, new HashMap<String,Double>());
					}
					
					// get the map of words to counts
					Map<String,Double> tagWordCount = tagsToWords.get(tag);
					
					// if this map doesn't have this word, add it with frequency 0
					if(!tagWordCount.containsKey(word)) {
						tagWordCount.put(word, 0.0);
					}
					
					// increment the frequency of that word by one
					tagWordCount.put(word, tagWordCount.get(word) + 1);
				}
				// read next lines
				sentLine = sentences.readLine();
				tagLine = tags.readLine();
			}
			// close files and confirm completion
			sentences.close();
			tags.close();
			
			System.out.println("States to words to frequency map filled.");
		}
		catch (Exception e) {
			System.err.println("Failed to read file.");
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * training method
	 * fills the map of parts of speech to next observed parts of speech to their frequencies of observation
	 * the values for a part of speech are only those parts of speech that have been observed to follow the current one, tagged with how frequently they've been observed to follow
	 * @param t			path for file containing parts of speech
	 */
	private void createTagsToTags(String t) {
		try {
			// create a BufferedReader for the tags file
			BufferedReader tags = new BufferedReader(new FileReader(t));
						
			// create a string for the line of the file
			String tagLine = tags.readLine();
			int count = 0;
			
			// while there are lines left to read
			while(tagLine != null) {
				// create an array of strings for each line in the files
				String[] tagList = tagLine.split("\\ ");
				
				if (!tagsToTags.containsKey("#")) {
					tagsToTags.put("#", new HashMap<String,Double>());
				}
				
				// grab the map of possible next tags to their frequencies
				Map<String,Double> hashtagTagCount = tagsToTags.get("#");
					
				// if that map hasn't yet seen the next tag, add it with frequency 0
				if (!hashtagTagCount.containsKey(tagList[0])) {
					hashtagTagCount.put(tagList[0], 0.0);
				}
				
				hashtagTagCount.put(tagList[0], hashtagTagCount.get(tagList[0]) + 1);
								
				// the next observed tag in the sentence
				String nextTag;
				
				// loop through the lists
				for (int i=0; i<tagList.length - 1; i++) {
					// grab the tag
					String tag = tagList[i];
						
					// grab the next tag
					nextTag = tagList[i+1];
						
					// if map of tags to next tags hasn't yet seen this tag, add it
					if (!tagsToTags.containsKey(tag)) {
						tagsToTags.put(tag, new HashMap<String,Double>());
					}
						
					// grab the map of possible next tags to their frequencies
					Map<String,Double> tagTagCount = tagsToTags.get(tag);
						
					// if that map hasn't yet seen the next tag, add it with frequency 0
					if (!tagTagCount.containsKey(nextTag)) {
						tagTagCount.put(nextTag, 0.0);
					}
						
					// increment the map of next tags to frequencies
					tagTagCount.put(nextTag, tagTagCount.get(nextTag) + 1);
						
					// update next tag
					nextTag = tagList[i+1];
				}
				// read next line
				tagLine = tags.readLine();
			}
			// close files and confirm completion
			tags.close();
									
			System.out.println("States to next states to frequency map filled.\n");
		}
		catch (Exception e) {
			System.err.println("Failed to read file.");
			System.err.println(e.getMessage());
		}
	}
		
	/**
	 * loop over the map of word observations and change their key values to logarithmic probabilities as opposed to frequencies
	 */
	private void normalizeObservationFrequencies() {	
		
		// loop over each tag in the map
		for (String tag : tagsToWords.keySet()) {
			// grab the map of words to frequencies
			Map<String, Double> words = tagsToWords.get(tag);
			
			// loop over each word in the map and sum its occurrences
			double sum = 0;
			for (String word : words.keySet()) {
				sum += words.get(word);
			}
			
			// loop over each word in the map again, calculate its probability (frequency / total sum), compute the log of that value, then add it to the map
			for (String word : words.keySet()) {
				double prob = words.get(word) / sum;
				double log = Math.log(prob);
				
				// change the value in the map from frequency to logarithmic probability
				tagsToWords.get(tag).put(word, log);
			}
		}
		System.out.println("Observation frequencies normalized to logarithmic probabilities.");
	}
	

	/**
	 * loop over the map of word transitions and change their key values to logarithmic probabilities as opposed to frequencies
	 */
	private void normalizeTransitionFrequencies() {		
		// loop over each tag in the map
		for (String tag : tagsToTags.keySet()) {
			// grab the map of words to frequencies
			Map<String, Double> nextTags = tagsToTags.get(tag);
			
			// loop over each word in the map and sum its occurrences
			double sum = 0;
			for (String nextTag : nextTags.keySet()) {
				sum += nextTags.get(nextTag);
			}
			
			// loop over each word in the map again, calculate its probability (frequency / total sum), compute the log of that value, then add it to the map
			for (String nextTag : nextTags.keySet()) {
				double prob = nextTags.get(nextTag) / sum;
				double log = Math.log(prob);
				
				// change the value in the map from frequency to logarithmic probability
				tagsToTags.get(tag).put(nextTag, log);
			}
		}
		
		System.out.println("Transition frequencies normalized to logarithmic probabilities.\n");
	}
	
	/**
	 * run Viterbi's algorithm on a list of observations (words)
	 * @param observations		list of words
	 * @return					backtracked list of all possible options to the final conclusion
	 */
	public List<Map<String,String>> viterbi(String[] observations) {
		// create set for current states and map for current scores
		Set<String> currStates = new HashSet<String>();
		Map<String,Double> currScores = new HashMap<String,Double>();
		
		// add # to indicate starting node
		currStates.add("#");
		currScores.put("#", 0.0);
				
		// value for when next states don't contain the current key
		Double unknown = -25.0;
		
		// list of maps to keep track of all possible backtracked options
		List<Map<String,String>> backTrack = new ArrayList<Map<String,String>>();
		
		// loop over each word in the input list
		for (int i=0; i<observations.length; i++) {
			
			// create a set for next states and a set for next scores
			Set<String> nextStates = new HashSet<String>();
			Map<String,Double> nextScores = new HashMap<String,Double>();
						
			// loop over all current states
			for (String currState : currStates) {
				// loop over each possible next state based on map of tagsToTags (transition map)
				for (String nextState : tagsToTags.get(currState).keySet()) {
					// add each next state to the nextStates map
					nextStates.add(nextState);
						
					// calculate current, transition, and observation scores
					Double currScore = currScores.get(currState);
					Double transitionScore = tagsToTags.get(currState).get(nextState);
					Double observationScore;
					
					// if the next state contains the word we are looking at, use that observation score
					if(tagsToWords.get(nextState).containsKey(observations[i])) {
						observationScore = tagsToWords.get(nextState).get(observations[i]);
					}
					// if not, use the unknown score defined above
					else {
						observationScore = unknown;
					}			
																					
					// combine these values to get a next score
					Double nextScore = currScore + transitionScore + observationScore;
						
					// if the map of next scores doesn't contain this state or we found a better next score, add it to the map
					if ((!nextScores.containsKey(nextState)) || (nextScore > nextScores.get(nextState))) {
						nextScores.put(nextState, nextScore);
							
						// if this is a new iteration over the list of words (meaning we haven't created a map for the backtrack list), make one
						if (backTrack.size() <= (i)) {
							backTrack.add(i, new HashMap<String,String>());
						}
						// add the next and current states to the backtrack map
						backTrack.get(i).put(nextState, currState);
					}
				}
			}
			// increment the set and map for next iteration
			currStates = nextStates;
			currScores = nextScores;
		}
		
		// find the best score of the last observation in the backtrack list, replace that map with only the best

		// hold temporary references to best score and its state (set originally to 10000000) so even one score will beat it
		Double score = 10000000.0;
		String state = "placeholder";
		
		// loop over all codes in the last reference of current scores
		for (String code : backTrack.get(backTrack.size()-1).keySet()) {
			// if found a better score, update the temporary variables for score and state
			if (Math.abs(currScores.get(code)) < score) {
				score = currScores.get(code);
				state = code;
			}
		}
				
		// create temporary variables for that code's backpointer
		String first = "placeholder";
		String second = "placeholder";
		
		// loop over all codes in just the last map in the list
		for (String code : backTrack.get(backTrack.size()-1).keySet()) {
			// if found the state we determined best, set first and second to its values
			if (code == state) {
				first = code;
				second = backTrack.get(backTrack.size()-1).get(code);
			}
		}
		
		// reset the last element in the backtrack list to an empty map, then only add the first and second (for what we determined is best score)
		backTrack.set(backTrack.size()-1, new HashMap<String,String>());
		backTrack.get(backTrack.size()-1).put(first, second);
					
		// return the backtrack map		
		return backTrack;
	}
	
	/**
	 * given a list of all possible options from the Viterbi algorithm, determine the path taken
	 * @param l			list of all possible options from Viterbi algorithm
	 * @return			actual path taken
	 */
	public List<String> getBestPath(List<Map<String,String>> path) {
		// create a list to be returned
		List<String> bestPath = new ArrayList<String>();
								
		// create a variable for the last seen element
		String last = "placeholder";
				
		// set last to the last item in the list
		for (String s : path.get(path.size()-1).keySet()) {
			last = path.get(path.size()-1).get(s);
		}
								
		// add the last item in the list to the best path
		bestPath.add(last);
			
		// loop backwards through given list
		for (int i=path.size()-1; i>=0; i--) {
				
			// loop through each code in that index's map
			for (String code : path.get(i).keySet()) {
				// if that code is the last one we've seen
				if (code == last) {
					// add its value to the best path, update last, and break the loop
					bestPath.add(0, path.get(i).get(code));
					last = path.get(i).get(code);
					break;
					}
				}
			}
				
		// remove the front element in the list
		bestPath = bestPath.subList(1, bestPath.size());
					
		// return the best path list
		return bestPath;
	}
	
	/**
	 * file based test method for the Viterbi decoding
	 * calculates/estimates the parts of speech of all words in a sentences file, then checks those estimations with their actual parts of speech in a tags file
	 * @param sentPath		path to sentences file
	 * @param tagPath		path to tag file
	 */
	public void testViterbi(String sentPath, String tagPath) {	
		try {			
			// create a BufferedReader for the sentences and tags files
			BufferedReader sentences = new BufferedReader(new FileReader(sentPath));
			BufferedReader tags = new BufferedReader(new FileReader(tagPath));
			
			System.out.println("Running test on '" + sentPath + "'");
			
			System.out.println("Calculating...\n");
			
			// create a string for the line of each file
			String sentLine = sentences.readLine();
			String tagLine = tags.readLine();
			
			double correct = 0.0;
			double incorrect = 0.0;
									
			// while they are lines left to read
			while(sentLine != null && tagLine != null) {
				// create an array of strings for each line in the files
				String[] sentList = sentLine.split("\\ ");
				String[] tagList = tagLine.split("\\ ");
				
				if (sentList.length > 0) {
					List<Map<String,String>> path = viterbi(sentList);
					List<String> bestPath = getBestPath(path);
														
					for (int i=0; i<bestPath.size()-1; i++) {
						if (bestPath.get(i).equals(tagList[i])) correct++;
						else incorrect++;
					}
				}
				
				// read next lines
				sentLine = sentences.readLine();
				tagLine = tags.readLine();
			}
			// close files and confirm completion
			sentences.close();
			tags.close();
						
			// print the number of correct and incorrect
			System.out.println("Test results:\n");
			
			System.out.println("Correct tags:   " + (int)correct);
			System.out.println("Incorrect tags: " + (int)incorrect);
			
			// calculate the percentage correct and format it nicely
			System.out.println("Percentage Correct: " + Double.parseDouble(new java.text.DecimalFormat("0.00").format(100*(correct / (incorrect + correct)))) + "%");
			System.out.println();
		}
			
		catch (Exception e) {
			System.err.println("Failed to read file.");
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * more simple test method
	 * simply does viterbi decoding on a short simple sentence
	 */
	public void testViterbiBasic() {		
		String s = "The dog ran quickly .";
		System.out.println("Running test on '" + s + "'");

		String[] list = s.split("\\ ");
				
		List<Map<String,String>> path = viterbi(list);
		System.out.println(getBestPath(path) + "\n");
	}
	
	/**
	 * create and run the scanner until the user quits the program - by typing "QUIT"
	 */
	public void startScanner() {
		// construct a new scanner object
		Scanner in = new Scanner(System.in);
		
		System.out.print("Now scanning for input sentence... ");
		System.out.println("Type 'QUIT' to terminate program\n");
		
		// create a boolean identifying whether or not to keep scanning input
		boolean next = in.hasNext();
		
		// loop over that boolean
		while(next) {
			// grab the next line, its identifier, and its message
			String line = in.nextLine();
			
			// if the user wants to exit the program, quit loop and identify user program has ended
			if (line.equals("QUIT")) {
				next = false;
				System.out.println("Program terminated.");
			}
			
			// otherwise, pass the input on to the input handler method
			else {
				handleInput(line);
			}
		}
	}
		
	/**
	 * handler method for all inputs given by the user
	 * @param s		the entire message typed by the user
	 */
	public void handleInput(String s) {
		// confirm receipt of sentence
		System.out.println("Got sentence: '" + s + "'");
		
		// add period to end of sentence
		s = s + " .";
		
		// split the string into a list
		String[] list = s.split("\\ ");
		
		System.out.println("Estimated parts of speech:");
		
		// run viterbi and print best path
		List<Map<String,String>> path = viterbi(list);
		System.out.println(getBestPath(path) + "\n");
	}
	
	
	/**
	 * main method, creates Sudi object, runs tests, then starts console scanner
	 */
	public static void main(String[] args) {
		// instantiate new Sudi object
		Sudi t = new Sudi("texts/brown-train-sentences.txt","texts/brown-train-tags.txt");
		
		// run tests
		t.testViterbiBasic();
		t.testViterbi("texts/brown-test-sentences.txt","texts/brown-test-tags.txt");
		
		// start console scanner
		t.startScanner();
	}
}