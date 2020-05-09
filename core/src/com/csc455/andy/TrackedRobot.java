package com.csc455.andy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

public class TrackedRobot extends Enemy implements Hittable{
	
	public TrackedRobot(TextureAtlas atlas, int health, Dimension dimension, Vector2 position) {
		super(atlas, health,dimension);
		Array<TextureAtlas.AtlasRegion> regions = atlas.getRegions();
		Array<TextureRegion> deaths = new Array<>();
		Array<TextureRegion> moves = new Array<>();
		for(TextureAtlas.AtlasRegion reg :regions) {
			if(reg.name.toLowerCase().contains("death"))
				deaths.add(reg);
			else if(reg.name.toLowerCase().contains("move"))
				moves.add(reg);
		}
		moveAnimation = new Animation<TextureRegion>(.2f,moves);
		moveAnimation.setPlayMode(Animation.PlayMode.LOOP);
		deathAnimation = new Animation<TextureRegion>(.1f,deaths);
		deathAnimation.setPlayMode(Animation.PlayMode.NORMAL);
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(Utils.gameToBox(position));
		bodyDef.fixedRotation = true;
		PolygonShape shape = new PolygonShape();
		TextureRegion tmp = moveAnimation.getKeyFrame(0);
		Vector2 size = Utils.gameToBox(tmp.getRegionWidth()/2f,tmp.getRegionHeight()/2f);
		shape.setAsBox(size.x,size.y);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 1;
		fixtureDef.shape=shape;
		fixtureDef.filter.categoryBits = Utils.ENEMY_BITS;
		
	}
	@Override
	void think(float delta) {
		stateTime +=delta;
		if(health < 1)
			die();
		if(body.getLinearVelocity().x < 0) {
			moveAnimation.setPlayMode(Animation.PlayMode.LOOP_REVERSED);
		}
		else if(body.getLinearVelocity().x > 0) {
			moveAnimation.setPlayMode(Animation.PlayMode.LOOP);
		}
		if(dead)
			current = deathAnimation.getKeyFrame(stateTime);
		else if(body.getLinearVelocity().x != 0)
			current = moveAnimation.getKeyFrame(stateTime);
		
		
	}
	
	@Override
	void draw(SpriteBatch batch, Dimension dimension) {
		if(MainGame.dimension == dimension) {
			//draw
		}
	}
}
