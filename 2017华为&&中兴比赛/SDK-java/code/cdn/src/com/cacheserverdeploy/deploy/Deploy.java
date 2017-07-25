package com.cacheserverdeploy.deploy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

public class Deploy
{   

	/**
	 * 你需要完成的入口
	 * <功能详细描述>
	 * @param graphContent 用例信息文件
	 * @return [参数说明] 输出结果信息
	 * @see [类方法、类#成员]
	 */
	public static String[] deployServer(String[] graphContent)
	{	
		long begin=System.currentTimeMillis();
		NetworkGraph graph_init=new NetworkGraph(graphContent,true);
		NetworkGraph usedForCheckResult=new NetworkGraph(graphContent,false);
		String[] finalPaths=null;
		Result result=new Result(null,graph_init.consumerNums*graph_init.costOfServer,null);  				//用来存放结果	
		Integer[] consumerNodesByDemand=getConsumerNodesByDemandAsc(graph_init.consumerLinks);
		Node[] middleNodesByOutDegree=sortNodesByOutDegree(graph_init,consumerNodesByDemand,true);
		Node[] consumeriNodesByOutDegree=sortNodesByOutDegree(graph_init,consumerNodesByDemand,false);
		SearchPath sp=new SearchPath(graph_init,result);	
		ArrayList<Integer> initServerNodes=new ArrayList<Integer>();		
		for(int i=consumerNodesByDemand.length-1;i>=(consumerNodesByDemand.length/4);i--)
			initServerNodes.add(consumerNodesByDemand[i]);
		for(int i=middleNodesByOutDegree.length-1;i>=(middleNodesByOutDegree.length*3/4);i--)
			initServerNodes.add(middleNodesByOutDegree[i].v);

		if(graph_init.consumerNums>=360){
			HashSet<Integer> serverNodes=new HashSet<Integer>();
			for(int i=consumerNodesByDemand.length-1;i>(graph_init.consumerNums/2);i--){
				serverNodes.add(consumerNodesByDemand[i]);
				if(serverNodes.size()>=(graph_init.consumerNums/3)){
					result=sp.beginSearchPaths(serverNodes,result,consumerNodesByDemand,false);
					int k=0;
					while(result.serverNodes==null){
						result=sp.beginSearchPaths(serverNodes,result,consumerNodesByDemand,false);
						k++;
						if(k>=8)    //循环次数不会超过8次
							break;
					}
				}
			}
			System.out.println();
			serverNodes.clear();
			Result result2=new Result(null,graph_init.consumerNums*graph_init.costOfServer,null);
			for(int i=consumerNodesByDemand.length-1;i>(graph_init.consumerNums/2);i--){
				if((System.currentTimeMillis()-begin)/1000.0>=88.5)
					break;
				serverNodes.add(consumerNodesByDemand[i]);
				if(serverNodes.size()>=(graph_init.consumerNums/4)){
					result2=sp.beginSearchPaths(serverNodes,result2,consumerNodesByDemand,false);
					int k=0;
					while(result2.serverNodes==null){
						result2=sp.beginSearchPaths(serverNodes,result2,consumerNodesByDemand,false);
						k++;
						if(k>=8)    //循环次数不会超过8次
							break;
					}
				}
			}
			System.out.println();
			serverNodes.clear();
			Result result3=new Result(null,graph_init.consumerNums*graph_init.costOfServer,null);
			for(int i=consumerNodesByDemand.length-1;i>(graph_init.consumerNums/2);i--){
				if((System.currentTimeMillis()-begin)/1000.0>=88.5)
					break;
				serverNodes.add(consumerNodesByDemand[i]);
				if(serverNodes.size()>=80){
					result3=sp.beginSearchPaths(serverNodes,result3,consumerNodesByDemand,false);
					int k=0;
					while(result3.serverNodes==null){
						result3=sp.beginSearchPaths(serverNodes,result3,consumerNodesByDemand,false);
						k++;
						if(k>=8)    //循环次数不会超过8次
							break;
					}
				}
			}
			Result min=(result.totalCost<=result2.totalCost?result:result2);
			min=(min.totalCost<=result3.totalCost?min:result3);
			
			min=sp.beginSearchPaths(min.serverNodes,min,consumerNodesByDemand,true);
			ArrayList<String> paths=sp.filterSuperSourceAndDstInPaths(min.tempPaths);
			finalPaths=sp.calculateFinalPaths(paths);
			sp.checkResult(usedForCheckResult,paths);	
			System.out.println((System.currentTimeMillis()-begin)/1000.0);
		}
		else if(graph_init.consumerNums>=135){
			HashSet<Integer> serverNodes=new HashSet<Integer>();
			for(int i=consumerNodesByDemand.length-1;i>(graph_init.consumerNums/2);i--){
				serverNodes.add(consumerNodesByDemand[i]);
				if(serverNodes.size()>=(graph_init.consumerNums/4)){
					result=sp.beginSearchPaths(serverNodes,result,consumerNodesByDemand,false);
					int k=0;
					while(result.serverNodes==null){
						result=sp.beginSearchPaths(serverNodes,result,consumerNodesByDemand,false);
						k++;
						if(k>=8)    //循环次数不会超过8次
							break;
					}
				}
			}
			initServerNodes=updateInitServerNodes(initServerNodes,result.serverNodes);
			sp.spfa(OurGentic.beginOurGentic(sp,initServerNodes,begin,result.serverNodes),true);
			ArrayList<String> paths=sp.filterSuperSourceAndDstInPaths(result.tempPaths);
			finalPaths=sp.calculateFinalPaths(paths);	
			sp.checkResult(usedForCheckResult,paths);					
		}	
		else{
			ArrayList<Integer> finalServerNodes=OurGentic.beginOurGentic(sp,initServerNodes,begin,null);
			sp.spfa(finalServerNodes,true);
			ArrayList<String> paths=sp.filterSuperSourceAndDstInPaths(result.tempPaths);
			finalPaths=sp.calculateFinalPaths(paths);
		}		
		return finalPaths;
	}

	private static  ArrayList<Integer> updateInitServerNodes(ArrayList<Integer> initServerNodes, HashSet<Integer> serverNodes){
		HashSet<Integer> temp=new HashSet<Integer>();
		temp.addAll(initServerNodes);
		temp.addAll(serverNodes);
		ArrayList<Integer> res=new ArrayList<Integer>();
		for(Integer ele:temp)
			res.add(ele);		
		return res;		
	}

	private static Integer[] getConsumerNodesByDemandAsc(NetworkLink[] consumerLinks){
		Integer[] res=new Integer[consumerLinks.length];
		int i=0;
		for(NetworkLink networkLink:consumerLinks)
			res[i++]=networkLink.from();
		return res;
	}

	private static HashSet<Integer> deleteNodeFromServers(int num,Node[] consumeriNodesByOutDegree,HashSet<Integer> serverNodes){
		Node[] tempNodes=new Node[serverNodes.size()];
		int  k=0;
		for(Integer ele:serverNodes){
			for(Node node:consumeriNodesByOutDegree){
				if(ele.intValue()==node.v){
					Node t=new Node(ele,node.weight);
					tempNodes[k++]=t;
					break;					
				}
			}
		}
		Arrays.sort(tempNodes);
		serverNodes.clear();
		for(int i=num;i<tempNodes.length;i++){
			serverNodes.add(tempNodes[i].v);
		}	
		return serverNodes;		
	}	

	private static Node[] sortNodesByOutDegree(NetworkGraph graph,Integer[] consumerNodes,Boolean consumerOrMiddleNode){
		Node[] nodesArr;
		if(consumerOrMiddleNode)
			nodesArr=new Node[graph.V()-2-consumerNodes.length];	
		else
			nodesArr=new Node[consumerNodes.length];
		ArrayList<Integer> consumerNodesList=new ArrayList<Integer>();
		for(int ele:consumerNodes)
			consumerNodesList.add(ele);
		int k=0;
		if(consumerOrMiddleNode){		
			for(int v=0;v<=graph.V()-3;v++){
				if(!consumerNodesList.contains(v)){
					Node temp=new Node(v,graph.outDegree(v));
					nodesArr[k++]=temp;
				}						
			}	
		}
		else{
			for(int v=0;v<=graph.V()-3;v++){
				if(consumerNodesList.contains(v)){
					Node temp=new Node(v,graph.outDegree(v));
					nodesArr[k++]=temp;
				}						
			}	
		}		
		Arrays.sort(nodesArr);
		return nodesArr;
	}

	static class Node implements Comparable<Node>{
		public int v;
		public int weight;

		public Node(int v, int weight){
			this.v=v;
			this.weight=weight;
		}

		@Override
		public int compareTo(Node that) {			
			return Integer.compare(this.weight, that.weight);
		}

		public String toString(){
			return v + " "+weight;
		}
	}
}

class Result{

	public ArrayList<String> tempPaths;
	public int totalCost;
	public HashSet<Integer> serverNodes;

	public Result(ArrayList<String> tempPaths,int totalCost, HashSet<Integer> serverNodes){
		this.tempPaths=tempPaths;
		this.totalCost=totalCost;
		this.serverNodes=serverNodes;
	}

}

