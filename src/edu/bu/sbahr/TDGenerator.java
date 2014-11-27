// This class will generate a term document matrix for the given set of
// documents.
//
// How to run: Run the file, and the console will ask for the list of documents
// to parse. The input should be supplied as a String.
// Every document path should be separated by a space, similar to the
// syntactical structure of writing command line arguments.
//
// This file parses each document, removing the stop words, and getting the stem
// of each term. For each occurrence of the term, we count it.
//
// Stemmer from http://www.tartarus.org/~martin/PorterStemmer
//
// @author - Stephen Bahr (sbahr@bu.edu)

package edu.bu.sbahr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TDGenerator {

	/** Map of all stopWords */
	private static Map<String, String> stopWordMap = new HashMap<String, String>();
	/** List of all documents */
	private static List<Document> documents = new ArrayList<Document>();

	public static void main(String[] args) {

		Scanner console = new Scanner(System.in);
		System.out.println("--------------------");
		System.out.println("Term Document Generator");
		System.out.println("--------------------\n");

		System.out.println("- Document Entry -");
		System.out.println("Please enter the documents you wish to construct a term matrix on. ");
		System.out.println("The full /path/to/file is needed. Enter multiple files by seperating paths with a space.");
		System.out.println("Ex: '/path/to/file1.txt /path/to/file2.txt /path/to/file3.txt'");
		System.out.println("\nInput: ");
		String input = console.nextLine();

		System.out.println("\nPlease enter the file location you would like the generated document to go. ");
		System.out.println("The full /path/to/file is needed.");
		System.out.println("\nInput: ");
		String saveLoc = console.nextLine();

		long startTime = System.currentTimeMillis();

		// split regex from
		// http://stackoverflow.com/questions/16483418/split-string-on-spaces-except-file-paths
		String[] inputParts = input.split("(?<!\\\\)\\s+");
		List<File> allFiles = new ArrayList<File>();
		for (String i : inputParts) {
			File f = new File(i);
			allFiles.add(f);
		}

		// show the user the arguments they passed in
		for (int i = 0; i < inputParts.length; i++) {
			System.out.println("Args[" + i + "]: " + inputParts[i]);
		}
		System.out.println();

		// construct the stop words mapping
		constructStopWords();

		// for each file, attempt to parse it
		for (File f : allFiles) {
			try {
				parseDocument(f);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		// try writing to file
		try {
			// want a document-term matrix A (which is just the term freq)
			writeToFile(new File(saveLoc), false);
			// want a document-term matrix B (which is
			writeToFile(new File(saveLoc + "Prime"), true);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("(" + (System.currentTimeMillis() - startTime) + " msecs)");
	}

	/**
	 * Populates the stop words map with a mapping of all possible stop words.
	 * We use a map to increase lookup performance.
	 */
	private static void constructStopWords() {
		String stopWords = "a,able,about,across,after,all,almost,also,am,among,an,and,any,are,as,at,be,because,been,"
						+ "but,by,can,cannot,could,dear,did,do,does,either,else,ever,every,for,from,get,got,had,has,"
						+ "have,he,her,hers,him,his,how,however,i,if,in,into,is,it,its,just,least,let,like,likely,may,"
						+ "me,might,most,must,my,neither,no,nor,not,of,off,often,on,only,or,other,our,own,rather,said,"
						+ "say,says,she,should,since,so,some,than,that,the,their,them,then,there,these,they,this,tis,"
						+ "to,too,twas,us,wants,was,we,were,what,when,where,which,while,who,whom,why,will,with,would,"
						+ "yet,you,your";

		// create the stop words hash
		String[] words = stopWords.split(",");
		if (words != null && words.length > 0) {
			for (String w : words) {
				stopWordMap.put(w, w);
			}
		}
	}

	/**
	 * Takes a path file of a document and parses the words in the document if
	 * they are not stop words. Maps these non-stop words (and the stem of them)
	 * to a counter.
	 * 
	 * @param file - document file being read
	 * @throws IOException - error reading
	 */
	private static void parseDocument(File file) throws IOException {
		// maps words to the times they appear
		Map<String, Integer> wordToCount = new HashMap<String, Integer>();
		// read from this file
		BufferedReader reader = new BufferedReader(new FileReader(file.toString()));
		// new stemming object
		Stemmer s = new Stemmer();
		// initial line
		String line = null;
		while ((line = reader.readLine()) != null) {
			// get each word
			// String[] parts = line.split("\\s");
			String[] parts = line.replaceAll("[^a-zA-Z ]", " ").toLowerCase().split("\\s+");

			// for each word in the document, if it's a stop word
			for (String part : parts) {
				// remove whitespace, and lowercase it
				part = part.trim().toLowerCase();

				// if not a stop word
				if (!stopWordMap.containsKey(part) && !part.equalsIgnoreCase("")) {

					// take the word and get char[]
					char[] cw = part.toCharArray();
					s.add(cw, cw.length);

					// get the stem of the word
					s.stem();
					String stem = s.toString();

					// add to word counter
					if (wordToCount.containsKey(stem))
						wordToCount.put(stem, wordToCount.get(stem) + 1);
					else
						wordToCount.put(stem, 1);
				}
			}
		}

		Document d = new Document(file.getName().toString(), wordToCount);
		documents.add(d);

		reader.close();
	}

	/**
	 * Writers to a specified file and appends the file name with _words.txt.
	 * 
	 * @param file - the file that we are parsing from
	 * @param invDocFreq - value of matrix cell should be termFreq *
	 *            inverseDocFreq
	 * @throws IOException - error writing
	 */
	private static void writeToFile(File f, boolean invDocFreq) throws IOException {
		String name = f.getPath();

		String newPath = name + ".csv";

		System.out.println("Attempting to write to file " + newPath);

		// write to this file
		FileWriter writer = new FileWriter(newPath);

		// build all the terms first
		List<String> totalTerms = new ArrayList<String>();
		// for each file
		for (Document d : documents) {

			// for each term in that file
			for (String term : d.termFrequency.keySet()) {

				// if not in total terms already
				if (!totalTerms.contains(term)) {
					totalTerms.add(term);
				}
			}
		}

		/*
		 * Column Declaration (document name)
		 */
		writer.append(" ");
		writer.append(',');
		for (Document d : documents) {
			String n = d.documentName.replaceAll(".txt", "");
			writer.append(n);
			writer.append(',');
		}
		writer.append('\n');

		// for each term in terms
		for (String term : totalTerms) {
			/*
			 * Row declaration (doc name)
			 */
			writer.append(term);
			writer.append(',');
			// for each document
			for (Document d : documents) {
				// the term freq map
				Map<String, Integer> map = d.termFrequency;

				// value that goes in the matrix
				double amount = 0;

				// if computing tf-idf
				if (invDocFreq) {
					int occur = 0;
					if (map.containsKey(term)) {
						occur = map.get(term);
					}
					// value should be the tf-idf
					amount = computeInvDocFreq(term, occur);
				}
				else {
					if (map.containsKey(term)) {
						amount = map.get(term);
					}
				}

				writer.append(String.valueOf(amount));
				writer.append(',');
			}

			writer.append('\n');
		}

		writer.flush();
		writer.close();

		System.out.println("File written!\n");
	}

	/**
	 * Computes the inverse document frequency given the term and the number of
	 * occurrences of that term in a specified document.
	 * 
	 * The tf-idf is defined as http://en.wikipedia.org/wiki/Tf–idf.
	 * 
	 * @param term - the term
	 * @param occurs - the number of occurences of this term in this document
	 * 
	 * @return The tf-idf (term frequency - inverse document frequency) of the
	 *         term.
	 */
	private static double computeInvDocFreq(String term, int occurs) {
		if (occurs == 0)
			return 0;

		double idf = Math.log(((double) documents.size()) / ((double) numOfDocs(term)));
		double tfidf = occurs * idf;

		return tfidf;
	}

	/**
	 * Get the number of documents that term appears in.
	 * 
	 * @param term - the term
	 * 
	 * @return The number of documents the term appears in.
	 */
	private static int numOfDocs(String term) {
		int freq = 0;
		for (Document d : documents) {
			if (d.termFrequency.containsKey(term)) {
				freq++;
			}
		}
		return freq;
	}
}
