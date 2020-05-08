package com.csc455.andy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public abstract class Gun {
	public enum Firemode {
		SINGLE,BURST,AUTO
	}
	TextureAtlas atlas;
	int ammo,damage,slot;
	Firemode firemode;
	float stateTime,timer,delay;
	Animation<TextureRegion> firingAnimation;
	TextureRegion idle,current;
	
	public Gun(int ammo, int damage, float delay, int slot, TextureAtlas atlas, Firemode firemode) {
		this.ammo = ammo;
		this.damage = damage;
		this.firemode =firemode;
		this.delay = delay;
		this.slot = slot;
		stateTime = 0;
		timer = .3f;
		
		Array<TextureRegion> animationRegions = new Array<>();
		this.atlas = atlas;
		idle = atlas.findRegion("Idle");
		for(TextureAtlas.AtlasRegion a: atlas.getRegions()) {
			if(a.name.contains("Firing"))
				animationRegions.add(a);
		}
		animationRegions.add(idle);
		firingAnimation = new Animation<>(.2f/animationRegions.size,animationRegions);
		current = idle;
	}
	public void update(float delta) {
		timer += delta;
		stateTime += delta;
		current =firingAnimation.getKeyFrame(stateTime,false);
		
	}
	public void addAmmo(int ammo) {
		this.ammo += ammo;
	}
	public abstract void shoot();
	public abstract void draw(SpriteBatch batch, float x, float y, float originX, float originY, float angle);
	public void draw(SpriteBatch batch, Vector2 pos, Vector2 origin, float angle) {
		draw(batch,pos.x,pos.y,origin.x,origin.y,angle);
	}
	public abstract float armHeight();
	public void draw(SpriteBatch batch,Vector2 pos,float originX,float originY,float angle) {
		draw(batch,pos.x,pos.y,originX,originY,angle);
	}
	public abstract Gun clone();
}
