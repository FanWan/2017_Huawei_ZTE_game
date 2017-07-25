package com.cacheserverdeploy.deploy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

public class SearchPath {
	
	public NetworkGraph graph_init;
	public Result result;
	public int sumBandwidth;
	public NetworkLink[] consumerLinks;
	public ArrayList<Integer> initServerNodes=new ArrayList<Integer>();
	
	
	public SearchPath(NetworkGraph graph_init,Result result){
		this.graph_init=graph_init;
		this.result=result;	
		this.consumerLinks=graph_init.consumerLinks;
		this.sumBandwidth=buildSuperGraph(consumerLinks,graph_init,null);		
	}
		
	public int spfa(ArrayList<Integer> serverNodes,boolean flag){
		NetworkGraph graph=new NetworkGraph(graph_init);
		buildSuperGraph(null,graph,serverNodes);
		return pathsSearch(graph,serverNodes, result,flag);
	}
		
	
	
	public Result beginSearchPaths(HashSet<Integer> serverNodes,Result result,Integer[] consumerNodesByDemand,boolean flag){
		NetworkGraph graph=new NetworkGraph(graph_init);
		ArrayList<Integer> serverNodesList=new ArrayList<Integer>();
		for(Integer ele:serverNodes)
			serverNodesList.add(ele);
		buildSuperGraph(null,graph,serverNodesList);
		result=pathsSearch(graph,serverNodes,result,consumerNodesByDemand,flag);
		return result;
	}
	
	private Result pathsSearch(NetworkGraph graph,HashSet<Integer> serverNodes,
			Result result,Integer[] consumerNodesByDemand,boolean flag){
		int source=graph.V()-2;
		int dst=graph.V()-1;
		int totalCost=0;
		int maximumFlow=0;
		ArrayList<NetworkLink> traversedList=new ArrayList<NetworkLink>();
		SPFA_AL spfa=new SPFA_AL(graph,source,dst);			
		while(spfa.hasAugmentingPath()) {	
			int minFlow = Integer.MAX_VALUE;  
			int v = spfa.t;  
			while (v != spfa.s) {  
				minFlow = Math.min(minFlow, spfa.NetworkLinkTo[v].surplusCap()); 
				v = spfa.NetworkLinkTo[v].other(v);  
			}  
			v = spfa.t;
			while (v!= spfa.s){  
				NetworkLink tmp=spfa.NetworkLinkTo[v];
				tmp.addFlow(minFlow); 
				if(flag)
					addEleTraversed(traversedList,copyOf(tmp,minFlow));
				NetworkLink reverseNetworkLink=spfa.G.vToW(tmp.to(),tmp.from(),tmp.cost());
				reverseNetworkLink.updateCanyu(minFlow);                           
				v = spfa.NetworkLinkTo[v].other(v);   
			}  			
			totalCost+=spfa.distTo[spfa.t]*minFlow;	
			maximumFlow+=minFlow;			
		}
		totalCost+=serverNodes.size()*graph.costOfServer;
		if(maximumFlow==sumBandwidth&&totalCost<=result.totalCost){
			System.out.println("totalCost update = "+totalCost+", server nodes="+serverNodes.size());
			result.totalCost=totalCost;	
			result.serverNodes=copyOf(serverNodes);
			if(flag)
				result.tempPaths=spfa.getRealPaths(traversedList,null);
		}
		if(maximumFlow<sumBandwidth&&totalCost<result.totalCost){
			ArrayList<NetworkLink> notFullLinks=new ArrayList<NetworkLink>();
			for(int v=0;v<consumerNodesByDemand.length;v++){
				NetworkLink v2dst=graph.vToW(consumerNodesByDemand[v], dst, 0);
				if(v2dst.surplusCap()>0)
					notFullLinks.add(v2dst);								
			}
			NetworkLink[] notFullLinksArr=sortNotFullLinks(notFullLinks);
			for(int i=notFullLinksArr.length-1;i>=(notFullLinksArr.length/2);i--)
				serverNodes.add(notFullLinksArr[i].from());	
		}
		return result;
	}

	private HashSet<Integer> copyOf(HashSet<Integer> serverNodes) {
		HashSet<Integer> res=new HashSet<Integer>();
		for(Integer ele:serverNodes)
			res.add(ele);
		return res;
	}

	private NetworkLink[] sortNotFullLinks(ArrayList<NetworkLink> notFullLinks) {
		NetworkLink[] notFullLinksArrNetworkLinks=new NetworkLink[notFullLinks.size()];
		notFullLinks.toArray(notFullLinksArrNetworkLinks);
		Arrays.sort(notFullLinksArrNetworkLinks,new Comparator<NetworkLink>(){
			@Override
			public int compare(NetworkLink o1, NetworkLink o2) {
				int surplus1=o1.cap()-o1.flow();
				int surplus2=o2.cap()-o2.flow();
				return Integer.compare(surplus1, surplus2);
			}			 
		});		 
		return notFullLinksArrNetworkLinks;
	}
	
	private int buildSuperGraph(NetworkLink[] consumerLinks, NetworkGraph graph_init, ArrayList<Integer> serverNodes) {
		int sumBandwidth=0;
		int superDst=graph_init.V()-1;
		if(consumerLinks!=null){
			for(NetworkLink ele:consumerLinks){
				sumBandwidth+=ele.cap();
				NetworkLink NetworkLink=new NetworkLink(ele.from(),superDst,0,ele.cap(),0,ele.cap());
				NetworkLink reverseNetworkLink=new NetworkLink(superDst,ele.from(),0,0,0,0);
				graph_init.addNetworkLink(NetworkLink);
				graph_init.addNetworkLink(reverseNetworkLink);
			}
		}
		int superSrc=graph_init.V()-2;
		if(serverNodes!=null){
			for(Integer serverId:serverNodes){
				NetworkLink NetworkLink=new NetworkLink(superSrc,serverId,0,Integer.MAX_VALUE,0,Integer.MAX_VALUE);
				NetworkLink reverseNetworkLink=new NetworkLink(serverId,superSrc,0,0,0,0);
				graph_init.addNetworkLink(NetworkLink);
				graph_init.addNetworkLink(reverseNetworkLink);			
			}
		}
		return sumBandwidth;	
	}
	
	private NetworkLink copyOf(NetworkLink tmp, int minFlow) {
		NetworkLink res=new NetworkLink(tmp.from(),tmp.to(),minFlow,tmp.cap(),tmp.cost(),tmp.canyu);
		return res;
	}

	private void addEleTraversed(ArrayList<NetworkLink> traversedList,NetworkLink NetworkLink) {
		if(traversedList.size()==0){
			traversedList.add(NetworkLink);
			return;
		}
		for(NetworkLink ele:traversedList){
			if(ele.from()==NetworkLink.from()&&ele.to()==NetworkLink.to()&&NetworkLink.cost()>0){
				ele.addFlow(NetworkLink.flow());
				return;
			}
			else if(ele.from()==NetworkLink.to()&&ele.to()==NetworkLink.from()&&NetworkLink.cost()<0){
				ele.addFlow(-1*NetworkLink.flow());
				return;
			}				
		}
		traversedList.add(NetworkLink);
	}

	private int pathsSearch(NetworkGraph graph,ArrayList<Integer> serverNodes,Result result,boolean flag){
		int source=graph.V()-2;
		int dst=graph.V()-1;
		int totalCost=0;
		int maximumFlow=0;
		ArrayList<NetworkLink> traversedList=new ArrayList<NetworkLink>();
		SPFA_AL spfa=new SPFA_AL(graph,source,dst);			
		while(spfa.hasAugmentingPath()) {	
			int minFlow = Integer.MAX_VALUE;  				
			int v = spfa.t;  
			while (v != spfa.s) {  
				minFlow = Math.min(minFlow, spfa.NetworkLinkTo[v].surplusCap()); 
				v = spfa.NetworkLinkTo[v].other(v);  
			}  
			v = spfa.t;
			while (v!= spfa.s){  
				NetworkLink tmp=spfa.NetworkLinkTo[v];
				tmp.addFlow(minFlow);         					          
				if(flag)
					addEleTraversed(traversedList,copyOf(tmp,minFlow));
				NetworkLink reverseNetworkLink=spfa.G.vToW(tmp.to(),tmp.from(),tmp.cost());
				reverseNetworkLink.updateCanyu(minFlow);                         
				v = spfa.NetworkLinkTo[v].other(v);   
			}  			
			totalCost+=spfa.distTo[spfa.t]*minFlow;	
			maximumFlow+=minFlow;			
		}
		totalCost+=serverNodes.size()*graph.costOfServer;
		if(flag){
			result.tempPaths=spfa.getRealPaths(traversedList,null);
			result.totalCost=totalCost;	
		}
		if(maximumFlow==sumBandwidth)
			return totalCost;
		return Integer.MAX_VALUE;
	}


	public ArrayList<String> filterSuperSourceAndDstInPaths(ArrayList<String> paths){
		ArrayList<String> resPaths=new ArrayList<String>();
		for(String path:paths){
			String[] pathArr=path.split("\\s+");
			int dst=Integer.valueOf(pathArr[pathArr.length-3]);
			StringBuilder sb=new StringBuilder();
			for(NetworkLink NetworkLink:consumerLinks){				
				if(dst==NetworkLink.from()){
					for(int i=1;i<=pathArr.length-3;i++)
						sb.append(pathArr[i]+" ");
					sb.append(NetworkLink.to()+" ");
					sb.append(pathArr[pathArr.length-1]);
					resPaths.add(sb.toString());
					break;
				}							
			}			
		}
		return resPaths;		
	}

	public String[] calculateFinalPaths(ArrayList<String> paths){
		String[] finalPaths=new String[paths.size()+1];
		finalPaths[0]=paths.size()+"\n";
		int k=1;
		for(String str:paths)
			finalPaths[k++]=str;
		return finalPaths;
	}	

	public void checkResult(NetworkGraph graph,ArrayList<String> path){
		for(String str:path){
			int index=str.lastIndexOf(" ");
			int flow=Integer.parseInt(str.substring(index+1, str.length()));
			String[] arr=str.split("\\s+");
			if(arr.length>3){
				for(int i=0;i<arr.length-3;i++){
					int v=Integer.valueOf(arr[i]);
					int w=Integer.valueOf(arr[i+1]);
					graph.vToW(v,w,-1).addFlow(flow);
					if(check(graph)){
						System.out.println(v+" -> "+w+" overloaded");
						return;	
					}
				}	
			}			
		}					
	}
		
	public boolean check(NetworkGraph graph) {
		for(int v=0;v<graph.V()-2;v++)
			for(NetworkLink ele:graph.adj(v)){
				if(ele.surplusCap()<0)
					return true;
			}
		return false;
	}


}
