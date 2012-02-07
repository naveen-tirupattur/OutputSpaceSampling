/**
 * Comparator Class to Compare 2 terms
 */
package textmining.outputspace1;

import java.util.Comparator;

/**
 * @author Naveen Tirupattur
 *
 */
public class TermComparator implements Comparator<Term> {

	/**
	 * @param: Two Terms
	 * @return: integer value which tells if two terms are same or not
	 */
	public int compare(Term o1, Term o2) {		
		// TODO Auto-generated method stub
		//Compare the pattern of two terms
		
		/*String o1Pattern = o1.getPattern().toString();
		String o2Pattern = o2.getPattern().toString();
		int c = o1Pattern.length() - o2Pattern.length();
        if (c == 0)
          c = o1Pattern.compareToIgnoreCase(o2Pattern);
        System.out.println("Length: "+c);
        return c;
        return (o1.hashCode() - o2.hashCode());*/
       
		return o1.getPattern().toString().compareTo(o2.getPattern().toString());
		//return 0;
	}

}
