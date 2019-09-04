package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	expr=expr.trim();
    	for(int i=0;i<expr.length();i++)
    	{
    		if(expr.charAt(i)=='[')
    		{
    			boolean alreadyThere=false;
    			int arrayNameLength=0;
    			int k=i-1;
    			while(Character.isLetter(expr.charAt(k)))
    			{
    				arrayNameLength++;
    				k--;
    				if(k==-1)
    				{
    					break;
    				}
    			}
    			for(int r=0;r<arrays.size();r++)
    			{
    				
    				if(arrays.get(r).name.equals(expr.substring(k+1, k+1+arrayNameLength)))
    				{
    					alreadyThere=true;
    					break;
    				}
    			}
    			if(!alreadyThere)
    			{
    				
    				arrays.add(new Array(expr.substring(k+1, k+1+arrayNameLength)));
    			}
    		}
    		else if(Character.isLetter(expr.charAt(i)))
    		{
    			boolean last=false;
    			boolean alreadyThere=false;
    			int varLength=0;
    			int l=i;
    			while(Character.isLetter(expr.charAt(l)))
    			{
    				varLength++;
    				l++;
    				if(l==expr.length())
    				{
    					last=true;
    					break;
    				}
    			}
    			if(!(last))
    			{
    			if(!(expr.charAt(l)=='['))
    			{
    			for(int j=0;j<vars.size();j++)
    			{
    				
    				if(vars.get(j).name.equals(expr.substring(l-varLength,l)))
    				{
    					alreadyThere=true;
    					break;
    				}
    			}
    			if(!(alreadyThere))
    			{
    				vars.add(new Variable(expr.substring(l-varLength,l)));
    			}
    			i=l-1;
    			}
    			}
    			else
    			{
    				for(int j=0;j<vars.size();j++)
        			{
        				if(vars.get(j).name.equals(expr.substring(l-varLength,l)))
        				{
        					alreadyThere=true;
        					break;
        				}
        			}
    				if(!(alreadyThere))
        			{
        				vars.add(new Variable(expr.substring(l-varLength,l)));
        			}
        			i=l-1;
    			}
    		}
    	}	
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
 
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	// following line just a placeholder for compilation
    	Stack<Float> numStack=new Stack<Float>();
    	Stack<String> opStack=new Stack<String>();
    	for(int a=0;a<expr.length();a++)
    	{
    		if(Character.isDigit(expr.charAt(a)))
    		{
    			int p=a;
    			while(Character.isDigit(expr.charAt(p)))
    			{
    				p++;
    				if(p==expr.length())
    				{
    					break;
    				}
    			}
    			numStack.push(Float.parseFloat(expr.substring(a,p)));
    			a=p-1;
    		}
    		else if(expr.charAt(a)=='[')
    		{
    			int k=a-1;
    			while(Character.isLetter(expr.charAt(k)))
    			{
    				k--;
    				if(k==-1)
    				{
    					break;
    				}
    			}
    			for(int d=0;d<arrays.size();d++)
    			{
    				if(arrays.get(d).name.equals(expr.substring(k+1,a)))
    				{
    					int [] arr=arrays.get(d).values;
    					numStack.push((float)arr[(int)evaluate(expr.substring(a+1), vars, arrays)]);
    					break;
    				}
    			}
    			int bool=0;
    			boolean bypass=false;
    			for(int i=a+1;i<expr.length();i++)
    			{
    				if(expr.charAt(i)==']' && bool==0)
    				{
    					a=i;
    					if(!(a==expr.length()-1))
    					{
    						bypass=true;
    					}
    					break;
    				}
    				else if(expr.charAt(i)=='[')
    				{
    					bool++;
    				}
    				else if(expr.charAt(i)==']')
    				{
    					bool--;
    				}
    			}
    			if(bypass)
    			{
    				continue;
    			}
    		}
    		else if(Character.isLetter(expr.charAt(a)))
    		{
    			int h=a;
    			while(Character.isLetter(expr.charAt(h)))
    			{
    				h++;
    				if(h==expr.length())
    				{
    					break;
    				}
    			}
    			if(h==expr.length())
    			{
    				for(int c=0;c<vars.size();c++)
        			{
        				if(vars.get(c).name.equals(expr.substring(a,h)))
        				{
        					numStack.push((float)vars.get(c).value);
        					break;
        				}
        			}
        			a=h-1;
    			}
    			else if(!(expr.charAt(h)=='['))
    			{
    				for(int c=0;c<vars.size();c++)
    				{
    					if(vars.get(c).name.equals(expr.substring(a,h)))
    					{
    						numStack.push((float)vars.get(c).value);
    						break;
    					}
    				}
    				a=h-1;
    			}
    		}
    		else if(expr.charAt(a)=='(')
    		{
    			numStack.push((float)evaluate(expr.substring(a+1), vars, arrays));
    			int bool=0;
    			boolean bypass=false;
    			for(int i=a+1;i<expr.length();i++)
    			{
    				if(expr.charAt(i)==')' && bool==0)
    				{
    					a=i;
    					if(!(a==expr.length()-1))
    					{
    						bypass=true;
    					}
    					break;
    				}
    				else if(expr.charAt(i)=='(')
    				{
    					bool++;
    				}
    				else if(expr.charAt(i)==')')
    				{
    					bool--;
    				}
    			}
    			if(bypass)
    			{
    				continue;
    			}
    		}
    		else if(expr.charAt(a)=='+') 
    		{
    			opStack.push("+");
    		}
    		else if(expr.charAt(a)=='-')
    		{
    			opStack.push("-");
    		}
    		else if(expr.charAt(a)=='*')
    		{
    			opStack.push("*");
    		}
    		else if(expr.charAt(a)=='/')
    			{
    				opStack.push("/");
    			}
    		if(expr.charAt(a)==')' || a==expr.length()-1 || expr.charAt(a)==']')
    		{
    			if(!(opStack.isEmpty()))
    			{
    			Stack<String> newOpStack=new Stack<String>();
    			Stack<Float> newNumStack=new Stack<Float>();
    			while(!(numStack.isEmpty()))
    			{
    				newNumStack.push(numStack.pop());
    			}
    			while(!(opStack.isEmpty()))
    			{
    				newOpStack.push(opStack.pop());
    			}
    			while(!(newOpStack.isEmpty()))
    			{
    				if(newOpStack.peek().equals("+"))
    				{
    					opStack.push(newOpStack.pop());
    					numStack.push(newNumStack.pop());
    					if(newOpStack.isEmpty() && !(newNumStack.isEmpty()))
    					numStack.push(newNumStack.pop());
    				}
    				else if(newOpStack.peek().equals("-"))
    	    		{
    	    			opStack.push(newOpStack.pop());
    	    			numStack.push(newNumStack.pop());
    	    			if(newOpStack.isEmpty() && !(newNumStack.isEmpty()))
        				numStack.push(newNumStack.pop());
    	    		}
    				else if(newOpStack.peek().equals("*") || newOpStack.peek().equals("/"))
    	    		{
    					float val=newNumStack.pop();
    					while(newOpStack.peek().equals("*") || newOpStack.peek().equals("/"))
    					{
    						if(newOpStack.peek().equals("*"))
    						val=val*newNumStack.pop();
    						else if(newOpStack.peek().equals("/"))
    						val=val/newNumStack.pop();
    						newOpStack.pop();
    						if(newOpStack.isEmpty())
    						{
    							break;
    						}
    					}
    					newNumStack.push(val);
    	    		}
    			}
    			while(!(numStack.isEmpty()))
    			{
    				newNumStack.push(numStack.pop());
    			}
    			while(!(opStack.isEmpty()))
    			{
    				newOpStack.push(opStack.pop());
    			}
    			float val=newNumStack.pop();
    			while(!(newOpStack.isEmpty()))
    			{
    			String op=newOpStack.pop();
    			if(op.equals("+"))
    				val=val+newNumStack.pop();
    			if(op.equals("-"))
    				val=val-newNumStack.pop();
    			}
    			return val;
    			}
    			else
    			{
    				return numStack.pop();
    			}
    		}
    	}
    	return 0;
    }
}
