package com.csc455.andy;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PlayerState {
	Vector2 position,velocity;
	int health;
	Array<Gun> inventory;
	Dimension dimension;
	public PlayerState(Vector2 position, Vector2 velocity, Array<Gun>inventory,int health,Dimension dimension) {
		this.position =position;
		this.velocity = velocity;
		this.health = health;
		this.inventory = new Array<>();
		this.dimension = dimension;
		//inventory.forEach(p -> this.inventory.add(p.clone()));
	}
}