package com.naens.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.naens.tools.Numbered;
import com.naens.tools.Tools;

public class TestNumbered implements Numbered {

	public static void main(String[] args) throws IOException {
		System.out.println("Enter elements separated by space:");
	    String s = readline();
	    System.out.println("s=" + s);
		String [] elements = s.split("\\s+");
		System.out.println("elements: " + elements);
		System.out.println("elements.length=" + elements.length);
		ArrayList <TestNumbered> ns = new ArrayList <TestNumbered> (1000);
		for (int i = 0; i < elements.length; ++ i) {
			String string = elements[i];
			TestNumbered n = new TestNumbered();
			n.number = i;
			n.str = string;
			ns.add(i, n);
			System.out.printf("ns[%d]=%s\n", i, string);
		}
		System.out.print("Enter start index: ");
		int startIndex = -1;
		while (startIndex == -1) {
			try {
				startIndex = Integer.parseInt(readline());
				if (startIndex < 0 || startIndex > elements.length - 1) {
					startIndex = -1;
				}
			} catch (NumberFormatException e) {
				System.out.println("wrong number format!");
			}
			
		}		
		System.out.print("Enter end index: ");
		int endIndex = -1;
		while (endIndex == -1) {
			try {
				endIndex = Integer.parseInt(readline());
				if (endIndex < 0 || endIndex > elements.length - 1) {
					endIndex = -1;
				}
			} catch (NumberFormatException e) {
				System.out.println("wrong number format!");
			}
			
		}
		Tools.moveObject((TestNumbered[]) ns.toArray(), startIndex, endIndex);
		ns.add(endIndex, ns.remove(startIndex));
		for (TestNumbered n : ns) {
			System.out.println("new list:");
			System.out.printf("ns[%d]=%s\n", n.number, n.str);
		}
	}

	private static String readline() throws IOException {
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	    return bufferRead.readLine();
	}

	private int number;
	private String str;

	@Override
	public void setNumber(int number) {
		this.number = number;
	}
}
