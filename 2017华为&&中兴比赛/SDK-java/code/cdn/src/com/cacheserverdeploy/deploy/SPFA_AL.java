package com.cacheserverdeploy.deploy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class SPFA_AL {
	
	private boolean[] onQueue;  
	private LinkedList<Integer> queue;
	public NetworkLink[] NetworkLinkTo;  
	public int[]  distTo; 
		
	private boolean[] marked;  //marked[v] = true if v is reachable
	public int s;			  //source vertex
	public NetworkGraph G;	  //terminal vertex
	public int t;
	
	public SPFA_AL(NetworkGraph G, int s,int t){
		NetworkLinkTo = new NetworkLink[G.V()]; 
		onQueue = new boolean[G.V()];  
		distTo  = new int[G.V()];
		queue= new LinkedList<Integer>();
		this.s=s;
		this.t=t;
		this.G=G;
	}
	 
	private void initBFS(NetworkGraph G){
		for (int v = 0; v < G.V(); v++)
			distTo[v] = Integer.MAX_VALUE;
		for(int v=0;v<G.V();v++)
			NetworkLinkTo[v]=null;
	}

	public boolean hasAugmentingPath(){  
		initBFS(G);
		distTo[s] = 0;		 
		queue.add(s);  
		onQueue[s] = true; 
		int sum=0;
		int count=1;
		while (!queue.isEmpty()){
			if(distTo[queue.peek()]*count>sum){
				Integer first=queue.removeFirst();
				queue.add(first);
				continue;
			}
			int v=queue.removeFirst();
			sum-=distTo[v];count--;
			onQueue[v] = false;
			for (NetworkLink e : G.adj(v)){  
				int w = e.other(v);  
				if (e.surplusCap()>0&&distTo[w]>distTo[v]+e.cost()){  
					NetworkLinkTo[w] = e;  
					distTo[w] = distTo[v]+e.cost();
					if(!onQueue[w]){
						addElementToQueue(queue,w,distTo);
						onQueue[w] = true;  
						sum+=distTo[w];
						count++;
					}
					
				}  
			}  
		}  
		return NetworkLinkTo[t]!=null;  
	}  
		
	private void addElementToQueue(LinkedList<Integer> queue, int w, int[] distTo) {
		if(queue.size()==0){
			queue.add(w);
			return;
		}
		if(distTo[w]<distTo[queue.peek()])
			queue.addFirst(w);
		else
			queue.add(w);
	}
	
	private void dfs(NetworkGraph G, int v) { 
		marked[v] = true;
		for (NetworkLink e : G.adj(v)) {
			int w = e.other(v); 
			if (!marked[w]&&e.flow()>0){
				NetworkLinkTo[w] = e; 
				dfs(G, w);				
			}
		}
	}
	
	private void initDFS(NetworkGraph G) {
		for(int v=0;v<G.V();v++)
			NetworkLinkTo[v]=null;
		for(int v=0;v<G.V();v++)
			marked[v]=false;		
	}
	
	private NetworkGraph buildPathsNetwork(ArrayList<NetworkLink> traversedList){
		NetworkGraph graph = new NetworkGraph(G.V()); 
		for(NetworkLink ele:traversedList)
			graph.addNetworkLink(ele);					
		return graph;		
	}
	
    public ArrayList<String> getRealPaths(ArrayList<NetworkLink> traversedList,NetworkLink NetworkLink){
    	NetworkGraph pathGraph=buildPathsNetwork(traversedList);
    	Stack<Integer> stack=new Stack<Integer>();
    	ArrayList<String> resPaths=new ArrayList<String>();
    	marked=new boolean[G.V()];
    	initDFS(pathGraph);
    	dfs(pathGraph,s);
    	while(marked[t]){
    		int v=t;
    		int minFlow = Integer.MAX_VALUE;  
    		while (v!= s) {  
    			minFlow = Math.min(minFlow, NetworkLinkTo[v].flow());  
    			stack.push(v);
				v = NetworkLinkTo[v].other(v); 			
			}
    		stack.push(s);
    		resPaths.add(toPath(stack,minFlow,NetworkLink));
    		v = t;  
			while (v!=s) {  
				NetworkLinkTo[v].addFlow(-1*minFlow);         					                                            
				v = NetworkLinkTo[v].other(v);   
			}  
    		initDFS(pathGraph);
        	dfs(pathGraph,s);
    	}   	
    	return resPaths;
    }
    
	private String toPath(Stack<Integer> stack,int minflow,NetworkLink e){
		StringBuilder sb=new StringBuilder();
		while(!stack.isEmpty())
			sb.append(stack.pop()+" ");
		//sb.append(e.to()+" ");
		sb.append(minflow);
		return sb.toString().trim();
	}

}
