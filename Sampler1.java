package textmining.outputspace1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * 
 * @author Naveen Tirupattur
 * Main class for output space sampling
 * 1. Read the dataset
 * 2. Perform random walk
 * 3. Display the samples
 *
 */
public class Sampler1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		//Take the minimum support
		int minsup = 2;

		//Read the data from dataset
		ReadData reader = new ReadData();

		/*//Return a list of terms of level 1 whose support is greater than minimum support
		//HashSet<Term> level1termsSet = reader.readDataSet(".\\Data\\SampleData.txt", minsup );

		TreeSet<String> patternsSet = reader.returnSet(".\\Data\\Terms.txt");
		TreeSet<String> documentIdsSet = reader.returnSet(".\\Data\\DocumentIds.txt");

		HashSet<Term> level1termsSet = reader.readDocuments(patternsSet,documentIdsSet, "C:\\Documents and Settings\\Administrator\\workspace\\TestProject\\Abst\\", minsup);*/
		//HashSet<Term> level1termsSet = reader.readDocuments(patternsSet,documentIdsSet, "C:\\Documents and Settings\\Administrator\\workspace\\IncrementalMining\\Abstracts\\", minsup);		
		//HashSet<Term> level1termsSet = reader.readDocuments(patternsSet,documentIdsSet, "C:\\Documents and Settings\\Administrator\\workspace\\TestProject\\Abstracts\\Schizophrenia\\", minsup);

		//System.out.println("Time taken for reading documents: "+(System.currentTimeMillis()-startTime));	
		
		//Read level 1 terms from data file		
		HashSet<Term> level1termsSet = reader.readTermsSetFromFile(".\\Data\\TermsSet.txt", minsup);
		
		
		
		//Get the terms chosen by the user
		PersonalizedTerms1 personalizer = new PersonalizedTerms1();
		HashSet<Term> chosentermsSet =  personalizer.getChosenTerms(level1termsSet);
		
		if(chosentermsSet.size() == 0)
		{	
			System.out.println("You have not chosen any pattern. Hence all patterns will be considered");
			chosentermsSet = new HashSet<Term>(level1termsSet);
		}	
		
		long startTime = System.currentTimeMillis();
		
		/*//Replace the term frequencies in datafile with TF-IDF scores
		level1termsSet = reader.getTFIDF(level1termsSet, documentIdsSet.size());*/	
		/*startTime = System.currentTimeMillis();*/
		
		//Add level 1 terms to all terms set
		TreeSet<Term> alltermsSet = new TreeSet<Term>(new TermComparator());
		alltermsSet.addAll(chosentermsSet);

		//Add level 1 terms to frequent terms set
		TreeSet<Term> frequentTermsSet = new TreeSet<Term>(new TermComparator());	
		frequentTermsSet.addAll(chosentermsSet);

		//If user selects only one pattern, display the pattern and exit
		if(chosentermsSet.size() == 1)
		{
			System.out.println("You have selected only one pattern");
			System.exit(1);
			
		}
		
		CalculateSupport calc = new CalculateSupport();	

		TreeSet<Term> neighborsList = new TreeSet<Term>(new TermComparator());

		for(int i=0;i<50;i++)
		{
			//Choose a term from level 1 and start random walk
			ChooseTerm choose = new ChooseTerm();
			Term chosenTerm = new Term();

			if(neighborsList.size() != 0)
				chosenTerm = choose.getTerm(neighborsList);
			else
				chosenTerm = choose.getTerm(chosentermsSet);

						
			//Code to chose patterns with support > minsup and which were not chosen earlier
			int counter = 0;			
			//Choose a term again if chosen term is infrequent
			while((calc.calculate(chosenTerm.getVat()) < minsup) || chosenTerm.isChosen())
			{
				if(counter == 10) break;
				counter++;
				//System.out.println("Choosing Term Again");
				chosenTerm = choose.getTerm(neighborsList);
			}			
			
			/*//Show the chosen term
			System.out.println("============================================");
			System.out.println("Chosen Term: "+chosenTerm.getPattern()+" With Suppport: "+calc.calculate(chosenTerm.getVat()));
			System.out.println("============================================");*/
			
			
			//Get Neighbors for the term
			GetNeighbors getN = new GetNeighbors();
			neighborsList = getN.get(chosenTerm, chosentermsSet, alltermsSet,minsup);

			//Add the neighbors to all terms
			alltermsSet.addAll(neighborsList);

			frequentTermsSet.addAll(neighborsList);

			//Add the neighbors to chosen term
			chosenTerm.setNeighbors(neighborsList);									
		}
		writeTermsSetToFile(frequentTermsSet,".\\Data\\FrequentItemSets.txt");		
		
		
		System.out.println("Time Taken For Finding Frequent Itemsets: "+(System.currentTimeMillis()-startTime));
	}
	/**	
	 * Write Frequent Terms to a file 
	 * @param Set of Frequent Terms
	 * @return void
	 */
	public static void writeTermsSetToFile(TreeSet<Term> termsSet, String fileName)
	{	

		CalculateSupport calc = new CalculateSupport();
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
			//Write the map to a file
			for(Term t:termsSet)
			{
				writer.write(t.getPattern()+" "+calc.calculate(t.getVat()));
				writer.newLine();
			}			
			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


