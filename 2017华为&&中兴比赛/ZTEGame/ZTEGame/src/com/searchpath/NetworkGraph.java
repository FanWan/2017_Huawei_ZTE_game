package com.searchpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class NetworkGraph{		
	public int V; 					  				//the number of network nodes
	public int E;							  		//the number of network links
	private LinkedList<NetworkLink>[] adj;
	
	public NetworkLink notRequiredLink;  	
	public ArrayList<Integer> requiredVertex;
	public ArrayList<NetworkLink> requiredLinks;
	public LinkedHashMap<String,Integer> requiredWeight;
	
	public int source;
	public int terminal;

	private static final String NEWLINE = System.getProperty("line.separator");
	/**
	 * Initializes an empty NetworkLink-weighted graph with {@code V} vertices and 0 NetworkLinks.
	 */
	public NetworkGraph(int vertices) {
		this.V = vertices;
		this.E = 0;
		adj = (LinkedList<NetworkLink>[]) new LinkedList[V];	
		requiredVertex=new ArrayList<Integer>();
		requiredLinks=new  ArrayList<NetworkLink>();
		requiredWeight=new LinkedHashMap<String,Integer>();
		for (int v = 0; v < V; v++) {
			adj[v] = new LinkedList<NetworkLink>();
		}
	}

	public NetworkGraph(String[] graphContent) {		
		this(Integer.parseInt(graphContent[0].split("\\s+")[0]));
		int E = Integer.parseInt(graphContent[0].split("\\s+")[1]);
		this.source=Integer.parseInt(graphContent[0].split("\\s+")[2]);
		this.terminal=Integer.parseInt(graphContent[0].split("\\s+")[3]);
		/*读取必经节点和必经线段*/
		requiredVertex.add(Integer.valueOf(graphContent[2]));
		requiredVertex.add(Integer.valueOf(graphContent[3]));
		requiredLinks.add(new NetworkLink(graphContent[4]));
		requiredLinks.add(new NetworkLink(graphContent[5]));
		/*读取必经节点、必经线段对应的权重*/
		String[] requiredWeightArr=graphContent[7].split("\\s+");
		requiredWeight.put(String.valueOf(requiredVertex.get(0)), Integer.valueOf(requiredWeightArr[0]));
		requiredWeight.put(String.valueOf(requiredVertex.get(1)), Integer.valueOf(requiredWeightArr[1]));
		requiredWeight.put(requiredLinks.get(0).display(), Integer.valueOf(requiredWeightArr[2]));
		requiredWeight.put(requiredLinks.get(1).display(), Integer.valueOf(requiredWeightArr[3]));
		/*读取不可经过线段*/
		notRequiredLink=new NetworkLink(graphContent[9]);
		/*读取网络拓扑图*/
		int k=11;                                   
		for (int i = 0; i < E; i++) {			
			NetworkLink e = new NetworkLink(graphContent[k++]);
			if(!(e.from()==notRequiredLink.from()&&e.to()==notRequiredLink.to()))
				addNetworkLink(e);
		}
	}

	/**
	 * Initializes a new NetworkLink-weighted graph that is a deep copy of {@code G}.
	 *
	 * @param  G the NetworkLink-weighted graph to copy
	 */
	public NetworkGraph(NetworkGraph G) {
		this(G.V);
		this.source=G.source;
		this.terminal=G.terminal;
		this.requiredLinks=G.requiredLinks;
		this.E = G.E;
		for (int v = 0; v < G.V; v++) {
			ArrayList<NetworkLink> reverse = new ArrayList<NetworkLink>();		
			for (NetworkLink e : G.adj[v])
				reverse.add(e);
			Collections.shuffle(reverse);
			addNetworkLink(reverse,v,adj[v]);			
		}
	}
	
	//这样够�?图的副本目的在于通过深度优先搜索找到�?��从起点到终点的路�?如果这条路径经过必经线段的一个端�?，那么该路径下一个顶点肯定会经过4)
	private void addNetworkLink(ArrayList<NetworkLink> reverse,int v,LinkedList<NetworkLink> adj2v) {
		int w=findRequiredEndOf_v(v);
		if(w==-1){
			for (NetworkLink e : reverse) 
				adj2v.add(e);
			return;
		}
		int index=0;
		for(int i=0;i<reverse.size();i++){
			if(reverse.get(i).other(v)==w){
				index=i;
				adj2v.add(reverse.get(i));
				break;
			}
		}
		for(int i=0;i<reverse.size();i++){
			if(i==index)
				continue;
			adj2v.add(reverse.get(i));
		}
	}
	
	public int findRequiredEndOf_v(int v){
		for(NetworkLink edge:this.requiredLinks){
			if(edge.from()==v||edge.to()==v)
				return edge.other(v);
		}
		return -1;	
	}
			
	/**
	 * Adds the undirected NetworkLink {@code e} to this NetworkLink-weighted graph.
	 */
	private void addNetworkLink(NetworkLink e) {
		int v = e.from();
		int w = e.other(v);
		adj[v].add(e);
		adj[w].add(new NetworkLink(w,v,e.cost()));		
		this.E++;
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
	public NetworkLink vToW(int v,int w) {
		for(NetworkLink ele:adj[v]){
			if(ele.to()==w)
				return ele;
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
