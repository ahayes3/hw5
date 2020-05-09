package com.csc455.andy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;

abstract class Enemy implements Hittable{
	Animation<TextureRegion> moving;
	TextureAtlas atlas;
	Body body;
	int health;
	boolean dead;
	Animation<TextureRegion> moveAnimation;
	Animation<TextureRegion> deathAnimation;
	TextureRegion current;
	float stateTime;
	Dimension dimension;
	public Enemy(TextureAtlas atlas,int health,Dimension dimension) {
		this.atlas = atlas;
		this.health = health;
		this.dimension = dimension;
		dead =false;
		stateTime = 0;
	}
	abstract void think(float delta);
	abstract void draw(SpriteBatch batch, Dimension dimension);
	
	public void die() {
		stateTime = 0;
		dead = true;
	}
	
	@Override
	public void hit(Bullet b) {
		health -= b.damage;
		body.applyLinearImpulse(b.velocity,body.getWorldCenter(),true);
	}
	
	@Override
	public Body getBody() {
		return body;
	}
}
