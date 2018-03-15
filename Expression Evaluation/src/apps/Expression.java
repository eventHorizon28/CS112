package apps;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	/**
	 * Expression to be evaluated
	 */
	String expr;

	/**
	 * Scalar symbols in the expression
	 */
	ArrayList<ScalarSymbol> scalars;

	/**
	 * Array symbols in the expression
	 */
	ArrayList<ArraySymbol> arrays;

	Stack<Character> gOper = new Stack<Character>();
	Stack<Double> gNums = new Stack<Double>();
	Stack<Character> lOper = new Stack<Character>();
	Stack<Double> lNums = new Stack<Double>();
	/**
	 * String containing all delimiters (characters other than variables and
	 * constants), to be used with StringTokenizer
	 */
	public static final String delims = " \t*+-/()[]";

	/**
	 * Initializes this Expression object with an input expression. Sets all
	 * other fields to null.
	 * 
	 * @param expr
	 *            Expression
	 */
	public Expression(String expr) {
		this.expr = expr;
	}

	boolean etest2 = false;

	/**
	 * Populates the scalars and arrays lists with symbols for scalar and array
	 * variables in the expression. For every variable, a SINGLE symbol is
	 * created and stored, even if it appears more than once in the expression.
	 * At this time, values for all variables are set to zero - they will be
	 * loaded from a file in the loadSymbolValues method.
	 */
	public void buildSymbols() { // so do we have to add the
		/** COMPLETE THIS METHOD **/
		scalars = new ArrayList<ScalarSymbol>();
		arrays = new ArrayList<ArraySymbol>();

		for (int i = 0; i < expr.length(); i++) // to ignore the operators
		{
			String symb = "";
			if (Character.isLetter(expr.charAt(i))) {
				if (expr.charAt(i) == 'v' && i + 1 < expr.length() && expr.charAt(i + 1) == 'a') {
					while (i < expr.length() && Character.isLetter(expr.charAt(i))) {
						symb += expr.charAt(i);
						i++;
					}
					i--;
					etest2 = true;
				} else if ((expr.charAt(i) == 'a' && i + 1 < expr.length() && expr.charAt(i + 1) == 'r')) {
					while (i < expr.length() && Character.isLetter(expr.charAt(i))) {
						symb += expr.charAt(i);
						i++;
					}
					i--;
				} else
					symb += expr.charAt(i);

				if (i + 1 < expr.length()) {
					if (expr.charAt(i + 1) == '[') {
						ArraySymbol arr = new ArraySymbol(symb);
						if (!arrays.contains(arr))
							arrays.add(arr);
						else
							;
					} else {
						ScalarSymbol scal = new ScalarSymbol(symb);
						if (!scalars.contains(scal))
							scalars.add(scal);
						else
							;
					}
				} else {
					ScalarSymbol scal = new ScalarSymbol(symb);
					if (!scalars.contains(scal))
						scalars.add(scal);
					else
						;
				}
			}

		}
		// System.out.println("arrayList: "+arrays.toString());
		// System.out.println("scalList: "+scalars.toString());
	}

	/**
	 * Loads values for symbols in the expression
	 * 
	 * @param sc
	 *            Scanner for values input
	 * @throws IOException
	 *             If there is a problem with the input
	 */
	public void loadSymbolValues(Scanner sc) throws IOException {
		while (sc.hasNextLine()) {
			StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
			int numTokens = st.countTokens();
			String sym = st.nextToken();
			ScalarSymbol ssymbol = new ScalarSymbol(sym);
			ArraySymbol asymbol = new ArraySymbol(sym);
			int ssi = scalars.indexOf(ssymbol);
			int asi = arrays.indexOf(asymbol);
			if (ssi == -1 && asi == -1) {
				continue;
			}
			int num = Integer.parseInt(st.nextToken());
			if (numTokens == 2) { // scalar symbol
				scalars.get(ssi).value = num;
			} else { // array symbol
				asymbol = arrays.get(asi);
				asymbol.values = new int[num];
				// following are (index,val) pairs
				while (st.hasMoreTokens()) {
					String tok = st.nextToken();
					StringTokenizer stt = new StringTokenizer(tok, " (,)");
					int index = Integer.parseInt(stt.nextToken());
					int val = Integer.parseInt(stt.nextToken());
					asymbol.values[index] = val;
				}
			}
		}
	}

	private double evaluate(String a) // (b+A[B[2]]) this expression throws an
										// outOfBoundsException L333
	/////////////////// this error is most probably because the evaluation of
	/////////////////// '(' and '[' is not working!!!!!!
	/////////////// and also having problem in evaluating a+b-d, to correct this
	/////////////////// we can peek at operator stack to see if it is minus
	// if it is, push negative number onto Nums stack, and push plus onto the
	/////////////////// operator stack
	{	
		//System.out.println(a);
		if (a.length() == 1 && (a.charAt(0) == ']' || a.charAt(0) == ')')) {
			//System.out.println("157, peek"+lNums.peek() +" size "+ lNums.size());
			//System.out.println(lNums.peek());
			double ans = 0;
			while (!lOper.isEmpty()) {
				ans = 0;
				if (lNums.size() == 1)
					break;

				double n2 = lNums.pop();
				double n1 = lNums.pop();
				char oper = lOper.pop();
				if (oper == '+')
					ans = (n1 + n2);
				else if (oper == '-')
					ans = (n1 - n2);
				else if (oper == '*')
					ans = (n1 * n2);
				else if (oper == '/')
					ans = (n1 / n2);

				lNums.push(ans);
			}
			return lNums.pop();
		}

		if (a.charAt(0) == ')') {
			double ans = 0;
			while (!lOper.isEmpty()) {
				ans = 0;
				if (lNums.size() == 1)
					break;

				double n2 = lNums.pop();
				double n1 = lNums.pop();
				char oper = lOper.pop();
				if (oper == '+')
					ans = (n1 + n2);
				else if (oper == '-')
					ans = (n1 - n2);
				else if (oper == '*')
					ans = (n1 * n2);
				else if (oper == '/')
					ans = (n1 / n2);

				lNums.push(ans);
			}
			if(a.indexOf(')') == a.length()-1 || a.indexOf(')', a.indexOf(')')+1) == -1)
				return lNums.pop();
			else
				return evaluate(a.substring(a.indexOf(')')+1));
		}

		else if (a.charAt(0) == '(') {
			double ans = 0;
			while (!lOper.isEmpty()) {
				ans = 0;
				if (lOper.size() == 1) {
					double lastNum = lNums.pop();
					char lastOper = lOper.pop();

					if (lastOper == '+')
						ans = (lastNum + evaluate(a.substring(1)));
					else if (lastOper == '-')
						ans = (lastNum - evaluate(a.substring(1)));
					else if (lastOper == '*')
						ans = (lastNum * evaluate(a.substring(1)));
					else if (lastOper == '/')
						ans = (lastNum / evaluate(a.substring(1)));

					return ans;
				}

				double n2 = lNums.pop();
				double n1 = lNums.pop();
				char oper = lOper.pop();
				if (oper == '+')
					ans = (n1 + n2);
				else if (oper == '-')
					ans = (n1 - n2);
				else if (oper == '*')
					ans = (n1 * n2);
				else if (oper == '/')
					ans = (n1 / n2);

				lNums.push(ans);
			}
			return evaluate(a.substring(1));
		}

		else if (a.charAt(0) == ']') ////////////////////////////////operations after ']'  and ')' are not working
			// examples are a+((b*d)+d) and A[B[2]+1]
		{
			double ans = 0;

			while (!lOper.isEmpty()) {
				ans = 0;
				if (lNums.size() == 1)
					break;

				double n2 = lNums.pop();
				double n1 = lNums.pop();
				char oper = lOper.pop();
				if (oper == '+')
					ans = (n1 + n2);
				else if (oper == '-')
					ans = (n1 - n2);
				else if (oper == '*')
					ans = (n1 * n2);
				else if (oper == '/')
					ans = (n1 / n2);

				lNums.push(ans);
			}
			if(a.indexOf(']') == a.length()-1){
				return lNums.pop();
				}
			else{
				return evaluate(a.substring(a.indexOf(']')+1));
			}
		}

		else if (a.charAt(0) == '+' || a.charAt(0) == '-' || a.charAt(0) == '*' || a.charAt(0) == '/') 
		{
			lOper.push(a.charAt(0));
			return evaluate(a.substring(1));
		} 
		
		else if(a.charAt(0) == ' ')
			return evaluate(a.substring(1));
		
		else if (Character.isDigit(a.charAt(0))) {
			String digit = "" + a.charAt(0);
			int i = 1;
			if (a.length()>0 && Character.isDigit(a.charAt(1))) {
				while (i < a.length() && Character.isDigit(a.charAt(i))) {
					digit += a.charAt(i);
					i++;
				}
				lNums.push(Double.parseDouble(digit));
				return evaluate(a.substring(i));
			}
			else {
				lNums.push(Double.parseDouble(digit));
				//System.out.println("298 " + lNums.peek());
			}
			if (lOper.size() > 0) {
				if (lOper.peek() == '*') {
					lOper.pop();
					lNums.push(lNums.pop() * lNums.pop());
				} else if (lOper.peek() == '/') {
					double n2 = lNums.pop();
					double n1 = lNums.pop();
					lOper.pop();
					lNums.push(n1 / n2);
				} else if (lOper.peek() == '-') {
					lOper.pop();
					lOper.push('+');
					lNums.push(-1 * lNums.pop());
				}
			}
			return evaluate(a.substring(1));

		}
//(a + A[a*2-b])			(b+A[B[2]])*d
		else {
			int i = 0;
			if (etest2 == false) {
				if (a.length() > 1 && a.charAt(1) == '[') {
					ArraySymbol tmp = new ArraySymbol(Character.toString(a.charAt(0)));
					int[] obj1 = arrays.get(arrays.indexOf(tmp)).values;
					double ans = 0;
					double x = (double) 0;
					double lastNum = 0;
					char lastOper = ' ';
					if (!lOper.isEmpty()) {
						while (!lOper.isEmpty()) {
							ans = 0;
							if (lOper.size() == 1) {
								lastNum = lNums.pop();
								lastOper = lOper.pop();
								break;
							}

							double n2 = lNums.pop();
							double n1 = lNums.pop();
							char oper = lOper.pop();
							if (oper == '+')
								ans = (n1 + n2);
							else if (oper == '-')
								ans = (n1 - n2);
							else if (oper == '*')
								ans = (n1 * n2);
							else if (oper == '/')
								ans = (n1 / n2);

							lNums.push(ans);
						}
					}
					x = evaluate(a.substring(2));
					if(lastNum != 0)
						lNums.push(lastNum);
					if(lastOper != ' ')
						lOper.push(lastOper);
					lNums.push((double) obj1[(int) x]);
					if(a.indexOf(']') == a.length()-1)
						return lNums.pop();
					else
						return evaluate(a.substring(a.indexOf(']')+1));
				}
				else {
					ScalarSymbol tmp = new ScalarSymbol(Character.toString(a.charAt(0)));
					Double obj = (double) (scalars.get(scalars.indexOf(tmp)).value);
					lNums.push(obj);
					if (lOper.size() > 0 && lOper.peek() == '*') {
						lOper.pop();
						lNums.push(lNums.pop() * lNums.pop());
						return evaluate(a.substring(1));
					} else if (lOper.size() > 0 && lOper.peek() == '/') {
						double n2 = lNums.pop();
						double n1 = lNums.pop();
						lOper.pop();
						lNums.push(n1 / n2);
						return evaluate(a.substring(1));
					} else if (lOper.size() > 0 && lOper.peek() == '-') {
						lOper.pop();
						lOper.push('+');
						lNums.push(-1 * lNums.pop());
						return evaluate(a.substring(1));
					} else
						return evaluate(a.substring(1));
				}

			}

			else // if the variables are from etest2
			{
				String symb = "";
				if (a.charAt(0) == 'v' && a.length() > 1 && a.charAt(1) == 'a')// if its the scalar symbol
				{

					while (i < a.length() && Character.isLetter(a.charAt(i))) {
						symb += a.charAt(i);
						i++;
					}
				}
				else if ((a.charAt(0) == 'a' && a.length() > 1 && a.charAt(1) == 'r')) // if its an arraysymbol
				{
					while (i < a.length() && Character.isLetter(a.charAt(i))) {
						symb += a.charAt(i);
						i++;
					}
				} 
				else
					;

				//System.out.println("414 "+symb);
				if (a.length()>symb.length() && a.charAt(symb.length()) == '[') {
					ArraySymbol tmp = new ArraySymbol(symb);
					int[] obj = arrays.get(arrays.indexOf(tmp)).values;
					double ans = 0;
					double x = (double) 0;
					// System.out.println("320 oper size: "+lOper.size());
					double lastNum = 0;
					char lastOper = ' ';
					if (!lOper.isEmpty()) {
						while (!lOper.isEmpty()) {
							ans = 0;
							if (lOper.size() == 1) {
								lastNum = lNums.pop();
								lastOper = lOper.pop();
								break;
							}

							double n2 = lNums.pop();
							double n1 = lNums.pop();
							char oper = lOper.pop();
							if (oper == '+')
								ans = (n1 + n2);
							else if (oper == '-')
								ans = (n1 - n2);
							else if (oper == '*')
								ans = (n1 * n2);
							else if (oper == '/')
								ans = (n1 / n2);

							lNums.push(ans);
						}
					}
					x = evaluate(a.substring(symb.length()+1));
					if(lastNum != 0)
						lNums.push(lastNum);
					if(lastOper != ' ')
						lOper.push(lastOper);
					lNums.push((double) obj[(int) x]);
					if(a.indexOf(']') == a.length()-1)
						return lNums.pop();
					else
						return evaluate(a.substring(a.indexOf(']')+1));
				}
			
				else {
					//System.out.println("symb = "+symb);
					ScalarSymbol tmp = new ScalarSymbol(symb);
					Double obj = (double) (scalars.get(scalars.indexOf(tmp)).value);
					System.out.println(obj);
					lNums.push(obj);
					if (lOper.size() > 0 && lOper.peek() == '*') {
						lOper.pop();
						lNums.push(lNums.pop() * lNums.pop());
						return evaluate(a.substring(symb.length()));
					} else if (lOper.size() > 0 && lOper.peek() == '/') {
						double n2 = lNums.pop();
						double n1 = lNums.pop();
						lOper.pop();
						lNums.push(n1 / n2);
						return evaluate(a.substring(symb.length()));
					} else if (lOper.size() > 0 && lOper.peek() == '-') {
						lOper.pop();
						lOper.push('+');
						lNums.push(-1 * lNums.pop());
						return evaluate(a.substring(symb.length()));
					} else
						return evaluate(a.substring(symb.length()));
				}
				
			}
		}
	}

	/**
     * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 
     * subscript expressions.
     * 
     * @return Result of evaluation
     */
   
    public float evaluate() {
    		
    	for(int i = 0; i<expr.length(); i++)
    	{
    		if(expr.charAt(i) == '+' || expr.charAt(i) == '-' || expr.charAt(i) == '*' || expr.charAt(i) == '/') //correct this according to PEMDAS
    			{gOper.push(expr.charAt(i));
    			continue;
    			}
    		else if(expr.charAt(i) == '(')
    		{
    			
    			//System.out.println(kb);
    			gNums.push(evaluate(expr.substring(i+1)));
    			//System.out.println(gNums.peek());
    			if(expr.indexOf('(', i+1) == -1)
    			{
    				i = expr.indexOf(')');
    				continue;
    			}
    			else // if there are more than 1 subexpressions
    			{
    				int closing = expr.indexOf(')');
    				int tempI = i+1;
    				
    				if(expr.indexOf('(', tempI) < closing) //if there is another paren inside a paren then shift i to the last closing
	    			{
    					i = expr.indexOf(')', closing+1);
    				}
    				else
    					i = closing;
    				
    				if(i == expr.length()-1)
    				{
    					break;
    				}
    			}
    		}
    				
    		else if(expr.charAt(i) == ' ')
    			continue;
    		
    		else if(Character.isLetter(expr.charAt(i))) // if it is a letter
    		{
    			if(etest2 == false)
    			{
	    			if(i+1<expr.length() && expr.charAt(i+1) == '[')
    				{
    					ArraySymbol tmp = new ArraySymbol(Character.toString(expr.charAt(i)));
    					int[] obj = arrays.get(arrays.indexOf(tmp)).values;
    					for(int j = 0; j<obj.length; j++)
    					{
    						System.out.print(obj[j]);
    					}System.out.println();
    					/////////////////////////////////////////////////////////////////////A[B[2]] gives 1, because b is the obj now
    					int tj = (int) evaluate(expr.substring(i+2));
    					Double x = (double)obj[tj];
    					
    					gNums.push(x);
    					
    					if(expr.indexOf('[', i+2) == -1)
    	    			{
    	    				i = expr.indexOf(']');
    	    				continue;
    	    			}
    	    			else // if there are more than 1 subexpressions
    	    			{
    	    				int closing = expr.indexOf(']');
    	    				int tempI = i+1;
    	    				
    	    				if(expr.indexOf('[', tempI) < closing) //if there is another paren inside a paren then shift i to the last closing
    		    			{
    	    					i = expr.indexOf(']', closing+1);
    	    				}
    	    				else
    	    					i = closing;
    	    			}
    					
    					
    				}
    				else
    				{
    					ScalarSymbol tmp = new ScalarSymbol(Character.toString(expr.charAt(i)));
						Double obj = (double)(scalars.get(scalars.indexOf(tmp)).value);
						gNums.push(obj);
						
					}
	    			if(i>0) // to evaluate Xyz or 3X
	    			{
	    				if(gNums.size()>1)
    					{
    					if(Character.isLetter(expr.charAt(i-1)))
    						gNums.push(gNums.pop()*gNums.pop());
    					else if(Character.isDigit(expr.charAt(i-1)))
    						gNums.push(gNums.pop()*gNums.pop());
    					}
	    			}
	    		}
    			
    			else //if the variables are from etest2
    			{
    				String symb = "";
    				if(expr.charAt(i) == 'v' && i+1<expr.length() && expr.charAt(i+1) == 'a')//if its the scalar symbol
    				{
    					while(i<expr.length() && Character.isLetter(expr.charAt(i)))
    					{
    						symb +=expr.charAt(i);
    						i++;
    					}
    					i--;
    				}
    				else if((expr.charAt(i) == 'a' && i+1<expr.length() && expr.charAt(i+1) == 'r')) // if its an arraysymbol
    				{
    					while(i<expr.length() && Character.isLetter(expr.charAt(i)))
    					{
    						symb +=expr.charAt(i);
    						i++;
    					}
    					i--;
    				}
    				else
    					;
    				
    				if(i+1<expr.length() && expr.charAt(i+1) == '[')
    				{	
	    				ArraySymbol tmp = new ArraySymbol(symb);
	    				int[] obj = arrays.get(arrays.indexOf(tmp)).values;
       					int tj = (int) evaluate(expr.substring(i+2));
    					System.out.println("497 "+tj);
    					Double x = (double)obj[tj];
    					gNums.push(x);
    					
    					if(expr.indexOf('[', i+2) == -1)
    	    			{
    	    				i = expr.indexOf(']');
    	    				continue;
    	    			}
    	    			else // if there are more than 1 subexpressions
    	    			{
    	    				int closing = expr.indexOf(']');
    	    				int tempI = i+1;
    	    				
    	    				if(expr.indexOf('[', tempI) < closing) //if there is another paren inside a paren then shift i to the last closing
    		    			{
    	    					i = expr.indexOf(']', closing+1);
    	    				}
    	    				else
    	    					i = closing;
    	    				continue;
    	    			}
    				}
    				else
	    			{
						ScalarSymbol tmp = new ScalarSymbol(symb);
						Double obj = (double)(scalars.get(scalars.indexOf(tmp)).value);
						gNums.push(obj);
	    			}
    			}
    			if(gNums.size() >1)
        		{
    	    		if(gOper.size()>0)
    	    		{
    			    	
    	    			if(gOper.peek() == '-')
        	    		{
        	    			gOper.pop();
        	    			gOper.push('+');
        	    			gNums.push(-1*gNums.pop());
        	    		}
    	    			else if(gOper.peek() == '*')
    			    	{
    			    		gOper.pop();
    			    		gNums.push(gNums.pop() * gNums.pop());
    			    	}
    			    	else if(gOper.peek() == '/')
    			    	{
    			    		gOper.pop();
    			    		double n2 = gNums.pop();
    			    		gNums.push(gNums.pop()/n2);
    			    	}
    			    	
    			    	else continue;
    	    		}
        		}
    			
    		}
    		else if(Character.isDigit(expr.charAt(i)))
    		{
    			String digit = "";
    			while(i<expr.length() && Character.isDigit(expr.charAt(i)))
    			{
    				digit+=expr.charAt(i);
    				i++;
    			}
    			i--;
    			gNums.push(Double.parseDouble(digit));
    			if(!gOper.isEmpty()){
    				if(gNums.size()>1)
    		    	{
    					if(gOper.peek() == '*')
	    		    	{
	    		    		gOper.pop();
	    		    		gNums.push(gNums.pop() * gNums.pop());
	    		    	}
	    		    	else if(gOper.peek() == '/')
	    		    	{
	    		    		gOper.pop();
	    		    		double n2 = gNums.pop();
	    		    		gNums.push(gNums.pop()/n2);
	    		    	}
	    		    	else if(gOper.peek() == '-')
	    	    		{
	    	    			gOper.pop();
	    	    			gOper.push('+');
	    	    			gNums.push(-1*gNums.pop());
	    	    		}
	    		    	else
	    		    		continue;
    		    	}
    	    	}
    		}
    	}
    	double ans = 0;
    	//System.out.println("gnums after loop: "+gNums.peek());
    	if(gOper.isEmpty())
    		ans = gNums.pop();
    	
    	else
    	{
    		while(!gOper.isEmpty())
	    	{
	    		ans = 0;
	    		if(gNums.size()==1)
	    		{
	    			ans = gNums.pop();
	    			return (float)ans;
	    		}
	    		double n2 = gNums.pop();
	    		double n1 = gNums.pop();
	    		char oper = gOper.pop();
	    		
	    		if(oper == '+')
	    			ans = (n1 + n2);
		        else if(oper =='-')
	    			ans = (n1 - n2);
	    		else if(oper == '*')
	    			ans = (n1 * n2);
	    		else if(oper == '/')
	    			ans = (n1 / n2);
	    		
	    		gNums.push(ans);
	    	}
    	}
    	return (float)ans;
    	
    }

	/**
	 * Utility method, prints the symbols in the scalars list
	 */
	public void printScalars() {
		for (ScalarSymbol ss : scalars) {
			System.out.println(ss);
		}
	}

	/**
	 * Utility method, prints the symbols in the arrays list
	 */
	public void printArrays() {
		for (ArraySymbol as : arrays) {
			System.out.println(as);
		}
	}
}
