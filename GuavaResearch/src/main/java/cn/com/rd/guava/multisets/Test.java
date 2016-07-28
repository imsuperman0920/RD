package cn.com.rd.guava.multisets;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * 试用 Guava Multisets
 * 
 * @author i.m.superman
 */
public class Test {
	public static void main(String[] args) {
		Persion p1 = new Persion(1, "Tom", 1);
		Persion p2 = new Persion(1, "Grean", 1);
		Persion p3 = new Persion(1, "Kevin", 1);
		Persion p4 = new Persion(2, "Kate", 0);
		Persion p5 = new Persion(2, "Marri", 0);
		
		Multiset<Persion> mset = HashMultiset.create();
		mset.add(p1);
		mset.add(p2);
		mset.add(p3);
		mset.add(p4);
		mset.add(p5);

		int num = mset.count(p1);
		
		System.out.println(num);
		
		System.out.println(mset.elementSet());
	}
}

class Persion {
	private int id;
	private String name;
	private int sex;
	
	public Persion(int id, String name, int sex) {
		this.id = id;
		this.name = name;
		this.sex = sex;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}
	
	@Override
	public boolean equals(Object obj) {
		Persion other = (Persion) obj;
		return other.getId() == this.getId();
	}
	
	@Override
	public int hashCode() {
		return id;
	}
}
