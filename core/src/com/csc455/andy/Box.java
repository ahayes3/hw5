package com.csc455.andy;

public class Box<I> {
	I value;
	public Box() {
		value = null;
	}
	public Box(I value) {
		this.value = value;
	}
}
