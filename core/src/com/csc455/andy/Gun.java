package com.csc455.andy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
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
	Array<Bullet> bullets;
	Body body;
	float scale;
	Vector2 barrel, grip;
	Vector2 size;
	Dimension dimension;
	boolean flipped;
	
	public Gun(int ammo, int damage, float delay, int slot, TextureAtlas atlas, Firemode firemode,Vector2 position,Vector2 barrelRatio,Vector2 gripRatio,float scale,Dimension dimension,World world) {
		this.ammo = ammo;
		this.damage = damage;
		this.firemode =firemode;
		this.delay = delay;
		this.slot = slot;
		this.scale = scale;
		this.flipped = false;
		
		stateTime = 0;
		timer = .3f;
		bullets = new Array<>();
		
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
		
		size = Utils.gameToBox(new Vector2(idle.getRegionWidth()*scale,idle.getRegionHeight()*scale));
		grip = new Vector2(scale*size.x * gripRatio.x, scale* size.y * gripRatio.y);
		barrel = new Vector2( scale* size.x * barrelRatio.x, scale* size.y * barrelRatio.y);
		BodyDef bdef = new BodyDef();
		bdef.type = BodyDef.BodyType.DynamicBody;
		bdef.position.set(Utils.gameToBox(position));
		bdef.gravityScale = 0;
		body = world.createBody(bdef);
		body.setUserData(this);
		PolygonShape ps = new PolygonShape();
		ps.setAsBox((size.x/2f),(size.y/2f));
		FixtureDef fdef = new FixtureDef();
		fdef.shape = ps;
		fdef.density=.1f;
		fdef.filter.maskBits =0;
		fdef.filter.categoryBits = 5;
		body.createFixture(fdef);
		
	}
	public void update(float delta) {
		timer += delta;
		stateTime += delta;
		current =firingAnimation.getKeyFrame(stateTime,false);
		
	}
	public void update(float delta,Dimension dimension) {
		this.dimension = dimension;
		update(delta);
	}
	public void setPosition(Vector2 pos,float angle) {
		setPosition(pos.x,pos.y,angle);
	}
	//use box coords
	public void setPosition(float x,float y,float angle) {
		Vector2 local = body.getLocalPoint(new Vector2(x,y));
		if(flipped)
			local.add(grip.x,-grip.y);
		else
			local.add(grip);
		body.setTransform(body.getWorldPoint(local),angle);
	}
	public void addAmmo(int ammo) {
		this.ammo += ammo;
	}
	public Bullet shoot(World world,Dimension dimension,Body owner) {
		if(firingAnimation.isAnimationFinished(stateTime)) {
			stateTime = 0;
			Bullet b = fire(world,dimension,owner);
			bullets.add(b);
			return b;
		}
		return null;
	}
	public abstract Bullet fire(World world,Dimension dimension, Body owner);
	
	public Vector2 getGrip() {
		return body.getWorldPoint(grip);
	}
	public Vector2 getBarrel() {
		return body.getWorldPoint(barrel);
	}
	public Bullet createBullet(Vector2 position,Vector2 velocity,Body owner,Dimension dimension,World world) {
	return new Bullet(position,velocity,damage,owner,dimension,world);
	}
	public void draw(SpriteBatch batch) {
		//Vector2 pos = Coords.boxToGame(body.getPosition());
		System.out.println(body.getAngle());
		if((body.getAngle() <= Math.PI/2 && body.getAngle() > -Math.PI/2 ) && current.isFlipY()) {
			current.flip(false, true);
			flipped =false;
		}
		else if((body.getAngle() > Math.PI/2 || body.getAngle() < -Math.PI/2) && !current.isFlipY()) {
			current.flip(false, true);
			flipped = true;
		}
		
		Vector2 pos = Utils.boxToGame(body.getWorldPoint(new Vector2(-size.x/2f,-size.y/2f)));
		batch.draw(current,pos.x,pos.y,0,0,current.getRegionWidth(),current.getRegionHeight(),scale,scale, (float) (body.getAngle() * (180/Math.PI)));
		bullets.forEach(p -> p.draw(batch));
	}
}
