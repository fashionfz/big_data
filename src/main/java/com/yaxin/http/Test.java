package com.yaxin.http;

public class Test {
	public static void main(String[] args){
		Thread a = new A(1);
		Thread b = new A(0);
		a.start();
		b.start();
	}
	
}

class A extends Thread{
	
	private static int x = 0;
	private int type = 0;
	private static volatile boolean flag = false;
	
	public A(int type){
		this.type = type;
	}
	
	public void xxx(int type){
		if(type ==1){
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			for(int i = 0; i<100000 ; i++);
			x = 10;
			flag = true;
		}else{
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			if(flag){
				System.out.println(x);
			}
			
		}
	}

	@Override
	public void run() {
		xxx(type);
	}
	
	
}
