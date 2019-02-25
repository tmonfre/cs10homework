import java.io.*;
import java.util.*;

/**
 * This program using Huffman encoding to compress and decompress files.
 * It uses trees, maps, and priority queues to encode characters in an input file to bits, then decode those bits into a copy of the original file.
 * 
 * @author Thomas Monfre, Dartmouth CS 10, Winter 2018
 */

public class Huffman {
	// declare reader and writer objects for files and bits
	BufferedReader input;
	BufferedWriter output;
	BufferedBitReader bitInput;
	BufferedBitWriter bitOutput;
	
	// declare instance variable for path name
	String pathName;
	
	// declare data structures needed for Huffman Tree encoding
	Map<Character, Integer> frequencyMap; 	// map of characters and their frequencies
	PriorityQueue<BinaryTree<CharFreqElement>> priorityQueue;	 	// priority queue of binary trees holding objects for character and its frequency
	BinaryTree<CharFreqElement> tree;							// Huffman tree of objects holding character and its frequency
	Map<Character, String> codeMap;								// map of characters to their codes
	
	/**
	 * instantiate all reader/writers and data structures
	 * @param pathName for input file
	 * @throws IOException - extended by FileNotFoundException
	 */
	public Huffman(String pathName) throws IOException {
		// store path name as instance variable - will be accessed by compress and decompress methods
		this.pathName = pathName;
		
		// create reader and writer objects for the input file and compressed file
		input = new BufferedReader(new FileReader(pathName));		
		bitOutput = new BufferedBitWriter(createCompressedPathName(pathName));
			
		// instantiate the frequency map
		frequencyMap = new HashMap<Character, Integer>();
		
		// instantiate the priority queue and pass comparator to it
		Comparator<BinaryTree<CharFreqElement>> lenCompare = new TreeComparator();
		priorityQueue = new PriorityQueue<BinaryTree<CharFreqElement>>(lenCompare);
		
		// instantiate the map of codes 
		codeMap = new HashMap<Character, String>();		
	}
	
	/**
	 * compress the input file into a new compressed file
	 * @throws IOException
	 */
	public void compress() throws IOException, Exception {	
		// call all helper methods 
		generateFrequencyTable();
		putTreesInPQ();
		createHuffmanTree();
		createCodeMap();
				
		// initialize unicode representation of each character
		input = new BufferedReader(new FileReader(pathName));		
		int unicode = input.read();
		
		// loop through each character in input file
		while (unicode != -1) {
			// convert unicode representation to character and get its code from the code map
			char c = (char) unicode;
			String code = codeMap.get(c);
			
			// loop through the code, writing bits to compressed file based on code
			for (int i=0; i<code.length(); i++) {			
				if (code.charAt(i) == '0') bitOutput.writeBit(false);
				else bitOutput.writeBit(true);
			}
			
			// advance loop
			unicode = input.read();			
		}
						
		// close input reader and output writer
		input.close();
		bitOutput.close();
		
		// confirm successful compression by printing to console
		System.out.println("compress success");
		//System.out.println("print tree: \n" + tree);
	}
	
	/**
	 * decompress the compressed file and write it to a new decompressed file
	 * @throws IOException
	 */
	public void decompress() throws IOException {
		// create reader and writer objects for the compressed file and decompressed file 
		bitInput = new BufferedBitReader(createCompressedPathName(pathName));
		output = new BufferedWriter(new FileWriter(createDecompressedPathName(pathName)));

		// initialize temporary variable to hold location in tree
		BinaryTree<CharFreqElement> node = tree;
		
		// loop through all bits in the compressed file
		while (bitInput.hasNext()) {
			
			// if hit a leaf, write that character into the decompressed file, then reset the node variable 
			if (node.isLeaf()) {
				output.write(node.getData().getCharacter());
				node = tree;
			}
			else {
				// only read the bit from the compressed file if not a leaf
				boolean bit = bitInput.readBit();
				
				// if the bit is false, go left, otherwise go right
				if (bit == false) node = node.getLeft();
				else node = node.getRight();
			}
		}
		
		// write the last character
		output.write(node.getData().getCharacter());
		
		// close the reader and writer objects
		bitInput.close();
		output.close();	
		
		// confirm successful decompression by printing to console
		System.out.println("decompress success");
	}
	
	// HELPER METHODS LISTED BELOW
	/**
	 * create the table of characters and frequencies
	 * @throws IOException
	 */
	public void generateFrequencyTable() throws IOException, Exception {
		
		// initialize unicode representation of each character
		int unicode = input.read();
		
		// initialize variable to count number of loops (used to throw error if empty file
		int count = 0;
		
		// loop through each character in input file
		while (unicode != -1) {
			// increment loop count
			count++;
			
			// convert unicode representation to character
			char c = (char) unicode;
		
			// if key not in map, add it
			if (!frequencyMap.containsKey(c)) {
				frequencyMap.put(c, 0);
			}
		
			// increment value for key
			frequencyMap.replace(c, frequencyMap.get(c) + 1);
		
			// advance loop
			unicode = input.read();			
		}
		
		// if empty file or file only has one character, throw error (based off count variable above)
		if (count == 0) {
			throw new Exception("File is empty.");
		}
		else if (count == 1) {
			throw new Exception("File only has one character.");
		}

		input.close();
	}
	
	/**
	 * put all tree elements into the priority queue
	 */
	public void putTreesInPQ() {
		// create a set of the keys in the frequency map
		Set<Character> keys = frequencyMap.keySet();
		
		// create an iterator object of those keys
		Iterator<Character> iterator = keys.iterator();
		
		// iterate over the keys in the set, create a new binary tree with the character from the set and its frequency from the map
		for (Iterator<Character> i = iterator; i.hasNext();) {
			
			// save a reference to the key then advance the iterator
			Character key = i.next();
			
			// create a new binary tree then add it to the priority queue
			BinaryTree<CharFreqElement> bt = new BinaryTree<CharFreqElement>(new CharFreqElement(key,frequencyMap.get(key)));
			priorityQueue.add(bt);
		}
	}
	
	/**
	 * create the Huffman Tree by remove from priority queue and constructing tree until only one tree is left
	 */
	public void createHuffmanTree() {
		
		while (priorityQueue.size() > 1) {
			
			// extract the two lowest-frequency trees from priority queue
			BinaryTree<CharFreqElement> t1 = priorityQueue.remove();
			BinaryTree<CharFreqElement> t2 = priorityQueue.remove();
			
			// create new element with combined frequency of t1 and t2 -- character doesn't matter so set to ~
			CharFreqElement total = new CharFreqElement('~', t1.getData().getFrequency() + t2.getData().getFrequency());
			
			// create new tree with subtrees of t1 and t2 and data the previously constructed element
			BinaryTree<CharFreqElement> t = new BinaryTree<CharFreqElement>(total, t1, t2);
			
			// add the new tree to the priority queue
			priorityQueue.add(t);
		}
		
		// only one tree left in priority queue so remove it -- this is the Huffman Code Tree
		tree = priorityQueue.remove();
		
		// if the tree only contains one node (meaning the input file is only one character repeated a series of times)
		// create a new element to serve as the root, then append the tree of just one node to its children
		// this handles the case in which the file is just one character repeated over and over
		if (tree.size() == 1) {
			CharFreqElement newElement = new CharFreqElement('~', 11);
			BinaryTree<CharFreqElement> newTree = new BinaryTree<CharFreqElement>(newElement,tree,tree);
			tree = newTree;
		}
	}
	
	/**
	 * non-recursive method to create code map, calls recursive helper method
	 */
	public void createCodeMap() {
		codeMapHelper(tree, "");
	}
	
	/**
	 * recursive helper method to create code map
	 * @param t 		current tree
	 * @param path	path of 0s and 1s for how it got there
	 */
	public void codeMapHelper(BinaryTree<CharFreqElement> t, String path) {
		
		if (!t.hasLeft() && !t.hasRight()) {
			codeMap.put(t.getData().getCharacter(), path);
		}
		else {
			String pathCopy = path;
			codeMapHelper(t.getLeft(), path.concat("0"));
			codeMapHelper(t.getRight(), pathCopy.concat("1"));
		}
	}
	
	/**
	 * splice the original path name to be everything but ".txt" then add "_compressed.txt"
	 * @param pathName of input file
	 * @return string representing compressed file path
	 */
	private static String createCompressedPathName(String pathName) {
		pathName = pathName.substring(0, pathName.length() - 4 );
		pathName += "_compressed.txt";
		
		return pathName;
	}
	
	/**
	 * splice the original path name to be everything but ".txt" then add "_decompressed.txt"
	 * @param pathName of input file
	 * @return string representing decompressed file path
	 */
	private static String createDecompressedPathName(String pathName) {
		pathName = pathName.substring(0, pathName.length() - 4 );
		pathName += "_decompressed.txt";
		
		return pathName;
	}
	
	
	// MAIN METHOD BELOW
	/**
	 * instantiate Huffman object, compress it, then decompress it
	 */
	public static void main(String[] args) {
		try {
			Huffman huffEncode = new Huffman("inputs/WarAndPeace.txt");
			huffEncode.compress();
			huffEncode.decompress();
		}
		catch (IOException e) {
			System.err.println("Failed to read file.");
			System.err.println(e.getMessage());
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}