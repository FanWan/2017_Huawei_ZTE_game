package com.cacheserverdeploy.deploy;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Stack;

public class NetworkGraph{
	private static final String NEWLINE = System.getProperty("line.separator");

	private int V; 					  				// the number of network nodes
	private int E;							  		//the number of network links
	public int consumerNums;                        // number of consumers
	private LinkedList<NetworkLink>[] adj;
	public NetworkLink[] consumerLinks;   					// the list of consumer nodes
	public int costOfServer;						// the cost of each server

	/**
	 * Initializes an empty NetworkLink-weighted graph with {@code V} vertices and 0 NetworkLinks.
	 */
	public NetworkGraph(int vertices) {
		this.V = vertices;
		this.E = 0;
		adj = (LinkedList<NetworkLink>[]) new LinkedList[V];		
		for (int v = 0; v < V; v++) {
			adj[v] = new LinkedList<NetworkLink>();
		}
	}

	public NetworkGraph(String[] graphContent,boolean fourNetworkLinks) {	
		this(Integer.parseInt(graphContent[0].split("\\s+")[0])+2);
		int E = Integer.parseInt(graphContent[0].split("\\s+")[1]);
		this.consumerNums=Integer.parseInt(graphContent[0].split("\\s+")[2]);
		this.costOfServer=Integer.parseInt(graphContent[2]);
		consumerLinks=new NetworkLink[this.consumerNums];
		int k=4;                                     //the begin of network links
		for (int i = 0; i < E; i++) {			
			NetworkLink e = new NetworkLink(graphContent[k++]);
			addNetworkLink(e,fourNetworkLinks);
		}
		k++;
		int i=0;
		for(;k<graphContent.length;k++){
			NetworkLink e = new NetworkLink(graphContent[k],-1);
			consumerLinks[i++]=e;			
		}
		Arrays.sort(consumerLinks);
	}

	/**
	 * Initializes a new NetworkLink-weighted graph that is a deep copy of {@code G}.
	 *
	 * @param  G the NetworkLink-weighted graph to copy
	 */
	public NetworkGraph(NetworkGraph G) {
		this(G.V());
		this.E = G.E();
		this.consumerNums=G.consumerNums;
		this.costOfServer=G.costOfServer;
		for (int v = 0; v < G.V(); v++) {
			// reverse so that adjacency list is in same order as original
			Stack<NetworkLink> reverse = new Stack<NetworkLink>();
			for (NetworkLink e : G.adj[v])
				reverse.push(e.deep_copy());
			for (NetworkLink e : reverse) 
				this.adj[v].add(e);
		}
	}

	public int V() {
		return V;
	}

	public int E() {
		return E;
	}

				
	/**
	 * used for reconstruct paths
	 */
	public void addNetworkLink(NetworkLink e){
		int v = e.from();
		adj[v].add(e);
		this.E++;
	}
		
	public int outDegree(int v){
		return adj[v].size();
	}
		
	/**
	 * Adds the undirected NetworkLink {@code e} to this NetworkLink-weighted graph.
	 */
	public void addNetworkLink(NetworkLink e,boolean fourNetworkLinks) {
		int v = e.from();
		int w = e.other(v);
		if(fourNetworkLinks){
			adj[v].add(e);
			adj[w].add(new NetworkLink(w,v,0,0,-1*e.cost(),0));

			adj[w].add(new NetworkLink(w,v,e.flow(),e.cap(),e.cost(),e.canyu));
			adj[v].add(new NetworkLink(v,w,0,0,-1*e.cost(),0));
		}
		else{
			adj[v].add(e);
			adj[w].add(new NetworkLink(w,v,e.flow(),e.cap(),e.cost(),e.canyu));
		}
		E++;
	}

	/**
	 * Returns the NetworkLinks incident on vertex {@code v}.
	 */
	public Iterable<NetworkLink> adj(int v) {
		return adj[v];
	}


	/**
	 * Returns an NetworkLink v->w
	 */
	public NetworkLink vToW(int v,int w,int cost) {
		if(cost>=0){
			for(NetworkLink ele:adj[v]){
				if(ele.to()==w&&ele.cost()<=0)
					return ele;
			}
		}
		if(cost<0){
			for(NetworkLink ele:adj[v]){
				if(ele.to()==w&&ele.cost()>0)
					return ele;
			}
		}
		return null;
	}
	
	/**
	 * Returns a string representation of the NetworkLink-weighted graph.
	 * This method takes time proportional to <em>E</em> + <em>V</em>.
	 */
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(V + " " + E + NEWLINE);
		for (int v = 0; v < V; v++) {
			s.append(v + ": ");
			for (NetworkLink e : adj[v]) {
				s.append(e + "  ");
			}
			s.append(NEWLINE);
		}
		return s.toString();
	}

}
