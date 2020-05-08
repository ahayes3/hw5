package com.csc455.andy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Pistol extends Gun implements Disposable {
	public Pistol(int damage,TextureAtlas atlas) {
		super(-1,damage,.3f,0,atlas,Firemode.SINGLE);
	}
	public Pistol(int damage,String atlasPath) {
		super(-1,damage,.3f,0,new TextureAtlas(atlasPath),Firemode.SINGLE);
	}
	
	@Override
	public void shoot() {
		if(firingAnimation.isAnimationFinished(stateTime)) {
			stateTime = 0;
			
			//Shoot
		}
		
	}
	//Position of hand passed in
	public void draw(SpriteBatch batch,float x,float y,float originX,float originY,float angle) {
		batch.draw(current,x,y,originX,originY,current.getRegionWidth(),current.getRegionHeight(),.2f,.2f,angle);
	}
	public Vector2 getEnd() {
		//TODO
		return null;
	}
	@Override
	public void dispose() {
		atlas.dispose();
	}
}
