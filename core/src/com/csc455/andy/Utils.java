package com.csc455.andy;

import com.badlogic.gdx.math.Vector2;

//Utility methods for converting from and to box2d coordinates
public class Utils {
	public static final short PLAYER_BITS = 4,PAST_BITS = 1,PRESENT_BITS = 2,BULLET_BITS = 8,ENEMY_BITS = 16;
	public static final short BULLET_GROUP=-1;
	//Modifies vector given
	public static float pixelRatio = 8; //8 game units per 1 meter in box2d
	public static Vector2 gameToBox(Vector2 vec) {
		return vec.scl(1/ pixelRatio);
	}
	//returns new vector
	public static Vector2 gameToBox(float x,float y) {
		return new Vector2(x*(1/ pixelRatio),y*(1/ pixelRatio));
	}
	
	public static Vector2 boxToGame(Vector2 vec) {
		return vec.scl(pixelRatio);
	}
	public static Vector2 boxToGame(float x,float y) {
		return new Vector2(x* pixelRatio,y* pixelRatio);
	}
}
