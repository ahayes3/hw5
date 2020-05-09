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
	private final Vector2 barrel, grip;
	Vector2 size;
	Dimension dimension;
	
	public Gun(int ammo, int damage, float delay, int slot, TextureAtlas atlas, Firemode firemode,Vector2 position,Vector2 barrelRatio,Vector2 gripRatio,float scale,Dimension dimension,World world) {
		this.ammo = ammo;
		this.damage = damage;
		this.firemode =firemode;
		this.delay = delay;
		this.slot = slot;
		this.scale = scale;
		
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
		
		size = Coords.gameToBox(new Vector2(idle.getRegionWidth()*scale,idle.getRegionHeight()*scale));
		grip = new Vector2(size.x * gripRatio.x, size.y * gripRatio.y);
		barrel = new Vector2( size.x * barrelRatio.x, size.y * barrelRatio.y);
		BodyDef bdef = new BodyDef();
		bdef.type = BodyDef.BodyType.DynamicBody;
		bdef.position.set(Coords.gameToBox(position));
		bdef.gravityScale = 0;
		body = world.createBody(bdef);
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
		local.add(grip);
		body.setTransform(body.getWorldPoint(local),angle);
	}
	public void addAmmo(int ammo) {
		this.ammo += ammo;
	}
	public void shoot(World world,Vector2 position,Vector2 angle,Body owner) {
		if(firingAnimation.isAnimationFinished(stateTime)) {
			stateTime = 0;
			fire(world,position,angle,owner);
		}
	}
	public Vector2 getGrip() {
		return body.getWorldPoint(grip);
	}
	public Vector2 getBarrel() {
		return body.getWorldPoint(barrel);
	}
	public abstract void fire(World world, Vector2 position, Vector2 angle, Body owner);
	public void draw(SpriteBatch batch) {
		//Vector2 pos = Coords.boxToGame(body.getPosition());
		Vector2 pos = Coords.boxToGame(body.getWorldPoint(new Vector2(-size.x/2f,-size.y/2f)));
		batch.draw(current,pos.x,pos.y,0,0,current.getRegionWidth(),current.getRegionHeight(),scale,scale, (float) (body.getAngle() * (180/Math.PI)));
	}
}
