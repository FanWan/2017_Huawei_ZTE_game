package com.cacheserverdeploy.deploy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;

public class OurGentic {
	public static int generateNum;
	static double crossoverProb = 0.98;
	static double mutationProb = 0.07;
	static int chromLenth =0;
	static int scaleNum =4;
	static OurIndividual[] thePopulation = new OurIndividual[scaleNum];
	static Random random = new Random();
	static ArrayList<Integer> severArrList = new ArrayList<Integer>();
	static ArrayList<Integer> curArrList = new ArrayList<Integer>();
	static ArrayList<OurIndividual> selectArrList = new ArrayList<OurIndividual>();

	public static ArrayList<Integer> beginOurGentic(SearchPath sp,ArrayList<Integer> initServerNodes,
			long begin,HashSet<Integer> firstNodes){
		ArrayList<Integer> finalServeList = new ArrayList<Integer>(); 
		severArrList.addAll(initServerNodes); 
		chromLenth = severArrList.size();
		generateNum = 0;
		generateFirstPopulation(sp,firstNodes);
		computeFitnessValue(sp);
		while(true){
			if((System.currentTimeMillis()-begin)/1000.0>=85.0)
				break;
			if(sp.graph_init.consumerNums<=100&&thePopulation[0].theFitness<=27568)
				break;
			generateNum++;
			addPreGeneration();   
			crossoverFactor();
			mutationFactor();
			computeFitnessValue(sp);
			addPreGeneration();
			selectionFactor();
			printInfoOut();
			selectArrList.clear();
		}
		for (int i = 0; i < thePopulation[0].theChrom.length(); i++) {
			if (thePopulation[0].theChrom.charAt(i) == '1') {
				finalServeList.add(severArrList.get(i));
			}
		}
		return finalServeList;
	}
	
	public static void addPreGeneration() {
		OurIndividual OurIndividual = null;
		for (int i = 0; i < scaleNum; i++) {
			OurIndividual = new OurIndividual();
			OurIndividual.theChrom = thePopulation[i].theChrom;
			OurIndividual.theFitness = thePopulation[i].theFitness;
			selectArrList.add(OurIndividual);
		}
	}
	
	public static void generateFirstPopulation(SearchPath sp,HashSet<Integer> firstNodes) {
		OurIndividual OurIndividual; 
		for(int i=0;i<scaleNum;i++){  
			OurIndividual=initFirst(sp,firstNodes);
			thePopulation[i]=OurIndividual;  
		}  
	}
	
	private static OurIndividual initFirst(SearchPath sp,HashSet<Integer> firstNodes) {
		char[] theChrom=new char[chromLenth];
		for(int i=0;i<chromLenth;i++){
			theChrom[i]='0';
		}
		if(firstNodes==null){
			for(NetworkLink ele:sp.consumerLinks){
				for(int i=0;i<chromLenth;i++)
					if(ele.from()==severArrList.get(i).intValue()){
						theChrom[i]='1';
						break;
					}	
			}
		}		
		else{
			for(Integer ele:firstNodes){
				for(int i=0;i<chromLenth;i++)
					if(ele.intValue()==severArrList.get(i).intValue()){
						theChrom[i]='1';
						break;
					}	
			}			
		}
		OurIndividual OurIndividual=new OurIndividual();
		OurIndividual.theChrom=new String(theChrom);
		return OurIndividual;
	}
	
	public static void computeFitnessValue(SearchPath sp) {
		for (int i = 0; i < scaleNum; i++) {
			for (int j = 0; j < thePopulation[i].theChrom.length(); j++) {
				if (thePopulation[i].theChrom.charAt(j) == '1') {
					curArrList.add(severArrList.get(j)); 
				}
			}
			thePopulation[i].theFitness = sp.spfa(curArrList,false);  
			curArrList.clear();
		}
	}

	public static void mutationFactor() {
		double p;
		char temp = 0;
		for (int i = 0; i < scaleNum; i++) {
			for (int j = 0; j < chromLenth; j++) {
				p = random.nextInt(65535) % 1000 / 1000.0;
				if (p < mutationProb) {
					temp = thePopulation[i].theChrom.charAt(j);
					temp = (temp == '1' ? '0' : '1');
					thePopulation[i].theChrom = thePopulation[i].theChrom.substring(0, j) + temp + thePopulation[i].theChrom.substring(j + 1);
				}
			}
		}
	}


	public static void selectionFactor() {
		Comparator<OurIndividual> SortList = new Comparator<OurIndividual>() {
			@Override
			public int compare(OurIndividual o1, OurIndividual o2) {
				return o1.theFitness - o2.theFitness;
			}
		};
		Collections.sort(selectArrList, SortList);
		for (int i = 0; i < scaleNum; i++) {
			thePopulation[i] = selectArrList.get(i);
		}
	}


	public static void crossoverFactor() {
		ArrayList<Integer> indexArr = new ArrayList<Integer>();
		int position;
		double p;
		String str1, str2;
		for (int i = 0; i <scaleNum; i++) {
			indexArr.add(i);
		}
		Collections.shuffle(indexArr);// 
		for (int i = 0; i < scaleNum - 1; i = i + 2) {
			p = random.nextInt(65535) % 1000 / 1000.0;
			if (p < crossoverProb) {
				position= random.nextInt(65536) % (chromLenth + 1);
				str1 = thePopulation[indexArr.get(i)].theChrom.substring(position);
				str2 = thePopulation[indexArr.get(i + 1)].theChrom.substring(position);
				thePopulation[indexArr.get(i)].theChrom = thePopulation[indexArr.get(i)].theChrom.substring(0, position) + str2;
				thePopulation[indexArr.get(i + 1)].theChrom = thePopulation[indexArr.get(i + 1)].theChrom.substring(0, position) + str1;
			}
		}
	}
	
	public static void printInfoOut() {
		String str=thePopulation[0].theChrom;
		int count = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '1')
				count++;
		}
		
		System.out.println("generate " + generateNum + " best "
				+ thePopulation[0].theFitness + " 服务器节点个数： "+ count);
	}
}

class OurIndividual {
	String theChrom;
	int theFitness;
}
