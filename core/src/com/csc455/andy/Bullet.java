package com.csc455.andy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Bullet {
	static TextureRegion textureRegion;
	static float scale = .3f;
	Vector2 position, velocity;
	int damage;
	Body owner, body;
	boolean destroyed;
	Dimension dimension;
	Vector2 size;
	
	public Bullet(Vector2 position, Vector2 velocity, int damage, Body owner,Dimension dimension, World world) {
		this.position = position;
		this.owner = owner;
		this.damage = damage;
		this.velocity = velocity;
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.bullet = true;
		bodyDef.gravityScale = 0;
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.fixedRotation = true;
		bodyDef.linearVelocity.set(velocity);
		bodyDef.position.set(position.x, position.y);
		bodyDef.angle = velocity.angleRad();
		PolygonShape shape = new PolygonShape();
		size = Utils.gameToBox(textureRegion.getRegionWidth(), textureRegion.getRegionHeight()).scl(scale);
		shape.setAsBox(size.x, size.y);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		Filter tmp = owner.getFixtureList().get(0).getFilterData();
		
		
		short maskbits=0;
		if((tmp.maskBits & Utils.PRESENT_BITS) != 0)
			maskbits = Utils.PRESENT_BITS;
		else if((tmp.maskBits & Utils.PAST_BITS) != 0)
			maskbits = Utils.PAST_BITS;
		
		fixtureDef.filter.categoryBits = (short) (Utils.BULLET_BITS|maskbits);
		fixtureDef.filter.maskBits = maskbits;
		fixtureDef.density = .1f;
		
		this.dimension = dimension;
		body = world.createBody(bodyDef);
		body.setUserData(this);
		
		body.createFixture(fixtureDef);
		shape.dispose();
		destroyed = false;
	}
	
	public void destroy() {
//		body.setTransform(0, 0, 0);
		body.getFixtureList().clear();
		destroyed = true;
	}
	
	public void draw(SpriteBatch batch) {
		if(body.getPosition().sub(owner.getPosition()).len2() > 1000*1000)
			destroy();
		
		if (!destroyed) {
			Vector2 pos = Utils.boxToGame(body.getWorldPoint(body.getLocalPoint(body.getPosition()).sub(size.x, size.y)));
			batch.draw(textureRegion, pos.x, pos.y, 0, 0,size.x*Utils.pixelRatio, size.y*Utils.pixelRatio, 1, 1, (float) (body.getAngle() * (180 / Math.PI)));
		}
	}
}
