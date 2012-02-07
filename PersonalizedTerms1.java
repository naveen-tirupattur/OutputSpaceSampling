/**
 * Class to provide a set of chosen terms based on user preferences
 */
package textmining.outputspace1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Naveen Tirupattur
 *
 */
public class PersonalizedTerms1 {

	/**
	 * Get the user chosen terms
	 * @param level1termsSet
	 * @return chosentermSet
	 */
	public HashSet<Term> getChosenTerms(HashSet<Term> level1termsSet)
	{
		/*
		 * Show all the patterns to user
		 * 
		 */
		HashSet<Term> chosenTerms = new HashSet<Term>();

		HashMap<String,Term> termsMap = new HashMap<String, Term>();

		char count='A';
		for(Term t:level1termsSet)
		{

			termsMap.put(String.valueOf(count), t);
			System.out.println("Choice: "+ count +" Pattern: "+t.getPattern().toString());
			count++;
		}

		/*
		 * Let user select the patterns he is interested in 
		 */
		String choice="";
		while(!choice.equals("-1"))
		{
			//prompt the user to enter their name
			System.out.print("Enter your Choice Or -1 to Quit: ");

			//  open up standard input
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));			

			//  read the username from the command-line; need to use try/catch with the
			//  readLine() method
			try {
				choice = br.readLine();
				if(!choice.equals("-1"))
				{					
					if(!termsMap.containsKey(choice)) System.out.println("Invalid choice.Try again!");
					else
					{
						Term term = termsMap.get(choice);						
						chosenTerms.add(term);
					}
				}

			} catch (IOException ioe) {
				System.out.println("IO error");
				System.exit(1);
			} catch(NullPointerException ne)
			{
				System.out.println("Invalid choice");
				System.exit(1);
			}			
		}
		for(Term e:chosenTerms)
		{
			System.out.println("Chosen Patterns: "+e.getPattern().toString());
		}
		return chosenTerms;
	}

}
