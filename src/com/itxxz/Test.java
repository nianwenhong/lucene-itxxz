package com.itxxz;

public class Test {

	/**
	 * @author ITÑ§Ï°Õß-ó¦Ð·
	 * @param args
	 */
	public static void main(String[] args) {
		for(int i=0;i<10;i++){
			System.out.println(getRandomChar());
		}
		

	}
	public static char getRandomChar(){       
        return (char)(0x4e00+(int)(Math.random()*(0x9fa5-0x4e00+1)));
    }

}
