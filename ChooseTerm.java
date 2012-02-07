/**
 * Class to choose a term randomly with uniform probability
 */
package textmining.outputspace1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

/**
 * @author Naveen Tirupattur
 *
 */
public class ChooseTerm {

	/**
	 * @param: List of terms to choose from	 * 
	 * @return: Term with support > minsup
	 */
	public Term getTerm(Collection<Term> termsList)
	{		
		Term randomTerm = null;
		
		/*Collections.shuffle((ArrayList<Term>)termsList);

		for (Term t : termsList) {
			    randomTerm = t;
		        break;
		    }*/

		//Using Random()
		int size = termsList.size();
		int item = new Random().nextInt(size); 
		int i = 0;
		for(Term t : termsList)
		{
			if (i == item%size)		    		       
				randomTerm = t;
			i = i + 1;


		}		
		return randomTerm;

	}
}
