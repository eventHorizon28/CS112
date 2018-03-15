package solitaire;

import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

/**
 * This class implements a simplified version of Bruce Schneier's Solitaire Encryption algorithm.
 * 
 * @author RU NB CS112
 */
public class Solitaire {
	
	/**
	 * Circular linked list that is the deck of cards for encryption
	 */
	CardNode deckRear;
	
	
	/**
	 * Makes a shuffled deck of cards for encryption. The deck is stored in a circular
	 * linked list, whose last node is pointed to by the field deckRear
	 */
	public void makeDeck() {
		// start with an array of 1..28 for easy shuffling
		int[] cardValues = new int[28];
		// assign values from 1 to 28
		for (int i=0; i < cardValues.length; i++) {
			cardValues[i] = i+1;
		}
		
		// shuffle the cards
		Random randgen = new Random();
 	        for (int i = 0; i < cardValues.length; i++) {
	            int other = randgen.nextInt(28);
	            int temp = cardValues[i];
	            cardValues[i] = cardValues[other];
	            cardValues[other] = temp;
	        }
	     
	    // create a circular linked list from this deck and make deckRear point to its last node
	    CardNode cn = new CardNode();
	    cn.cardValue = cardValues[0];
	    cn.next = cn;
	    deckRear = cn;
	    for (int i=1; i < cardValues.length; i++) {
	    	cn = new CardNode();
	    	cn.cardValue = cardValues[i];
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
	    }
	}
	
	/**
	 * Makes a circular linked list deck out of values read from scanner.
	 */
	public void makeDeck(Scanner scanner) 
	throws IOException {
		CardNode cn = null;
		if (scanner.hasNextInt()) {
			cn = new CardNode();
		    cn.cardValue = scanner.nextInt();
		    cn.next = cn;
		    deckRear = cn;
		}
		while (scanner.hasNextInt()) {
			cn = new CardNode();
	    	cn.cardValue = scanner.nextInt();
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
		}
		printList(deckRear);
	}
	
	/**
	 * Implements Step 1 - Joker A - on the deck.
	 */
	void jokerA() {
		
	//changing the card values not nodes	
		CardNode j1 = new CardNode();
		if(deckRear.cardValue == 27)
		{
			for(CardNode tmp = deckRear.next; tmp != deckRear; tmp = tmp.next)
			{
				if(tmp.next.cardValue == 27)
				{
					j1 = tmp.next;
					tmp.next = deckRear.next;
					deckRear.next = deckRear.next.next;
					tmp.next.next = deckRear;
					deckRear = tmp.next;
					break;
				}
			}
		}
		else
		{
			for(CardNode tmp = deckRear; tmp.next != deckRear; tmp = tmp.next)
			{
				if(tmp.next.cardValue == 27)
				{
					if(tmp.next.next.cardValue == deckRear.cardValue)
					{
						j1 = tmp.next;
						tmp.next = tmp.next.next;
						j1.next = tmp.next.next;
						tmp.next.next = j1;
						deckRear = j1;
						break;
					}
					j1 = tmp.next;
					tmp.next = tmp.next.next;
					j1.next = tmp.next.next;
					tmp.next.next = j1;
					break;
				}
			}
		}
		//printList(deckRear);

	}
	
	/**
	 * Implements Step 2 - Joker B - on the deck.
	 */
	void jokerB() {
		
		CardNode j2 = deckRear.next;
		CardNode j2prev = deckRear;
		
		if(deckRear.cardValue == 28)
		{
			for(CardNode tmp = deckRear.next; tmp!=deckRear; tmp = tmp.next)
			{
				if(tmp.next.cardValue == 28)
				{
					tmp.next = deckRear.next;
					deckRear.next = deckRear.next.next.next;
					tmp.next.next.next = deckRear;
					deckRear = tmp.next;
				}
			}
		}
				
		else if(deckRear.next.cardValue == 28)
		{
			j2 = deckRear.next;
			j2prev = deckRear;
			
			deckRear.next = j2.next;
			j2.next = deckRear.next.next.next;
			deckRear.next.next.next = j2;
		}
		
		else
		{
			for(j2prev = deckRear.next; j2prev.next != deckRear; j2prev = j2prev.next)
			{
				if(j2prev.next.cardValue == 28)
				{
					j2 = j2prev.next;
					break;
				}
			}
			if(j2.next.cardValue == deckRear.cardValue)
			{
				j2prev.next = deckRear;
				j2.next = deckRear.next.next;
				deckRear.next.next = j2;
				deckRear = deckRear.next;
			}
			
			else if(j2.next.next.cardValue == deckRear.cardValue )
			{
				j2prev.next = j2.next;
				j2.next = deckRear.next;
				deckRear.next = j2;
				deckRear = j2;
			}
			else
			{
				j2prev.next = j2.next;
				j2.next = j2prev.next.next.next;
				j2prev.next.next.next = j2;
			}
			
		}
		//printList(deckRear);
		/*if(deckRear.cardValue == 28)
		{
			for(CardNode tmp = deckRear.next; tmp != deckRear; tmp = tmp.next)
			{
				if(tmp.next.cardValue == 28)
				{
					tmp.next = deckRear.next;
					deckRear.next = tmp.next.next.next;
					tmp.next.next.next = deckRear;
					deckRear = tmp.next.next;
					break;
				}
			}
		}
		else if(deckRear.next.cardValue == 28)
		{
			CardNode j2 = deckRear.next;
			deckRear.next = j2.next;
			j2.next = j2.next.next.next;
			deckRear.next.next.next = j2;
			
		}
		else
		{
			CardNode j2 = new CardNode();
			CardNode tmp = new CardNode();
			for(tmp = deckRear.next; tmp.next != deckRear; tmp = tmp.next)
			{
				if(tmp.next.cardValue == 28)
				{
					j2 = tmp.next;
					break;
				}
			}
			if(j2.next.next == deckRear)
			{
				tmp.next = tmp.next.next;
				j2.next = tmp.next.next.next;
				tmp.next.next.next = j2;
				deckRear = j2;
			}
			
			else if(j2.next == deckRear)
			{
				tmp.next = tmp.next.next;
				j2.next = tmp.next.next.next;
				tmp.next.next.next = j2;
				deckRear = tmp.next.next;
			}
			else
			{
				tmp.next = tmp.next.next;
				j2.next = tmp.next.next.next;
				tmp.next.next.next = j2;
			}
		}*/
	}
	
	/**
	 * Implements Step 3 - Triple Cut - on the deck.
	 */
	void tripleCut() {
		CardNode j1 = new CardNode();
		
		CardNode j2 = new CardNode();
		CardNode j1prev = new CardNode();
		CardNode j2next = new CardNode();
		
		if(deckRear.next.cardValue == 27 ||deckRear.next.cardValue == 28) // checking if any joker is at the first place
		{
			j1 = deckRear.next; //j1 is the first joker
			j1prev = deckRear;
		}
		else
		{
			for(j1prev = deckRear.next; j1prev != deckRear; j1prev = j1prev.next) //going through the deck to find the first joker
			{
				if(j1prev.next.cardValue == 28 || j1prev.next.cardValue == 27)
				{
					j1 = j1prev.next;
					break;
				}
			}
		}
		
		for(j2 = j1.next; j2!=deckRear.next; j2 = j2.next) //going from the firstjoker.next to deck rear to find the second joker
		{
			if((j2.cardValue == 27 || j2.cardValue == 28))
			{
				j2next = j2.next;
				break;
			}
		}
		if(deckRear.next.cardValue == j1.cardValue && deckRear.cardValue == j2.cardValue)
		{
			;
		}
			
		else if(deckRear.next.cardValue == j1.cardValue)
		{
			deckRear = j2;
		}
		
		else if(deckRear.cardValue == j2.cardValue)
		{
			deckRear = j1prev;
		}
		
		else{
			//rearrangement starts here
			
			j2.next = deckRear.next;
			deckRear.next = j1;
			j1prev.next = j2next;
			deckRear = j1prev;
		}
	}
	
	/**
	 * Implements Step 4 - Count Cut - on the deck.
	 */
	void countCut() {
		CardNode a = new CardNode();			//a is the node right before deckRear
		for(a = deckRear.next; a.next != deckRear; a = a.next)
		{
			;
		}

		int ift8 = deckRear.cardValue;
		if(ift8 == 28)
			ift8 = 27;
		
		CardNode b = a.next;					//b is the node that is deckRear.cardvalue from the head
		for(int i = 0; i <ift8; ++i)
		{
			b = b.next;
		}
		
		a.next = deckRear.next;
		deckRear.next = b.next;
		b.next = deckRear;
	}
	
	/**
	 * Gets a key. Calls the four steps - Joker A, Joker B, Triple Cut, Count Cut, then
	 * counts down based on the value of the first card and extracts the next card value 
	 * as key. But if that value is 27 or 28, repeats the whole process (Joker A through Count Cut)
	 * on the latest (current) deck, until a value less than or equal to 26 is found, which is then returned.
	 * 
	 * @return Key between 1 and 26
	 */
	int getKey() {
		
		int ift8 = deckRear.next.cardValue;
		if(ift8 == 28)
		{
			ift8 = 27; //to count 28 as 27
		}
		
		CardNode a = deckRear.next;
		
		for(int i = 0; i<ift8; ++i)
		{
			a = a.next; //finding the card on that count
		}
		int isjoker = a.cardValue;
		while(isjoker == 27 || isjoker == 28)
		{
			jokerA();
			jokerB();
			tripleCut();
			countCut();
			isjoker = getKey();
		}
		return isjoker;
		
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE

	}
	
	/**
	 * Utility method that prints a circular linked list, given its rear pointer
	 * 
	 * @param rear Rear pointer
	 */
	private static void printList(CardNode rear) {
		if (rear == null) { 
			return;
		}
		System.out.print(rear.next.cardValue);
		CardNode ptr = rear.next;
		do {
			ptr = ptr.next;
			System.out.print("," + ptr.cardValue);
		} while (ptr != rear);
		System.out.println("\n");
	}


	/**
	 * Encrypts a message, ignores all characters except upper case letters
	 * 
	 * @param message Message to be encrypted
	 * @return Encrypted message, a sequence of upper case letters only
	 */
	public String encrypt(String message) {	
		
		String en = "";
		
		
		for(int i = 0; i<message.length(); ++i)
		{
			Character a = message.charAt(i);
			int idk = 0;
			if(Character.isLetter(a))
			{
				//printList(deckRear);
				jokerA();
				//printList(deckRear);
				jokerB();
				//printList(deckRear);
				tripleCut();
				//printList(deckRear);
				countCut();
				//printList(deckRear);
				int key = getKey();
				//System.out.println(key + " ");
				
				a = Character.toUpperCase(a);
				idk = (int)((char)a -'A'+1);
				//System.out.println(idk);
				idk = (int)(idk + key);
				
				while(idk>26)
				{
					idk = (int)(idk - 26);
				}
				idk = (int)(idk + 'A' -1);
				a = (char)idk;
				en +=a;
				
			}
			else
				continue;
		}
		
		//System.out.println();
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
	    return en;
	}
	
	/**
	 * Decrypts a message, which consists of upper case letters only
	 * 
	 * @param message Message to be decrypted
	 * @return Decrypted message, a sequence of upper case letters only
	 */
	public String decrypt(String message) {	
		
		String en = "";
		
		for(int i = 0; i<message.length(); ++i)
		{
			int key = 0;
			Character a = message.charAt(i);
			int idk = 0;
			if(Character.isLetter(a))
			{
			//printList(deckRear);
			jokerA();
			//printList(deckRear);
			jokerB();
			//printList(deckRear);
			tripleCut();
			//printList(deckRear);
			countCut();
			//printList(deckRear);
			key = getKey();
			//System.out.print(key + " ");
			}
			else continue;
			//a = Character.toUpperCase(a); since all are in caps **also no if-else since all are letters
			
			a = Character.toUpperCase(a);

			idk = (int)((char)a -'A'+1);
			//System.out.println(idk);
			idk = (int)(idk - key);
			
			while(idk<1)
			{
				idk = (int)(idk + 26);
			}
			idk = (int)(idk + 'A' -1);
			a = (char)idk;
			en +=a;
		}
		
		//System.out.println();
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
	    return en;
	    // THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
	}
}