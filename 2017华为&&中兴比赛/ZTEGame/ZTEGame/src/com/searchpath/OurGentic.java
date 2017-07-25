package com.searchpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

public class OurGentic {

	private static Random random=new Random();
	private static int scaleNum =40;			  	
	private static OurIndividual[] thePopulation = new OurIndividual[scaleNum];  
	private static ArrayList<OurIndividual> selectArrList = new ArrayList<OurIndividual>();

	public static OurIndividual beginOurGentic(NetworkGraph graph,long begin){
		generateFirstPopulation(graph);
		computeFitnessValue(graph);
		addPreGeneration();   
		computeFitnessValue(graph);
		mutationFactor(graph);
		selectionFactor();
		System.out.println("best:"+ thePopulation[0].theFitness+" path="+thePopulation[0].theChrom);
		selectArrList.clear();
		return thePopulation[0];
	}

	private static void addPreGeneration() {
		for (int i = 0; i < scaleNum; i++) {
			OurIndividual individual = new OurIndividual();
			individual.theChrom = thePopulation[i].theChrom;
			individual.theFitness=new Fitness(thePopulation[i].theFitness);
			selectArrList.add(individual);
		}
	}

	//产生初始解
	public static void generateFirstPopulation(NetworkGraph graph) {
		OurIndividual OurIndividual; 
		for(int i=0;i<scaleNum;i++){  
			OurIndividual=initFirst(graph);
			thePopulation[i]=OurIndividual;  
			optimizeChrome(graph,thePopulation[i]);
		}  		
	}

	//优化初始解
	private static void optimizeChrome(NetworkGraph graph,OurIndividual individual){
		String[] chromeArr=individual.theChrom.split("\\s+");
		int index1=-1,index2=-1;
		for(int i=1;i<chromeArr.length;i++){
			if(graph.findRequiredEndOf_v(Integer.valueOf(chromeArr[i]))>0
					||graph.requiredVertex.contains(Integer.valueOf(chromeArr[i]))){
				index1=i;
				break;
			}		
		}
		for(int i=chromeArr.length-2;i>=0;i--){
			if(graph.findRequiredEndOf_v(Integer.valueOf(chromeArr[i]))>0
					||graph.requiredVertex.contains(Integer.valueOf(chromeArr[i]))){
				index2=i;
				break;
			}
		}
		SPFA spfa=new SPFA(graph);
		spfa.BFS(graph.source);
		String path1=spfa.pathTo(graph.source,Integer.valueOf(chromeArr[index1]),true);
		spfa.BFS(Integer.valueOf(chromeArr[index2]));
		String path2=null;
		if(index1!=index2)
			path2=spfa.pathTo(Integer.valueOf(chromeArr[index2]),graph.terminal,true);
		else
			path2=spfa.pathTo(Integer.valueOf(chromeArr[index2]),graph.terminal,false);
		StringBuilder sb=new StringBuilder();		
		sb.append(path1+" ");
		for(int i=index1+1;i<index2;i++)
			sb.append(chromeArr[i]+" ");
		sb.append(path2);
		if(sb.toString().trim().split("\\s+").length<=9)
			individual.theChrom=sb.toString().trim();	
	}

	private static OurIndividual initFirst(NetworkGraph graph) {
		SPFA spfa=new SPFA(new NetworkGraph(graph),graph.source,graph.terminal);
		String theChrom=spfa.pathTo(graph.source,graph.terminal,true);
		String[] chromeArr=theChrom.split("\\s+");
		while(chromeArr.length>9||!checkPath(chromeArr,graph)){//初始解至少满足一个约束，以及路径长度不超过9
			spfa=new SPFA(new NetworkGraph(graph),graph.source,graph.terminal);
			theChrom=spfa.pathTo(graph.source,graph.terminal,true);
			chromeArr=theChrom.split("\\s+");
		}
		OurIndividual OurIndividual=new OurIndividual();
		OurIndividual.theChrom=new String(theChrom);
		return OurIndividual;
	}

	private static boolean checkPath(String[] chromeArr,NetworkGraph graph){
		for(int i=0;i<chromeArr.length;i++){
			if(graph.requiredVertex.contains(Integer.valueOf(chromeArr[i]))||
					graph.findRequiredEndOf_v(Integer.valueOf(chromeArr[i]))>0)
				return true;
		}
		return false;
	}

	//计算初始解的适应度
	private static void computeFitnessValue(NetworkGraph graph){
		for(int i = 0; i < scaleNum; i++) {
			String[] chrome=thePopulation[i].theChrom.split("\\s+");
			int totalCost=calTotalCost(graph,chrome);
			int weight=calWeight2(graph,chrome);
			thePopulation[i].theFitness=new Fitness(weight,totalCost);
		}
	}

	private static int calWeight2(NetworkGraph graph, String[] chrome) {
		int weight1=0;
		HashSet<Integer> set=new HashSet<Integer>();
		for(int i=0;i<chrome.length;i++)
			set.add(Integer.valueOf(chrome[i]));
		if(set.contains(graph.requiredVertex.get(0)))
			weight1+=graph.requiredWeight.get(String.valueOf(graph.requiredVertex.get(0)));
		if(set.contains(graph.requiredVertex.get(1)))
			weight1+=graph.requiredWeight.get(String.valueOf(graph.requiredVertex.get(1)));
		int weight2=0;
		for(int nodeId:set){
			if(graph.findRequiredEndOf_v(nodeId)>0){
				for(Map.Entry<String, Integer> entry:graph.requiredWeight.entrySet()){
					String key=entry.getKey();
					if(check2(key,String.valueOf(nodeId))){
						weight2+=entry.getValue();
						break;
					}						
				}

			}
		}
		return weight1+weight2/2;
	}

	public static boolean check2(String key,String nodeId){
		String[] arr=key.split("\\s+");
		if(arr.length==1)
			return arr[0].equals(nodeId);
		if(nodeId.equals(arr[0])||nodeId.equals(arr[1]))
			return true;
		return false;
	}

	private static int calTotalCost(NetworkGraph graph, String[] chrome){
		int totalCost=0;
		for(int i=0;i<chrome.length-1;i++)
			totalCost+=graph.vToW(Integer.valueOf(chrome[i]),Integer.valueOf(chrome[i+1])).cost();
		return totalCost;
	}


	//排序初始解
	private static void selectionFactor() {
		Comparator<OurIndividual> SortList = new Comparator<OurIndividual>() {
			@Override
			public int compare(OurIndividual one, OurIndividual two) {
				if(one.theFitness.weight!=two.theFitness.weight)
					return -1*(one.theFitness.weight-two.theFitness.weight);
				else
					return one.theFitness.totalCost-two.theFitness.totalCost;
			}
		};
		Collections.sort(selectArrList, SortList);
		for (int i = 0; i < scaleNum; i++)
			thePopulation[i] = selectArrList.get(i);
	}
	
	//拆分，重组初始解
	private static void mutationFactor(NetworkGraph graph) {
		for (int i = 0; i<scaleNum; i++) {
			String[] chromeArr=thePopulation[i].theChrom.split("\\s+");
			HashSet<Integer> remainRequiredNodes=calRemainRequiredNodes(graph,chromeArr);
			while(remainRequiredNodes.size()>0&&chromeArr.length<=9){						
				ArrayList<Integer> mutateIndex=findRequiredNodeIndexInPath(graph,chromeArr);
				int startIndex=-1,endIndex=-1;
				int max=Integer.MIN_VALUE;
				for(int j=0;j<mutateIndex.size()-1;j++){
					if(mutateIndex.get(j+1)-mutateIndex.get(j)>max){
						max=mutateIndex.get(j+1)-mutateIndex.get(j);
						startIndex=mutateIndex.get(j);
						endIndex=mutateIndex.get(j+1);
					}
				}
				if(max==1) //拆分的两个顶点在路径中相邻，必经线段不允许拆�?
					break;				
				String theChrom=beginMutate(startIndex,endIndex,Integer.valueOf(chromeArr[startIndex]),
						Integer.valueOf(chromeArr[endIndex]),graph,chromeArr,remainRequiredNodes);
				if(theChrom.split("\\s+").length<=9){
					thePopulation[i].theChrom=theChrom;
					chromeArr=thePopulation[i].theChrom.split("\\s+");
				}
			}
		}
	}

	//拆分路径并重组
	private static String beginMutate(int startIndex, int endIndex,Integer startNode, Integer endNode, 
			NetworkGraph graph,String[] chromeArr,HashSet<Integer> remainRequiredNodes) {
		StringBuilder sb=new StringBuilder();
		SPFA spfa=new SPFA(graph);
		spfa.BFS(startNode);
		int selectedNode=randomSelectEleFromSet(remainRequiredNodes);
		String path1=spfa.pathTo(startNode,selectedNode,true);
		spfa.BFS(selectedNode);
		String path2=spfa.pathTo(selectedNode,endNode,false);		
		for(int i=0;i<startIndex;i++)
			sb.append(chromeArr[i]+" ");
		sb.append(path1+" ");
		sb.append(path2+" ");
		for(int i=endIndex+1;i<chromeArr.length;i++)
			sb.append(chromeArr[i]+" ");
		return sb.toString().trim();		
	}

	//计算初始解中已经满足的约束包含某些顶点
	private static ArrayList<Integer> findRequiredNodeIndexInPath(NetworkGraph graph, String[] chromeArr) {
		ArrayList<Integer> res=new ArrayList<Integer>();
		for(int i=0;i<chromeArr.length;i++){
			if(graph.requiredVertex.contains(Integer.valueOf(chromeArr[i])))
				res.add(i);
			if(graph.findRequiredEndOf_v(Integer.valueOf(chromeArr[i]))>0)
				res.add(i);
		}
		return res;
	}

	//计算初始解中还没有满足的路径约束(包含某些顶点)
	private static HashSet<Integer> calRemainRequiredNodes(NetworkGraph graph,String[] nodeArr){
		HashSet<Integer> remainRequiredNodes=new HashSet<Integer>();
		HashSet<Integer> requiredNodesInPath=new HashSet<Integer>();
		for(int i=0;i<nodeArr.length;i++){
			if(graph.requiredVertex.contains(Integer.valueOf(nodeArr[i])))
				requiredNodesInPath.add(Integer.valueOf(nodeArr[i]));
		}		
		for(Integer node:graph.requiredVertex){
			if(!requiredNodesInPath.contains(node))
				remainRequiredNodes.add(node);
		}
		return remainRequiredNodes;		
	}


	private static int randomSelectEleFromSet(HashSet<Integer> set){
		if(set.size()==0)
			return -1;
		Integer[] temp=new Integer[set.size()];
		set.toArray(temp);
		int selectedEle=temp[random.nextInt(65535)%temp.length];
		set.remove(selectedEle);
		return selectedEle;	
	}	

}

class OurIndividual {
	public String theChrom;  //路径
	public Fitness theFitness; //适应�?

	public String toString(){		
		return theChrom;
	}
}

class Fitness{	

	public int weight;    //权重
	public int totalCost; //final cost

	public Fitness(int weight,int totalCost){
		this.weight=weight;
		this.totalCost=totalCost;		
	}

	public Fitness(Fitness theFitness) {
		this.weight=theFitness.weight;
		this.totalCost=theFitness.totalCost;
	}

	public String toString(){
		return "weight="+weight+ " totalCost="+totalCost;
	}

}
