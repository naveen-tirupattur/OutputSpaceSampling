/**
 * Class term to store the pattern, vat and neighors
 */
package textmining.outputspace1;



import java.util.TreeMap;
import java.util.TreeSet;



/**
 * @author Naveen Tirupattur
 *
 */
public class Term{
	
	// To store the pattern of term
	public TreeSet<String> pattern;
	//To store the VAT in <D1, 1>,<D2, 4> etc format
	public TreeMap<String, Integer> vat;
	//To store the neighbore of each term
	public TreeSet<Term> neighbors;
	
	public int support = -1;
	
	public boolean chosen = false;
	
	public boolean isInteresting = true;
	
	

	public boolean isInteresting() {
		return isInteresting;
	}

	public void setInteresting(boolean isInteresting) {
		this.isInteresting = isInteresting;
	}

	/**
	 * @param: null
	 * @return: List of Neighbors of term
	 */
	public TreeSet<Term> getNeighbors() {
		return neighbors;
	}
	
	/**
	 * 
	 * @param List of neighbors
	 * @return: null
	 */
	public void setNeighbors(TreeSet<Term> neighbors) {
		this.neighbors = neighbors;
	}
	
	/**
	 * @param: null
	 * @return: pattern string
	 */
	public TreeSet<String> getPattern() {
		return pattern;
	}
	
	/**
	 * @param: pattern string
	 * @return: null
	 */
	public void setPattern(TreeSet<String> pattern) {
		this.pattern = pattern;
	}
	
	/**
	 * @param: null
	 * @return: Map of VAT
	 */
	public TreeMap<String, Integer> getVat() {
		return vat;
	}
	
	/**
	 * @param: Map of VAT
	 * @return: null
	 */
	public void setVat(TreeMap<String, Integer> vat) {
		this.vat = vat;
	}

	/**
	 * @param: null
	 * @return: support of the term
	 */
	public int getSupport() {
		return support;
	}

	/**
	 * @param support
	 */
	public void setSupport(int support) {
		this.support = support;
	}

	public boolean isChosen() {
		return chosen;
	}

	public void setChosen(boolean wasChosen) {
		this.chosen = wasChosen;
	}
	
}
