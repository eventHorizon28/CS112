package apps;

import structures.*;
import java.util.ArrayList;

public class MST {
	
	/**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
		PartialTreeList L = new PartialTreeList();
		for(Vertex v: graph.vertices)
		{
			PartialTree T = new PartialTree(v);

			for(Vertex.Neighbor j = v.neighbors; j!=null; j= j.next)
			{
				PartialTree.Arc tmp = new PartialTree.Arc(v, j.vertex, j.weight);
				T.getArcs().insert(tmp);
			}
			System.out.println(T.toString());
			L.append(T);
		}
		//System.out.println(L.toString());
		return L;
	}

	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<PartialTree.Arc> execute(PartialTreeList ptlist) {
		
		System.out.println("Execute starts here");
		ArrayList<PartialTree.Arc> result = new ArrayList<PartialTree.Arc>();
		while(ptlist.size()>1)
		{
			PartialTree PTX = ptlist.remove();
			PartialTree.Arc alph = PTX.getArcs().deleteMin();
			System.out.println("arc = "+alph.toString());
			System.out.println("Root = "+PTX.toString());
			while(PTX.getArcs().size()>0 && alph.v1.getRoot().equals(alph.v2.getRoot())) //v2.getRoot() is what I added!!!
			{
				alph = PTX.getArcs().deleteMin();
			}
			result.add(alph);			
			System.out.println(result);
			PartialTree PTY = ptlist.removeTreeContaining(alph.v2);
			PTX.merge(PTY);
			ptlist.append(PTX);
		}
		return result;
	}
}
