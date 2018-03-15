package structures;

import java.util.ArrayList;

/**
 * Encapsulates an interval tree.
 * 
 * @author runb-cs112
 */
public class IntervalTree {
	
	/**
	 * The root of the interval tree
	 */
	IntervalTreeNode root;
	
	/**
	 * Constructs entire interval tree from set of input intervals. Constructing the tree
	 * means building the interval tree structure and mapping the intervals to the nodes.
	 * 
	 * @param intervals Array list of intervals for which the tree is constructed
	 */
	public IntervalTree(ArrayList<Interval> intervals) {
		
		// make a copy of intervals to use for right sorting
		ArrayList<Interval> intervalsRight = new ArrayList<Interval>(intervals.size());
		for (Interval iv : intervals) {
			intervalsRight.add(iv);
		}
		
		// rename input intervals for left sorting
		ArrayList<Interval> intervalsLeft = intervals;
		
		// sort intervals on left and right end points
		sortIntervals(intervalsLeft, 'l');
		sortIntervals(intervalsRight,'r');
		
		// get sorted list of end points without duplicates
		ArrayList<Integer> sortedEndPoints = 
							getSortedEndPoints(intervalsLeft, intervalsRight);
		
		
		// build the tree nodes
		root = buildTreeNodes(sortedEndPoints);
		
		// map intervals to the tree nodes
		mapIntervalsToTree(intervalsLeft, intervalsRight);
	}
	
	/**
	 * Returns the root of this interval tree.
	 * 
	 * @return Root of interval tree.
	 */
	public IntervalTreeNode getRoot() {
		return root;
	}
	
	/**
	 * Sorts a set of intervals in place, according to left or right endpoints.  
	 * At the end of the method, the parameter array list is a sorted list. 
	 * 
	 * @param intervals Array list of intervals to be sorted.
	 * @param lr If 'l', then sort is on left endpoints; if 'r', sort is on right endpoints
	 */
	public static void sortIntervals(ArrayList<Interval> intervals, char lr) {
        
        if(lr == 'l')
        {    
            for(int i = 1; i<intervals.size(); i++)
            {
                while((intervals.get(i).leftEndPoint < intervals.get(i-1).leftEndPoint) && i!=0)
                {
                    Interval tmp = intervals.get(i);
                    intervals.set(i, intervals.get(i-1));
                    intervals.set(i-1, tmp);
                    i--;
                }
            }
        }
        else if(lr == 'r')
        {    
            for(int i = 1; i<intervals.size(); i++)
            {
                while(i!=0 && intervals.get(i).rightEndPoint < intervals.get(i-1).rightEndPoint)
                {
                    Interval tmp = intervals.get(i);
                    intervals.set(i, intervals.get(i-1));
                    intervals.set(i-1, tmp);
                    i--;
                }
            }
        }
        else
            throw new IllegalArgumentException("Please enter 'l' or 'r'");
    }
	
	/**
	 * Given a set of intervals (left sorted and right sorted), extracts the left and right end points,
	 * and returns a sorted list of the combined end points without duplicates.
	 * 
	 * @param leftSortedIntervals Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 * @return Sorted array list of all endpoints without duplicates
	 */
	public static ArrayList<Integer> getSortedEndPoints(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
		
		
		ArrayList<Integer> sortedList = new ArrayList<Integer>();
        
        for(int i = 0; i<leftSortedIntervals.size(); i++) // adding the elements from leftSortedlist
        {
            if(sortedList.contains(leftSortedIntervals.get(i).leftEndPoint))
            {   
            	continue;
            }
            else
                sortedList.add(leftSortedIntervals.get(i).leftEndPoint);
        }

        for(int i = 0; i<rightSortedIntervals.size(); i++) //adding the elements from rightSortedlist and also checking if the sorted list already contains same element
        {
            if(sortedList.contains(rightSortedIntervals.get(i).rightEndPoint))
                continue;
            else
                sortedList.add(rightSortedIntervals.get(i).rightEndPoint);
        }
      
        
        for(int i = 1; i<sortedList.size(); i++) //sorting the list in ascending order
        {
            while(i!=0 && sortedList.get(i) < sortedList.get(i-1))
            {
                Integer tmp = sortedList.get(i);
                sortedList.set(i,sortedList.get(i-1));
                sortedList.set(i-1, tmp);
                i--;
            }
        }
        
        return sortedList;
	}
	
    private static double maxSplit(IntervalTreeNode smallT)
    {
        float val = smallT.splitValue;
        IntervalTreeNode tmp = smallT;
        while(tmp != null)
        {
            val = tmp.splitValue;
            tmp = tmp.rightChild;
        }
        return (double)val;
    }
    
    private static double minSplit(IntervalTreeNode smallT)
    {
        float val = smallT.splitValue;
        IntervalTreeNode tmp = smallT;
        while(tmp != null)
        {
            val = tmp.splitValue;
            tmp = tmp.leftChild;
        }
        return (double)val;
    }
    
    ////////////////////////////////////////////////////////////////////////Don't create an empty list for intervals let it remain null
	/**
	 * Builds the interval tree structure given a sorted array list of end points
	 * without duplicates.
	 * 
	 * @param endPoints Sorted array list of end points
	 * @return Root of the tree structure
	 */
	public static IntervalTreeNode buildTreeNodes(ArrayList<Integer> endPoints) {
		
        Queue<IntervalTreeNode> Qu = new Queue<IntervalTreeNode>();
        IntervalTreeNode T = new IntervalTreeNode(0,0,0);
        
        for(int i = 0; i<endPoints.size(); i++)
        {
            T = new IntervalTreeNode(endPoints.get(i), endPoints.get(i), endPoints.get(i));
            Qu.enqueue(T);
        }
        
        int s = Qu.size();
        if(s == 1)
        {
            T = Qu.dequeue();
        }
        else{
            while(s>1)
            {
                int tmp = s;
                while(tmp > 1)
                {
                    IntervalTreeNode T1 = Qu.dequeue();
                    IntervalTreeNode T2 = Qu.dequeue();
                    double v1 = maxSplit(T1);
                    double v2 = minSplit(T2);
                    //T1.maxSplitValue = (float)v1;
                    //T2.minSplitValue = (float)v2;
                    float x = (float) (v1+v2)/2;
                    //System.out.println(x);
                    IntervalTreeNode N = new IntervalTreeNode(x, (float)v2, (float)v1);
                    N.leftChild = T1;
                    N.rightChild = T2;
                    System.out.print(N.leftChild+"--");
                    System.out.print(N.toString());
                    System.out.println("--"+N.rightChild);
                    //System.out.println("T2 "+T2+" "+T2.toString());
                    Qu.enqueue(N);
                    tmp = tmp-2;
                }
                if(tmp == 1) //see why == and not <=
                {
                    Qu.enqueue(Qu.dequeue());
                }
                s = Qu.size();
            }
        }
        T = Qu.dequeue();
        
        return T;
	}
	
	/**
	 * Maps a set of intervals to the nodes of this interval tree. 
	 * 
	 * @param leftSortedIntervals Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 */
	public void mapIntervalsToTree(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
		IntervalTreeNode tmp = root;

		
		for(int i = 0; i<leftSortedIntervals.size(); i++)
		{
			IntervalTreeNode N = goThru(tmp, leftSortedIntervals.get(i));
			if(N.leftIntervals == null){
				N.leftIntervals = new ArrayList<Interval>();
				N.leftIntervals.add(leftSortedIntervals.get(i));
			}
			else
				N.leftIntervals.add(leftSortedIntervals.get(i));
		}
		
		for(int i = 0; i<rightSortedIntervals.size(); i++)
		{
			IntervalTreeNode N = goThru(tmp, rightSortedIntervals.get(i));
			if(N.rightIntervals == null){
				N.rightIntervals = new ArrayList<Interval>();
				N.rightIntervals.add(rightSortedIntervals.get(i));
			}
			else
				N.rightIntervals.add(rightSortedIntervals.get(i));
			
			//System.out.println("here: "+goThru(tmp, rightSortedIntervals.get(i)).toString());
		}
	}
	
	private static IntervalTreeNode goThru(IntervalTreeNode T, Interval a)
	{
		if(T.splitValue < a.rightEndPoint && T.splitValue > a.leftEndPoint)
		{
			return T;
		}
		
		else if(T.splitValue > a.rightEndPoint)
		{
			return goThru(T.leftChild, a);
		}
		
		else
		{
			return goThru(T.rightChild, a);
		}
	}
	/**
	 * Gets all intervals in this interval tree that intersect with a given interval.
	 * 
	 * @param q The query interval for which intersections are to be found
	 * @return Array list of all intersecting intervals; size is 0 if there are no intersections
	 */
	public ArrayList<Interval> findIntersectingIntervals(Interval q)
	{
		IntervalTreeNode T = root;
		return goThru2(T, q);
	}
	
	private ArrayList<Interval> goThru2(IntervalTreeNode T , Interval a)
	{
		ArrayList<Interval> result = new ArrayList<Interval>();
		
		if(T.rightChild == null)
			return result;
		
		else if(T.splitValue <= a.rightEndPoint && T.splitValue >= a.leftEndPoint)
		{
			if(T.leftIntervals == null || T.leftIntervals.size() ==0)
				;
			else
			{
				for(int i = 0; i<T.leftIntervals.size(); i++)
				{
					result.add(T.leftIntervals.get(i));
				}
			}
			
			ArrayList<Interval> rSub = goThru2(T.rightChild, a);
			for(int i = 0; i<rSub.size(); i++)
			{
				result.add(rSub.get(i));
			}
			
			ArrayList<Interval> lSub = goThru2(T.leftChild, a);
			for(int i = 0; i<lSub.size(); i++)
			{
				result.add(lSub.get(i));
			}
		}
		
		else if(T.splitValue > a.rightEndPoint)
		{
			int i = 0;
			
			if(T.leftIntervals != null)
			{
				while(i<T.leftIntervals.size())
				{
					if(((T.leftIntervals.get(i).leftEndPoint<= a.leftEndPoint) && (T.leftIntervals.get(i).rightEndPoint>= a.leftEndPoint)) || ((T.leftIntervals.get(i).leftEndPoint<= a.rightEndPoint) &&(T.leftIntervals.get(i).rightEndPoint>= a.rightEndPoint)))
						result.add(T.leftIntervals.get(i));
					i++;
				}
			}
			ArrayList<Interval> lSub = goThru2(T.leftChild, a);
			for(int j = 0; j<lSub.size(); j++)
			{
				result.add(lSub.get(j));
			}
		}
		
		else
		{
			if(T.rightIntervals !=null)
			{
				int i = T.rightIntervals.size()-1;
				while(i>=0)
				{
					if(((T.rightIntervals.get(i).leftEndPoint<= a.leftEndPoint) && (T.rightIntervals.get(i).rightEndPoint>= a.leftEndPoint)) || ((T.rightIntervals.get(i).leftEndPoint<= a.rightEndPoint) &&(T.rightIntervals.get(i).rightEndPoint>= a.rightEndPoint)))
						result.add(T.rightIntervals.get(i));
					i--;
				}
			}
			ArrayList<Interval> rSub = goThru2(T.rightChild, a);
			for(int j = 0; j<rSub.size(); j++)
			{
				result.add(rSub.get(j));
			}
		}
	return result;
	}
}

