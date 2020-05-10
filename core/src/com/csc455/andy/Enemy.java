package com.csc455.andy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

abstract class Enemy implements Hittable, Disposable {
	TextureAtlas atlas;
	Body body;
	int health;
	boolean dead;
	Animation<TextureRegion> moveAnimation;
	Animation<TextureRegion> deathAnimation;
	TextureRegion current;
	float stateTime;
	Vector2 size,position;
	Dimension dimension;
	public Enemy(TextureAtlas atlas,int health,Dimension dimension) {
		this.atlas = atlas;
		this.health = health;
		this.dimension = dimension;
		TextureRegion idle=null;
		for(TextureAtlas.AtlasRegion reg : atlas.getRegions()) {
			if(reg.name.toLowerCase().contains("idle"))
				idle = reg;
		}
		
		assert idle != null;
		size = Utils.gameToBox(new Vector2(idle.getRegionWidth(),idle.getRegionHeight()));
		dead =false;
		stateTime = 0;
	}
	abstract void think(float delta);
	abstract void draw(SpriteBatch batch, Dimension dimension);
	
	public void die() {
		stateTime = 0;
		dead = true;
		Array<Fixture> fixtures = body.getFixtureList();
		fixtures.forEach(p -> {
			p.getFilterData().groupIndex=-5;
			body.destroyFixture(p);
		});
	}
	
	@Override
	public void hit(Bullet b) {
		System.out.println("hit");
		health -= b.damage;
		body.applyLinearImpulse(b.velocity.scl(b.body.getMass()*2),body.getWorldCenter(),true);
	}
	
	@Override
	public Body getBody() {
		return body;
	}
	
	@Override
	public void dispose() {
		atlas.dispose();
	}
}
