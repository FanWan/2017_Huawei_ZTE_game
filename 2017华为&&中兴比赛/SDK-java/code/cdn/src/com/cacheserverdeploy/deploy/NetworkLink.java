package com.cacheserverdeploy.deploy;

public class NetworkLink implements Comparable<NetworkLink> { 

	private final int v;
	private final int w;
	private int flow; 					//current flow on the link
	private int cap;             		 //capacity of link
	private int cost;
	
	public int canyu;                  //残余容量可能会大于该链路的初始容量cap
	
	/**
	 * initialize a consumer link 
	 */
	public NetworkLink(String c_link,int op) {
		String[] link_paras=c_link.split("\\s+");
		this.v=Integer.parseInt(link_paras[1]);
		this.w=Integer.parseInt(link_paras[0]);
		this.cap=Integer.parseInt(link_paras[2]);
		this.cost=0;
		this.flow=0;
		this.canyu=this.cap;
	}

	/**
	 * initialize a network link 
	 */
	public NetworkLink(String link) {
		String[] link_paras=link.split("\\s+");
		this.v=Integer.parseInt(link_paras[0]);
		this.w=Integer.parseInt(link_paras[1]);
		this.cap=Integer.parseInt(link_paras[2]);
		this.cost=Integer.parseInt(link_paras[3]);
		this.flow=0;
		this.canyu=this.cap;
	}

	public NetworkLink(int v, int w, int flow, int cap, int cost,int canyu) {
		this.v=v;
		this.w=w;
		this.flow=flow;
		this.cap=cap;
		this.cost=cost;
		this.canyu=canyu;
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
	
	public int cap(){
		return cap;
	}
	
	public int flow(){
		return flow;
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
		return Integer.compare(this.cap, that.cap);
	}

	/**
     * @return a string representation of this node-node link,just for debug
     */
    public String toString() {
        return String.format("%d-%d %d %d %d", v,w,flow,canyu,cost);
    }
    	
    /**
     * @return a deep copy of NetworkLink <em>e</em>
     */
	public NetworkLink deep_copy() {
		return new NetworkLink(v,w,flow,cap,cost,canyu);
	}
	  	
	/**
     * @return the surplus capacity of this NetworkLink
     */
	public int surplusCap() {  
		return canyu- flow;  
	}  

	/**
     * @return add flow of this NetworkLink
     */
	public void addFlow(int flow) {  	
		this.flow += flow;  
	} 
	
	// 更新反向边容量
	public void updateCanyu(int flow) {   
		this.canyu+=flow;  
	}  
}