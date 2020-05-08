package com.csc455.andy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Bullet {
	
	Vector2 position,velocity;
	int damage;
	Body body,owner;
	float width,height;
	TextureRegion textureRegion;
	public Bullet(Vector2 position, Vector2 velocity,float width,float height, int damage, TextureRegion textureRegion,Body owner, World world) {
		this.position = position;
		this.velocity = velocity;
		this.textureRegion = textureRegion;
		this.damage = damage;
		this.width = width;
		this.height = height;
		this.owner = owner;
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.bullet = true;
		bodyDef.position.set(position);
		bodyDef.linearVelocity.set(velocity);
		bodyDef.angle = velocity.angleRad();
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width,height);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1;
		shape.dispose();
		
		
		body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);
		body.setUserData(this);
	}
	public void draw(SpriteBatch batch) {
		position = Coords.boxToGame(body.getPosition().x,body.getPosition().y);
		batch.draw(textureRegion,position.x,position.y,0,textureRegion.getRegionHeight()/2f,width,height,1,1,(velocity.angle()));
	}
}
