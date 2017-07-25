package com.searchpath;

import java.util.HashSet;
import java.util.Map;

public class Deploy
{   
	/**
	 * 你需要完成的入口
	 * <功能详细描述>
	 * @param graphContent 用例信息文件
	 * @return [参数说明] 输出结果信息
	 */
	public static String[] searchPath(String[] graphContent)
	{	
		NetworkGraph graph=new NetworkGraph(graphContent);
		OurIndividual best=OurGentic.beginOurGentic(graph,System.currentTimeMillis());
		String[] res=new String[5];
		StringBuilder sb=new StringBuilder();
		for(Map.Entry<String,Integer> entry:graph.requiredWeight.entrySet()){
			if(entry.getKey().split("\\s+").length==1)
				sb.append("必经点约束："+entry.getKey()+" 权重："+entry.getValue()+"\n");
			else
				sb.append("必经线段约束："+entry.getKey()+" 权重："+entry.getValue()+"\n");
		}
		sb.append("禁止通过线段："+graph.notRequiredLink+"\n");
		res[0]=sb.toString();
		res[1]="参考path: "+toFinalPath(best.theChrom);
		res[2]="参考path满足  " +calDemandNum(graph,best.theChrom.split("\\s+"))+" 个约束，获得的total weight为: "+best.theFitness.weight;
		res[3]="参考path所需的total cost: "+best.theFitness.totalCost;
		return res;
	}

	private static int calDemandNum(NetworkGraph graph, String[] chrome) {
		int num1=0;
		HashSet<Integer> set=new HashSet<Integer>();
		for(int i=0;i<chrome.length;i++)
			set.add(Integer.valueOf(chrome[i]));
		if(set.contains(graph.requiredVertex.get(0)))
			num1++;
		if(set.contains(graph.requiredVertex.get(1)))
			num1++;
		int num2=0;
		for(int nodeId:set){
			if(graph.findRequiredEndOf_v(nodeId)>0){
				num2++;
			}
		}		
		return num1+num2/2;
	}
	
	
	public static String toFinalPath(String res){
		String[] arr=res.split("\\s+");
		StringBuilder sb=new StringBuilder();
		for(String ele:arr){
			if(ele.equals("0"))
				sb.append("S->");
			else if(ele.equals("17"))
				sb.append("E");
			else
				sb.append("N"+ele+"->");
		}		
		return sb.toString();
	}
}


