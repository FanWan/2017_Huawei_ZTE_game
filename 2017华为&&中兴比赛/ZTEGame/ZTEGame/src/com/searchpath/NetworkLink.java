package com.searchpath;

public class NetworkLink implements Comparable<NetworkLink> { 

	private final int v;
	private final int w;
	private int cost;
	
	/**
	 * initialize a network link 
	 */
	public NetworkLink(String link) {
		String[] link_paras=link.split("\\s+");
		this.v=Integer.parseInt(link_paras[0]);
		this.w=Integer.parseInt(link_paras[1]);
		this.cost=Integer.parseInt(link_paras[2]);
	}

	public NetworkLink(int v, int w,int cost) {
		this.v=v;
		this.w=w;
		this.cost=cost;
	}
	
	public int from() {  
		return v;  
	}  

	public int to() {  
		return w;  
	}  

	public int cost(){
		return cost;
	}
		
	/**
	 * Returns the end-point of this NetworkLink that is different from the given vertex.
	 */
	public int other(int vertex) {
		if (vertex == v) 
			return w;
		else if(vertex == w) 
			return v;
		else 
			throw new IllegalArgumentException("Illegal endpoint");
	}
	
	public int compareTo(NetworkLink that) {
		return Integer.compare(this.cost, that.cost);
	}

	/**
     * @return a string representation of this node-node link,just for debug
     */
    public String toString() {
        return String.format("%d-%d %d", v,w,cost);
    }
    
    public String display(){
    	return v+" "+w;
    }
    
    

}