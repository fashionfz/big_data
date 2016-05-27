package com.yaxin.hbase;

import java.util.Random;

public class RandomUtil {
public static Random random = new Random();
	
	
	public static int getRandom(int begin,int end){
		return random.nextInt(end)+begin;
	}
	
	public static boolean percentage(int para){
		double value = para/100.00;
		double temp = Math.random();
		if(temp <= value)
			return true;
		else
			return false;
	}
	
	public static boolean permillage(int para){
		double value = para/1000.00;
		double temp = Math.random();
		if(temp <= value)
			return true;
		else
			return false;
	}
	
	public static void main(String[] args){
		for(int i=0;i<50;i++)
			System.out.println(RandomUtil.getRandom(0, 2));
	}
}
