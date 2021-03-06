package com.csc455.andy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

enum Movement {
	STANDING, WALKING, SPRINTING;
}

public class Player implements Disposable {
	TextureAtlas atlas;
	Animation<TextureRegion> walkAnimation;
	TextureRegion standing,current,arm;
	Vector2 aim, position, velocity;
	Movement movement;
	Vector3 unprojector;
	float sprintMul, stateTime;
	Vector2 right, left, up, down;
	int height, width,health;
	Body body;
	Array<Gun> inventory;
	Gun selection;
	
	public Player(TextureAtlas atlas, World world) {
		inventory = new Array<>(5);
		aim = new Vector2(1, 0);
		position = new Vector2(8, 9);
		velocity = Vector2.Zero;
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
		arm = new TextureRegion(new Texture(Gdx.files.internal("sprites"+ MainGame.fs+"pc"+MainGame.fs+"Arm.png")));
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(Coords.gameToBox(position.x + 4f, position.y + 7.5f));
		body = world.createBody(bodyDef);
		body.setUserData(this);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(.875f,  1.75f);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1;
		fixtureDef.friction =0;
		Fixture fixture = body.createFixture(fixtureDef);
		left = new Vector2(-1, 0);
		right = new Vector2(1, 0);
		up = new Vector2(0, 1);
		down = new Vector2(0, -1);
		body.setFixedRotation(true);
		height = 14;
		health = 100;
		width = 7;
	}
	public boolean pickup(Gun gun) {
		if(inventory.get(gun.slot)!=null)
			return false;
		inventory.set(gun.slot,gun);
		return true;
	}
	public boolean pickup(Ammo ammo) {
		if(inventory.get(ammo.slot) == null)
			return false;
		inventory.get(ammo.slot).addAmmo(ammo.quantity);
		return true;
	}
	public void update(OrthographicCamera camera,float delta) {
		if(!dead())
			move(camera,delta);
		else {
			movement = Movement.STANDING;
		}
		
	}
	public void move(OrthographicCamera camera,float delta) {
		right.set(1, 0);
		left.set(-1, 0);
		up.set(0, 1);
		down.set(-1, 0);
		float velClamp = 12;
		final float velMod = 30;
		float sprintMul = 1;
		if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
			sprintMul = 1.428f;
		float oldVx = velocity.x;
		velClamp *= sprintMul;
		if (Gdx.input.isKeyPressed(Input.Keys.D) && velocity.x < velClamp)
			velocity.x += sprintMul * velMod * delta;
		if (Gdx.input.isKeyPressed(Input.Keys.A) && velocity.x > -velClamp)
			velocity.x -= sprintMul * velMod * delta;
		if (!Gdx.input.isKeyPressed(Input.Keys.D) && velocity.x > 0) {
			velocity.x -= 1.428 * velMod * 3 * delta;
		}
		else if (!Gdx.input.isKeyPressed(Input.Keys.A) && velocity.x < 0) {
			velocity.x += 1.428 * velMod * 3 * delta;
		}
		
		if(!(Gdx.input.isKeyPressed(Input.Keys.D)||Gdx.input.isKeyPressed(Input.Keys.A)) && Math.signum(oldVx) != Math.signum(velocity.x)) {
			velocity.x = 0;
			movement = Movement.STANDING;
		}
		
		if(sprintMul > 1.2 && velocity.x != 0)
			movement = Movement.SPRINTING;
		else if(velocity.x != 0)
			movement = Movement.WALKING;
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
			body.setLinearVelocity(body.getLinearVelocity().x,10);
		
		body.setLinearVelocity(velocity.x, body.getLinearVelocity().y);
		
		Vector3 unprojected = camera.unproject(unprojector.set(Gdx.input.getX(),Gdx.input.getY(),0));
		aim.set(unprojected.x - (position.x + width/2f),unprojected.y - (position.y + height/2f));
		
		position = Coords.boxToGame(body.getPosition().x, body.getPosition().y).sub(0,height);
		camera.position.set(position.x, position.y, 0);
		
		if(selection.firemode == Gun.Firemode.AUTO && Gdx.input.isTouched())
			selection.shoot();
		else if(Gdx.input.justTouched())
			selection.shoot();
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
		if ((aim.angle() <= 90 || aim.angle() > 270) && current.isFlipX()) {
			current.flip(true, false);
		}
		else if (aim.angle() >= 90 && aim.angle() < 270 && !current.isFlipX()) {
			current.flip(true, false);
		}
		
		batch.draw(current, position.x, position.y, 4f, 7f, current.getRegionWidth(), current.getRegionHeight(), 1, 1, 0);
		batch.draw(arm,position.x+width/2f,position.y +height/2f,arm.getRegionWidth(),arm.getRegionHeight()/2f,arm.getRegionWidth(),arm.getRegionHeight(),1,1,aim.angle());
		Vector2 handPos = new Vector2(position.x +width/2f,position.y+height/2f);
		handPos.add(aim.setLength(arm.getRegionWidth()));
		selection.draw(batch,handPos,handPos.sub(aim),aim.angle());
	}
	public boolean dead() {
		return health <= 0;
	}
	
	
	@Override
	public void dispose() {
		atlas.dispose();
		
	}
}
