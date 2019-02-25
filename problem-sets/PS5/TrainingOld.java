import java.io.*;
import java.util.*;

public class TrainingOld {
	// map of each possible part of speech to all the words that fit that part of speech and how frequently they occur - updated to logarithmic probability in normalizeFrequencies method
	Map<String, Map<String,Double>> tagsToWords;
	
	// map of each possible part of speech to each next observed part of speech and how frequently they occur - updated to logarithmic probability in normalizeFrequencies method
	Map<String, Map<String,Double>> tagsToTags;

	/**
	 * constructor method for class Training - instantiates maps, fills maps, normalizes values, then runs test method
	 * @param sent		file path for sentences file
	 * @param tags		file path for tags file
	 */
	public TrainingOld(String sent, String tags) {
		// instantiate the maps
		tagsToWords = new HashMap<String, Map<String,Double>>();
		tagsToTags = new HashMap<String, Map<String, Double>>();
		
		// fill maps by running through training files and counting observances
		createTagsToWords(sent, tags);
		createTagsToTags(tags);
		
		// change frequencies to logarithmic probabilities
		normalizeObservationFrequencies();
		normalizeTransitionFrequencies();
		
		// run test
		testViterbi("texts/brown-test-sentences.txt","texts/brown-test-tags.txt");
	}
	
	public void createTagsToWords(String sent, String t) {
		try {
			// create a BufferedReader for the sentences and tags files
			BufferedReader sentences = new BufferedReader(new FileReader(sent));
			BufferedReader tags = new BufferedReader(new FileReader(t));
			
			// create a string for the line of each file
			String sentLine = sentences.readLine();
			String tagLine = tags.readLine();
			
			// while they are lines left to read
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
	
	
	public void createTagsToTags(String t) {
		try {
			// create a BufferedReader for the tags file
			BufferedReader tags = new BufferedReader(new FileReader(t));
						
			// create a string for the line of the file
			String tagLine = tags.readLine();
			int count = 0;
			
			// while they are lines left to read
			while(tagLine != null) {
				// create an array of strings for each line in the files
				String[] tagList = tagLine.split("\\ ");
								
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
	public void normalizeObservationFrequencies() {	
		
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
	public void normalizeTransitionFrequencies() {		
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
				
				// if this is the start node, determine the type of the next word
				if (currState == "#") {
					// create temporary variables for the best score and and it's code
					String code = "placeholder";
					Double score = 10000000.0;
										
					// loop over all tags and check for the first observed word
					// we have to know the type of the first word (by getting its value from the tagsToWords map) in order to then predict likelihood of next word type
					for (String tag : tagsToWords.keySet()) {
						// if that tag has the first word, determine if it's score is the best seen so far
						if (tagsToWords.get(tag).containsKey(observations[i])) {
							// if best seen so far, update code and score references
							if (Math.abs(tagsToWords.get(tag).get(observations[i])) < score) {
								score = tagsToWords.get(tag).get(observations[i]);
								code = tag;
							}
						
						}
					}
					
					// if our map does not have this word, guess that it is a verb and move on
					if (code == "placeholder") {
						code = "V";
						score = unknown;
					}
					
					// add the first code and its score to the set and map for next iteration
					nextStates.add(code);
					nextScores.put(code, score);
					
					// if this is a new iteration over the list of words (meaning we haven't created a map for the backtrack list), make one
					if (backTrack.size() == i) {
						backTrack.add(i, new HashMap<String,String>());
					}
					// add the next and current states to the backtrack map
					backTrack.get(i).put(code, "#");
				}
				
				// if not the start node, determine all next options
				else {
									
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
						
						System.out.println(observationScore);
						
						// combine these values to get a next score
						Double nextScore = currScore + transitionScore + observationScore;
						
						// if the map of next scores doesn't contain this state or we found a better next score, add it to the map
						if (!nextScores.containsKey(nextState) || nextScore > nextScores.get(nextState)) {
							nextScores.put(nextState, nextScore);
							
							// if this is a new iteration over the list of words (meaning we haven't created a map for the backtrack list), make one
							if (backTrack.size() == (i-1)) {
								backTrack.add(i-1, new HashMap<String,String>());
							}
							// add the next and current states to the backtrack map
							backTrack.get(i-1).put(nextState, currState);
						}
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
		for (String code : currScores.keySet()) {
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
		
		if (path.size() > 1) {
			
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
			
			// add the last determined last pointer to the list
			bestPath.add(last);
		}
		
		// return the best path list
		return bestPath;
	}
	
	
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
								
					for (int i=0; i<bestPath.size(); i++) {
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
		}
			
		catch (Exception e) {
			System.err.println("Failed to read file.");
			System.err.println(e.getMessage());
		}
	}
	
	
	public void testViterbiBasic() {
		String old = "the jury praised the administration and operation of the atlanta police department , the fulton tax commissioner's office , the bellwood and alpharetta prison farms , grady hospital and the fulton health department .\n";
		String s = "the joblessness";
		String[] list = s.split("\\ ");
		
		List<Map<String,String>> path = viterbi(list);
		System.out.println(getBestPath(path));
	}

	/**
	 * main method, creates Training object 
	 */
	public static void main(String[] args) {
		TrainingOld t = new TrainingOld("texts/brown-train-sentences.txt","texts/brown-train-tags.txt");
	}
}