// This class will serves as an object for each document, in order to perserve
// the term mappings of each document for later use.
//
// @author - Stephen Bahr (sbahr@bu.edu)

package edu.bu.sbahr;

import java.util.Map;

public class Document {

	/** Name of the document without the .txt format */
	protected String documentName;
	/** Map of terms to frequency */
	protected Map<String, Integer> termFrequency;

	/**
	 * Construct a new document
	 * 
	 * @param name - the document name
	 * @param termFreq - the map of term frequencies
	 */
	public Document(String name, Map<String, Integer> termFreq) {
		this.documentName = name;
		this.termFrequency = termFreq;
	}
}
