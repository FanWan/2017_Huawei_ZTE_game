package com.searchpath;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Stack;

public class SPFA{
	
	private boolean[] marked;  
	private LinkedList<Integer> queue;
	public NetworkLink[] networkLinkTo;  
	public int[] distTo; 		
	public NetworkGraph G;	 

	
	public SPFA(NetworkGraph G){
		this.networkLinkTo = new NetworkLink[G.V]; 
		this.marked = new boolean[G.V];  
		this.distTo  = new int[G.V];
		this.queue= new LinkedList<Integer>();
		this.G=G;
	}
	
	public SPFA(NetworkGraph G,int source,int terminal){
		this.networkLinkTo = new NetworkLink[G.V]; 
		this.marked = new boolean[G.V];  
		this.G=G;
		dfs(source,terminal);
	}
	
	//depth first search from v
	private void dfs(int v,int t) {
		marked[v] = true;
		if(marked[t])
			return;
		for(NetworkLink e : G.adj(v)) {
			int w=e.other(v);
			if (!marked[w]) {
				networkLinkTo[w] = e;
				dfs(w,t);
			}
		}
	}
	
	public String pathTo(int source,int terminal,boolean flag){
		int v=terminal;
		Stack<Integer> stack=new Stack<Integer>();
		while(v!=source){  
			stack.push(v);
			v = networkLinkTo[v].other(v); 			
		}
		if(flag)
			stack.push(source);
		StringBuilder sb=new StringBuilder();
		while(!stack.isEmpty()){
			sb.append(stack.pop()+" ");
		}
		return sb.toString().trim();
	}
	
	public void BFS(int s){  
		Arrays.fill(distTo,Integer.MAX_VALUE);
		Arrays.fill(networkLinkTo,null);
		distTo[s] = 0;		 
		queue.add(s);  
		marked[s] = true; 
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
			marked[v] = false;
			for (NetworkLink e : G.adj(v)){  
				int w = e.other(v);  
				if (distTo[w]>distTo[v]+e.cost()){  
					networkLinkTo[w] = e;  
					distTo[w] = distTo[v]+e.cost();
					if(!marked[w]){
						addElementToQueue(queue,w,distTo);
						marked[w] = true;  
						sum+=distTo[w];
						count++;
					}
					
				}  
			}  
		}  
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
}
