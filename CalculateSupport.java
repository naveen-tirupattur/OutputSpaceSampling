/**
 * Calculate Support for a term from vat of term
 */
package textmining.outputspace1;


import java.util.Iterator;
import java.util.Map;


/**
 * @author Naveen Tirupattur
 *
 */
public class CalculateSupport {
	
	/**
	 * @param : Map of VAT
	 * @return: Support of term
	 */
	public int calculate(Map<String, Integer> vat)
	{
		int support = 0;
		
		Iterator i = vat.entrySet().iterator();
		while(i.hasNext())
		{
			Map.Entry entries = (Map.Entry)i.next();
	        if((Integer)entries.getValue() > 0)
	        {	        	
	        	support++;
	        }
		}
		
		return support;
	}

}
