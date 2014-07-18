package ru.dreamcloud.entities;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;
import com.sleepycat.persist.model.Relationship;

@Entity
public class TestEntity_1 {
	@SecondaryKey(relate=Relationship.MANY_TO_ONE)
	private String name;
	private int age;
	@PrimaryKey
	private String rank;
	private boolean active;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name=name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age=age;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank=rank;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active=active;
	}
}