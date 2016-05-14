package cn.com.rd.guava.table;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * 试用 Guava Table
 * 
 * @author i.m.superman
 */
public class TestTable {
	public static void main(String[] args) {
		// 测试基础 API
		testHashBasedTableAPI();
	}
	
	/**
	 * 测试基础 API
	 */
	public static void testHashBasedTableAPI() {
		// 初始化 Table <Row, Column, Value>
		Table<Integer, String, String> gTable = HashBasedTable.create();
		
		/*
		 *  放入测试数据
		 *  
		 *   		A		B		C		D		E
		 *  1		A1	B1		C1	D1	E1
		 *  2		A2	B2		C2	D2	E2
		 *  3		A3	B3		C3	D3	E3
		 *  4		A4	B4		C4	D4	E4
		 *  5		A5	B5		C5	D5	E5
		 */
		int[] rows = new int[]{1, 2, 3, 4, 5};
		String[] columns = new String[]{"A", "B", "C", "D", "E"};
		for(int row : rows) {
			for(String column : columns) {
				gTable.put(row, column, column + row);
			}
		}
		
		/* 基于行的操作 */
		// 测试 API - rowMap() - 返回一个Map<R, Map<C, V>>的视图
		System.out.println("rowMap()\t\t\t" + gTable.rowMap());
		// 测试 API - rowKeySet() - 返回一个Set<R>
		System.out.println("rowKeySet()\t\t" + gTable.rowKeySet());
		// 测试 API - row(Row) - 返回一个非null的Map<C, V>
		System.out.println("row(R)\t\t\t" + gTable.row(2));
		
		System.out.println();
		
		/* 基于列的操作（基于列的操作会比基于行的操作效率差些） */
		// 测试 API - columnMap()
		System.out.println("columnMap()\t\t" + gTable.columnMap());
		// 测试 API - columnKeySet() - 返回一个Set<R>
		System.out.println("columnKeySet()\t\t" + gTable.columnKeySet());
		// 测试 API - column(Column) - 返回一个非null的Map<C, V>
		System.out.println("column(C)\t\t\t" + gTable.column("C"));
		
		System.out.println(gTable.values());
	}
}
