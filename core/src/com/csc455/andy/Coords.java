package com.csc455.andy;

import com.badlogic.gdx.math.Vector2;

//Utility methods for converting from and to box2d coordinates
public class Coords {
	//Modifies vector given
	public static float ratio = 8; //8 game units per 1 meter in box2d
	public static Vector2 gameToBox(Vector2 vec) {
		return vec.scl(1/ratio);
	}
	//returns new vector
	public static Vector2 gameToBox(float x,float y) {
		return new Vector2(x*(1/ratio),y*(1/ratio));
	}
	
	public static Vector2 boxToGame(Vector2 vec) {
		return vec.scl(ratio);
	}
	public static Vector2 boxToGame(float x,float y) {
		return new Vector2(x*ratio,y*ratio);
	}
}
