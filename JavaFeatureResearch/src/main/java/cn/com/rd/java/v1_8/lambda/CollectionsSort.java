package cn.com.rd.java.v1_8.lambda;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CollectionsSort {
	public static void main(String[] args) {
		// 测试列表
		List<Persion> list = new ArrayList<Persion>();
		list.add(new Persion("Tom", 27, 43.3));
		list.add(new Persion("Jam", 15, 51.4));
		list.add(new Persion("Kate", 36, 42.1));
		list.add(new Persion("Green", 13, 31.2));
		
		
		// 正向排序 - 写法1
		list.sort((p1, p2) -> p1.getAge() - p2.getAge());
		System.out.println(list);
		
		// 正向排序 - 写法2
		list.sort(Comparator.comparing(Persion::getAge));
		System.out.println(list);
		
		// 定义比较器
		Comparator<Persion> comparator = (p1, p2) -> p1.getAge() - p2.getAge();
		// 反向排序
		list.sort(comparator.reversed());
		System.out.println(list);
	}
}

class Persion {
	private String name;
	private int age;
	private double height;

	public Persion(String name, int age, double height) {
		this.name = name;
		this.age = age;
		this.height = height;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}

	@Override
	public String toString() {
		return "Name [" + name + "]-Age [" + age + "]-Height [" + height + "]";
	}
}
