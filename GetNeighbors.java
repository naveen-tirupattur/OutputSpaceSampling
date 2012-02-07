/**
 * Class to return all neighbors for a term
 */
package textmining.outputspace1;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author Naveen Tirupattur
 *
 */
public class GetNeighbors {

	/**
	 * Get the neighbors for chosen term
	 * @param pattern
	 * @param termsList with level 1 patterns
	 * @return list of terms
	 */
	public TreeSet<Term> get(Term chosenTerm, HashSet<Term> level1termsSet, TreeSet<Term> alltermsSet, int minSup)
	{
		TreeSet<Term> neighborsList = new TreeSet<Term>(new TermComparator());				

		//Add the super pattern to neighbors list		
		neighborsList.addAll(getSuperPattern(chosenTerm, level1termsSet, alltermsSet,minSup));	

		//Add the sub pattern to neighbors list if chosen term is not from level 1
		if(chosenTerm.getPattern().size() != 1)
		{						
			neighborsList.addAll(getSubPattern(chosenTerm, level1termsSet, alltermsSet, minSup));
		}
		chosenTerm.setChosen(true);
		return neighborsList;
	}

	/**
	 * Generate sub pattern for chosen term
	 * @param chosenTerm
	 * @return sub pattern list
	 */
	public TreeSet<Term> getSubPattern(Term chosenTerm, HashSet<Term> level1termsSet, TreeSet<Term> alltermsSet, int minSup)
	{
		//System.out.println("Generating Sub Patterns");
		TreeSet<Term> list = new TreeSet<Term>(new TermComparator());
		CalculateSupport calSupport = new CalculateSupport();		

		//Remove a single term from chosen term
		for(Term t: level1termsSet)
		{
			Term newTerm = new Term();			

			//Get the intersection of chosen term and term from term list
			TreeSet<String> intersectionSet = new TreeSet<String>();
			intersectionSet.addAll(chosenTerm.getPattern());
			intersectionSet.retainAll(t.getPattern());

			if(intersectionSet.size() > 0)
			{
				//Create a new set to store the pattern
				TreeSet<String> newPatternSet = new TreeSet<String>();	

				//Flag to check if new term is already in frequent list
				boolean flag = false;

				//Add the pattern of chosen term to new pattern
				newPatternSet.addAll(chosenTerm.getPattern());

				//Remove a single term
				newPatternSet.removeAll(t.getPattern());

				//Set the new pattern to new term
				newTerm.setPattern(newPatternSet);			

				//Calculate VAT for this new pattern. 
				//This is tricky part. This is not similar to super pattern, where we already had the VAT for combining terms				
				//Check if new term exits in all terms set
				if(alltermsSet.contains((Term)newTerm))
				{
					for(Term trm:alltermsSet)
					{					
						if(trm.getPattern().equals(newTerm.getPattern()))
						{
							//System.out.println("Sub Pattern "+newPatternSet+" Found in All terms");													
							newTerm.setVat(trm.getVat());
							break;
						}
					}
				}else
				{
					//Set the VAT by intersection of individual terms
					newTerm.setVat(intersection(newTerm.getPattern(), alltermsSet));
				}				

				//Set the support for new term
				newTerm.setSupport(calSupport.calculate(newTerm.getVat()));
				
				//System.out.println("Sub Neighbors Pattern: "+newPatternSet+" with Support: "+calSupport.calculate(newTerm.getVat()));	

				if(newTerm.getSupport() >= minSup)				list.add(newTerm);

			}			
		}
		return list;
	}

	/**
	 * Generate the super pattern list for chosen term
	 * @param chosenTerm
	 * @param termsList
	 * @return super pattern list
	 */
	public TreeSet<Term> getSuperPattern(Term chosenTerm, HashSet<Term> level1termsSet, TreeSet<Term> alltermsSet, int minSup)
	{
		
		TreeSet<Term> list = new TreeSet<Term>(new TermComparator());
		CalculateSupport calSupport = new CalculateSupport();

		//Check if chosen term exists in all terms
		if(!alltermsSet.contains(chosenTerm))
		{
			//Get the VAT for chosen term.
			//Perform intersection of all the terms
			//System.out.println("Setting VAT for Chosen Term with pattern "+chosenTerm.getPattern()+" in Super Neighbor");
			chosenTerm.setVat(intersection(chosenTerm.getPattern(), alltermsSet));
		}


		//Pick one term from termslist
		for(Term t:level1termsSet)
		{
			//Get the intersection of chosen term and term from term list
			TreeSet<String> intersectionSet = new TreeSet<String>();
			intersectionSet.addAll(chosenTerm.getPattern());
			intersectionSet.retainAll(t.getPattern());

			//Check if there is a term in common, if no then			
			if(intersectionSet.size() == 0)
			{	
				//Create a new set to store the pattern
				TreeSet<String> newPatternSet = new TreeSet<String>();				

				newPatternSet.addAll(chosenTerm.getPattern());				
				newPatternSet.addAll(t.getPattern());				

				Term newTerm = new Term();

				//Set the pattern
				newTerm.setPattern(newPatternSet);

				/*
				 * Check if new pattern exists in all terms list
				 * If, yes get the VAT from all terms list
				 */
				if(alltermsSet.contains((Term)newTerm))
				{
					for(Term tempTerm:alltermsSet)
					{
						if(tempTerm.getPattern().equals(newTerm.getPattern()))
						{
							newTerm.setVat(tempTerm.getVat());
							//System.out.println("Super Pattern "+newPatternSet+" Found in All terms");
							break;
						}

					}
				}
				else
				{
					//Set the VAT 
					newTerm.setVat(intersection(chosenTerm.getVat(), t.getVat()));
				}
				
				//System.out.println("Super Neighbors Pattern: "+newPatternSet+" with Support: "+calSupport.calculate(newTerm.getVat()));			

				//Set the support for new term
				newTerm.setSupport(calSupport.calculate(newTerm.getVat()));

				//System.out.println("Support: "+newTerm.getSupport());
				if(newTerm.getSupport() >= minSup)list.add(newTerm);
			}
		}

		return list;
	}
	/**
	 * method to perform intersection on vat of 2 terms
	 * @param termA
	 * @param termB
	 * @return vat of new term
	 */
	public TreeMap<String,Integer> intersection(TreeMap<String,Integer> termAVAT, TreeMap<String,Integer> termBVAT)
	{
		TreeMap<String,Integer> newPatternMap = new TreeMap<String, Integer>();			

		//I made a big mistake here of assigning a refrence to intersectionset instead of using constructor
		//Get the intersection of keys
		Set<String> interSectionSet = new TreeSet<String>(termAVAT.keySet());
		interSectionSet.retainAll(termBVAT.keySet());

		//For the keys in intersection, get the min(value)
		Iterator i = interSectionSet.iterator();
		while(i.hasNext())
		{
			String key = (String)i.next();
			//Add the intersection key,min(value) to map
			newPatternMap.put(key, Math.min(termAVAT.get(key), termBVAT.get(key)));
		}

		return newPatternMap;
	}

	/**
	 * Intersection method overloaded when argument is pattern
	 * @param: Pattern
	 * @return vat of pattern
	 */
	public TreeMap<String,Integer> intersection(TreeSet<String> pattern, TreeSet<Term> alltermsSet)
	{
		TreeMap<String,Integer> patternMap = new TreeMap<String, Integer>();

		//System.out.println("Pattern "+pattern+" not found in Existing List, Calculating VAT for this pattern");

		Term[] tArray = new Term[pattern.size()];

		Iterator lit = alltermsSet.iterator();	

		String[] patterns = new String[pattern.size()];

		int i =0;
		Iterator it = pattern.iterator();
		while(it.hasNext())
		{
			patterns[i] = (String)it.next();			
			i++;
		}

		i = 0;
		while(lit.hasNext())
		{
			Term t = (Term)lit.next();

			for(int j=0;j<patterns.length;j++)
			{				
				if(t.getPattern().toString().replaceAll("[\\[|\\]]", "").equals(patterns[j]))
				{					
					if(i == patterns.length) break;					
					tArray[i] = t;					
					i++;					
				}

			}
		}

		patternMap = (TreeMap<String, Integer>)tArray[0].getVat();

		for(int j=1;j<tArray.length;j++)
		{
			//patternMap = intersection(tArray[0].getVat(), tArray[j].getVat());
			patternMap = intersection(patternMap, tArray[j].getVat());
		}		
		return patternMap;
	}

}
