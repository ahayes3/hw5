package com.csc455.andy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class TrackedRobot extends Enemy implements Hittable {
	float shotDelta;
	Player player;
	Laser laser;
	public TrackedRobot(int health, Dimension dimension, Vector2 position,Player player, World world) {
		super(new TextureAtlas("sprites/enemies/trackedRobot/TrackedRobot.atlas"), health,dimension);
		this.player = player;
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
		deathAnimation = new Animation<TextureRegion>(.3f,deaths);
		deathAnimation.setPlayMode(Animation.PlayMode.NORMAL);
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(Utils.gameToBox(position));
		bodyDef.fixedRotation = true;
		PolygonShape shape = new PolygonShape();
		TextureRegion tmp = moveAnimation.getKeyFrame(1);
		Vector2 size = Utils.gameToBox(tmp.getRegionWidth()/2f,tmp.getRegionHeight()/2f);
		shape.setAsBox(size.x,size.y);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 1;
		fixtureDef.shape=shape;
		
		if(dimension ==  Dimension.PAST) {
			fixtureDef.filter.maskBits = Utils.PAST_BITS | Utils.BULLET_BITS;
			fixtureDef.filter.categoryBits = Utils.ENEMY_BITS;
		}
		else if (dimension == Dimension.PRESENT) {
			fixtureDef.filter.maskBits = Utils.PRESENT_BITS | Utils.BULLET_BITS;
			fixtureDef.filter.categoryBits = Utils.ENEMY_BITS|Utils.PRESENT_BITS;
		}
		
		body = world.createBody(bodyDef);
		body.setUserData(this);
		body.createFixture(fixtureDef);
		current = moveAnimation.getKeyFrame(0);
		shape.dispose();
		
	}
	@Override
	void think(float delta,World world) {
		stateTime +=delta;
		shotDelta += delta;
		if(laser != null)
			laser.timer += delta;
		
		if(health < 1 && !dead)
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
		
		if(!dead) {
			position = Utils.boxToGame(body.getPosition().cpy());
			
			//do stuff
			Box<Boolean> bool = new Box<>();
			CanSeeFixtureCB cb = new CanSeeFixtureCB(player.body.getFixtureList(),bool);
			world.rayCast(cb,body.getPosition(),player.body.getPosition());
			if(body.getPosition().cpy().sub(player.body.getPosition()).len2() < 1000 && laser == null && shotDelta > 2f && bool.value)
				shoot(Utils.boxToGame(player.body.getPosition().cpy()),player);
			
		}
		
		if(laser != null && laser.timer > .5f) {
			laser = null;
			shotDelta = 0;
		}
	}
	public void shoot(Vector2 position, Hittable hittable) {
		hittable.subHealth(10);
		laser = new Laser(position,Utils.boxToGame(getEye()),2);
	}
	public Vector2 getEye() {
		return body.getWorldPoint(new Vector2(0,size.y*(11.5f/27f)));
	}
	@Override
	void draw(SpriteBatch batch, Dimension dimension) {
		if(this.dimension == dimension) {
			Vector2 pos = position.cpy().sub(current.getRegionWidth()/2f,current.getRegionHeight()/2f);
			batch.draw(current,pos.x,pos.y,current.getRegionWidth()/2f,current.getRegionHeight()/2f,current.getRegionWidth(),current.getRegionHeight(),1,1, (float) (body.getAngle() * (180/Math.PI)));
		}
	}
	
	@Override
	void drawShapes(ShapeRenderer sr,Dimension dimension) {
		if(this.dimension == dimension && laser != null)
			laser.draw(sr);
	
	}
	
	@Override
	public void subHealth(int health) {
		this.health -= health;
	}
}
