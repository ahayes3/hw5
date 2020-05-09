package com.csc455.andy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Pistol extends Gun {
	private static final float scale = .2f;
	public Pistol(int damage,TextureAtlas atlas,Vector2 position,Dimension dimension, World world) {
		super(-1,damage,.3f,0,atlas,Firemode.SINGLE,position,new Vector2(1f,14/17f),new Vector2(4/26f,6/17f),scale,dimension,world);
		
	}
	
	@Override
	public void fire(World world, Vector2 position, Vector2 angle, Body owner) {
		//return new Bullet(position,angle.scl(100),3,1,damage,MainGame.bullet,owner,world);
	}
}
