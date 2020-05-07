package com.csc455.andy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

enum Movement {
	STANDING,WALKING,SPRINTING;
}
public class Player implements Disposable {
	TextureAtlas atlas;
	Animation<TextureRegion> walkAnimation;
	TextureRegion standing;
	TextureRegion current;
	Vector2 aim,position,velocity;
	Movement movement;
	Vector3 unprojector;
	Rectangle collider;
	float sprintMul;
	float stateTime;
	public Player(TextureAtlas atlas) {
		aim = new Vector2(1,0);
		position = new Vector2(8,9);
		velocity = Vector2.Zero;
		Array<TextureRegion> walkingRegions = new Array<>();
		for(TextureAtlas.AtlasRegion a : atlas.getRegions()) {
			if(a.name.contains("Walking"))
				walkingRegions.add(a);
		}
		walkAnimation = new Animation<TextureRegion>(.1f,walkingRegions);
		this.standing = atlas.findRegion("StandingStill");
		movement = Movement.STANDING;
		this.atlas = atlas;
		unprojector = new Vector3();
		current = standing;
		sprintMul = 1.428f;
		collider = new Rectangle(position.x,position.y,current.getRegionWidth(),current.getRegionHeight());
	}
	public void move(OrthographicCamera camera, MyMap map) {
		final float velMod = 2.25f;
		boolean sprinting = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
		if(sprinting && Gdx.input.isKeyPressed(Input.Keys.D)) {
			velocity.x += sprintMul * velMod;
			movement = Movement.SPRINTING;
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.D))
			velocity.x += velMod;

		if(sprinting && Gdx.input.isKeyPressed(Input.Keys.A)) {
			movement = Movement.SPRINTING;
			velocity.x -= sprintMul * velMod;
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.A))
			velocity.x -= velMod;


		if(Math.abs(velocity.x) < 1) {
			velocity.x = 0;
			movement = Movement.STANDING;
		}
		else if(!Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.D) && velocity.x >0)
			velocity.x -= 1.5f*velMod;
		else if (!Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.D) && velocity.x <0)
			velocity.x += 1.5f*velMod;

		if(velocity.x > 500)
			velocity.x = 500;
		else if(velocity.x < -500)
			velocity.x = -500;

		Vector3 unprojected = camera.unproject(unprojector.set(Gdx.input.getX(),Gdx.input.getY(),0));
		aim.set(unprojected.x - position.x,unprojected.y - position.y);
		if(velocity.x != 0 && movement != Movement.SPRINTING)
			movement = Movement.WALKING;

		velocity.y -= .5;

		TiledMapTileLayer collisionLayer = (TiledMapTileLayer) map.map.getLayers().get("collision");

		collider.set(position.x,position.y,current.getRegionWidth(),current.getRegionHeight());
		Vector2 nextPos = position.cpy().add(velocity.x *Gdx.graphics.getDeltaTime(),velocity.y * Gdx.graphics.getDeltaTime());
		
		position = nextPos;
		//todo change to set position to nextPos position.add(velocity.x *Gdx.graphics.getDeltaTime(),velocity.y * Gdx.graphics.getDeltaTime());
		camera.position.set(position.x,position.y,0);
	}
	public void draw(SpriteBatch batch) {//batch must already have begun and must be ended after
		if(movement == Movement.SPRINTING)
			walkAnimation.setFrameDuration(.07f);
		else if(movement == Movement.WALKING)
			walkAnimation.setFrameDuration(.1f);
		
		if(movement != Movement.STANDING) {
			stateTime += Gdx.graphics.getDeltaTime();
			current = walkAnimation.getKeyFrame(stateTime,true);
		}
		else {
			stateTime = 0;
			current = standing;
		}
		if((aim.angle() <= 90 || aim.angle() > 270) && current.isFlipX())
			current.flip(true,false);
		else if(aim.angle() >=90 && aim.angle() < 270 && !current.isFlipX())
			current.flip(true,false);
		
		batch.draw(current,position.x,position.y,4f,7f,current.getRegionWidth(),current.getRegionHeight(),1,1,0);
	}
	
	
	@Override
	public void dispose() {
		atlas.dispose();
	}
}
