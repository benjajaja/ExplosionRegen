package com.yermocraft.Gipsy;

public abstract class ErRunnable implements Runnable {

	private int id;
	
	public int getId() {
		return id;
	}

	public ErRunnable setId(int id) {
		this.id = id;
		return this;
	}
	
	public abstract void runWithoutSchedule();

}
