package PR;

import java.io.*;
import java.util.*;

// https://www.acmicpc.net/problem/1541
public class Main {

	static int N;
	static long[] distance;
	static long[] price;
	
	
	public static void main(String[] args) throws Exception {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out))) {

			// 노드 개수
			N = Integer.parseInt(br.readLine()); 
			// 노드별 간격
			distance = Arrays.stream(br.readLine().split(" ")).mapToLong(Long::parseLong).toArray();
			// 노드별 가격
			price = Arrays.stream(br.readLine().split(" ")).mapToLong(Long::parseLong).toArray();
			
			long total=0;
			
			long min_price = price[0];
			for(int i=0;i<distance.length;i++) {
				if(min_price>price[i]) {
					min_price=price[i];
				}
				total+=distance[i]*min_price;
			}
						
			bw.write(total+"\n");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}