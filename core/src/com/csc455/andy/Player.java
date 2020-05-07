package com.csc455.andy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

enum Movement {
	STANDING, WALKING, SPRINTING;
}

public class Player implements Disposable {
	TextureAtlas atlas;
	Animation<TextureRegion> walkAnimation;
	TextureRegion standing;
	TextureRegion current;
	Vector2 aim, position, velocity;
	Movement movement;
	Vector3 unprojector;
	Rectangle collider;
	float sprintMul;
	float stateTime;
	
	public Player(TextureAtlas atlas) {
		aim = new Vector2(1, 0);
		position = new Vector2(8, 9);
		velocity = new Vector2(0, 0);
		Array<TextureRegion> walkingRegions = new Array<>();
		for (TextureAtlas.AtlasRegion a : atlas.getRegions()) {
			if (a.name.contains("Walking"))
				walkingRegions.add(a);
		}
		walkAnimation = new Animation<TextureRegion>(.1f, walkingRegions);
		this.standing = atlas.findRegion("StandingStill");
		movement = Movement.STANDING;
		this.atlas = atlas;
		unprojector = new Vector3();
		current = standing;
		sprintMul = 1.428f;
		collider = new Rectangle(position.x, position.y, current.getRegionWidth(), current.getRegionHeight());
	}
	
	public void move(OrthographicCamera camera, MyMap map) {
		final float velMod = 2.25f;
		boolean sprinting = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
		
		if (sprinting && Gdx.input.isKeyPressed(Input.Keys.D)) {
			velocity.x += sprintMul * velMod;
			movement = Movement.SPRINTING;
		}
		else if (Gdx.input.isKeyPressed(Input.Keys.D))
			velocity.x += velMod;
		
		if (sprinting && Gdx.input.isKeyPressed(Input.Keys.A)) {
			movement = Movement.SPRINTING;
			velocity.x -= sprintMul * velMod;
		}
		else if (Gdx.input.isKeyPressed(Input.Keys.A))
			velocity.x -= velMod;
		
		
		if (Math.abs(velocity.x) < 1) {
			velocity.x = 0;
			movement = Movement.STANDING;
		}
		else if (!Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.D) && velocity.x > 0)
			velocity.x -= 1.5f * velMod;
		else if (!Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.D) && velocity.x < 0)
			velocity.x += 1.5f * velMod;
		
		if (velocity.x > 500)
			velocity.x = 500;
		else if (velocity.x < -500)
			velocity.x = -500;
		
		Vector3 unprojected = camera.unproject(unprojector.set(Gdx.input.getX(), Gdx.input.getY(), 0));
		aim.set(unprojected.x - position.x, unprojected.y - position.y);
		if (velocity.x != 0 && movement != Movement.SPRINTING)
			movement = Movement.WALKING;
		
		velocity.y -= 1;

//		if(!map.tilesNear(collider).isEmpty())
//			System.out.println(map.tilesNear(collider));
		
		
		Vector2 oldPos = position.cpy();
		position.add(velocity.x * Gdx.graphics.getDeltaTime(), velocity.y * Gdx.graphics.getDeltaTime());
		
//		float tileWidth = map.collisionLayer.getTileWidth(), tileHeight = map.collisionLayer.getTileHeight();
//		boolean xCollision = false, yCollision = false;
//		TiledMapTileLayer.Cell[] cells = new TiledMapTileLayer.Cell[12];
//		cells[0] = map.collisionLayer.getCell((int) (position.x / tileWidth), (int) ((position.y + collider.height) / tileHeight));
//		cells[1] = map.collisionLayer.getCell((int) (position.x / tileWidth), (int) ((position.y + collider.height / 2) / tileHeight));
//		cells[2] = map.collisionLayer.getCell((int) (position.x / tileWidth), (int) (position.y / tileHeight));
//		cells[3] = map.collisionLayer.getCell((int) ((position.x + collider.width) / tileWidth), (int) ((position.y + collider.height) / tileHeight));
//		cells[4] = map.collisionLayer.getCell((int) ((position.x + collider.width) / tileWidth), (int) ((position.y + collider.height / 2) / tileHeight));
//		cells[5] = map.collisionLayer.getCell((int) ((position.x + collider.width) / tileWidth), (int) (position.y / tileHeight));
//		cells[6] = map.collisionLayer.getCell((int) (position.x / tileWidth), (int) (position.y / tileHeight));
//		cells[7] = map.collisionLayer.getCell((int) ((position.x + collider.width / 2) / tileWidth), (int) (position.y / tileHeight));
//		cells[8] = map.collisionLayer.getCell((int) ((position.x + collider.width) / tileWidth), (int) (position.y / tileHeight));
//		cells[9] = map.collisionLayer.getCell((int) (position.x / tileWidth), (int) ((position.y + collider.height) / tileHeight));
//		cells[10] = map.collisionLayer.getCell((int) ((position.x + collider.width / 2) / tileWidth), (int) ((position.y + collider.height) / tileHeight));
//		cells[11] = map.collisionLayer.getCell((int) ((position.x + collider.width) / tileWidth), (int) ((position.y + collider.height) / tileHeight));
//
//		if (velocity.x < 0) {
//			xCollision = cells[0] != null && cells[0].getTile().getProperties().containsKey("block");
//			if (!xCollision)
//				xCollision = cells[1] != null && cells[1].getTile().getProperties().containsKey("block");
//			if (!xCollision)
//				xCollision = cells[2] != null && cells[2].getTile().getProperties().containsKey("block");
//		}
//		else if (velocity.x > 0) {
//			xCollision = cells[3] != null && cells[3].getTile().getProperties().containsKey("block");
//			if (!xCollision)
//				xCollision = cells[4] != null && cells[4].getTile().getProperties().containsKey("block");
//			if (!xCollision)
//				xCollision = cells[5] != null && cells[5].getTile().getProperties().containsKey("block");
//		}
//
//		if (xCollision) {
//			velocity.x = 0;
//			position.x = oldPos.x;
//		}
//
//		if (velocity.y < 0) {
//			yCollision = cells[6] != null && cells[6].getTile().getProperties().containsKey("block");
//			if (!yCollision)
//				yCollision = cells[7] != null && cells[7].getTile().getProperties().containsKey("block");
//			if (!yCollision)
//				yCollision = cells[8] != null && cells[8].getTile().getProperties().containsKey("block");
//		}
//		else if (velocity.y > 0) {
//			if (velocity.y < 0) {
//				yCollision = cells[9] != null && cells[9].getTile().getProperties().containsKey("block");
//				if (!yCollision)
//					yCollision = cells[10] != null && cells[10].getTile().getProperties().containsKey("block");
//				if (!yCollision)
//					yCollision = cells[11] != null && cells[11].getTile().getProperties().containsKey("block");
//			}
//		}
//		if (yCollision) {
//			velocity.y = 0;
//			position.y = oldPos.y;
//		}
		
		
		collider.set(position.x, position.y, current.getRegionWidth(), current.getRegionHeight());
		//todo change to set position to nextPos position.add(velocity.x *Gdx.graphics.getDeltaTime(),velocity.y * Gdx.graphics.getDeltaTime());
		camera.position.set(position.x, position.y, 0);
	}
	
	public void draw(SpriteBatch batch) {//batch must already have begun and must be ended after
		if (movement == Movement.SPRINTING)
			walkAnimation.setFrameDuration(.07f);
		else if (movement == Movement.WALKING)
			walkAnimation.setFrameDuration(.1f);
		
		if (movement != Movement.STANDING) {
			stateTime += Gdx.graphics.getDeltaTime();
			current = walkAnimation.getKeyFrame(stateTime, true);
		}
		else {
			stateTime = 0;
			current = standing;
		}
		if ((aim.angle() <= 90 || aim.angle() > 270) && current.isFlipX())
			current.flip(true, false);
		else if (aim.angle() >= 90 && aim.angle() < 270 && !current.isFlipX())
			current.flip(true, false);
		
		batch.draw(current, position.x, position.y, 4f, 7f, current.getRegionWidth(), current.getRegionHeight(), 1, 1, 0);
	}
	
	public void debugDraw(ShapeRenderer sr) {
		sr.rect(collider.x, collider.y, collider.width, collider.height);
	}
	
	
	@Override
	public void dispose() {
		atlas.dispose();
	}
}
