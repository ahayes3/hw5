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

public class Player implements Disposable,Hittable {
	TextureAtlas atlas;
	Animation<TextureRegion> walkAnimation;
	TextureRegion standing, current,armRegion;
	Vector2 aim, position, velocity;
	Movement movement;
	Vector3 unprojector;
	float sprintMul, stateTime;
	Vector2 right, left, up, down;
	int height, width, health;
	Body body,arm;
	Array<Gun> inventory;
	boolean jumped;
	Gun selection;
	Dimension dimension;
	
	public Player(TextureAtlas atlas, World world) {
		//todo if in past fixture.filter with past else filter with current
		inventory = new Array<>();
		inventory.setSize(5);
		aim = new Vector2(1, 0);
		position = new Vector2(8, 20);
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
		armRegion = new TextureRegion(new Texture("sprites/pc/Arm.png"));
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(Utils.gameToBox(position.x + standing.getRegionWidth() / 2f, position.y + standing.getRegionHeight() / 2f));
		body = world.createBody(bodyDef);
		body.setUserData(this);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox((standing.getRegionWidth() / 2f) / Utils.pixelRatio, (standing.getRegionHeight() / 2f) / Utils.pixelRatio);
		FixtureDef fixtureDef = new FixtureDef();
		dimension = Dimension.PRESENT;
		fixtureDef.shape = shape;
		fixtureDef.filter.categoryBits = Utils.PLAYER_BITS;
		fixtureDef.filter.maskBits = Utils.PRESENT_BITS;
		fixtureDef.density = 1;
		fixtureDef.friction = 0;
		Fixture fixture = body.createFixture(fixtureDef);
		left = new Vector2(-1, 0);
		right = new Vector2(1, 0);
		up = new Vector2(0, 1);
		down = new Vector2(0, -1);
		body.setFixedRotation(true);
		height = 14;
		health = 100;
		jumped = false;
		width = 7;
		
		bodyDef.position.set(Utils.gameToBox(position.add(width / 2f, height / 2f).add(3, 3)));
		fixtureDef.filter.groupIndex = -5;
		fixtureDef.density = .001f;
		armRegion.setRegionWidth((int) (armRegion.getRegionWidth()*1.3f));
		armRegion.setRegionHeight((int) (armRegion.getRegionHeight()*1.3f));
		Vector2 size = Utils.gameToBox(armRegion.getRegionWidth()/2f, armRegion.getRegionHeight()/2f);
		shape.setAsBox(size.x, size.y,new Vector2(size.x,0),0);
		
		fixtureDef.shape = shape;
		arm = world.createBody(bodyDef);
		arm.setGravityScale(0);
		arm.createFixture(fixtureDef);
		shape.dispose();
	}
	
	public boolean pickup(Gun gun) {
		if (inventory.get(gun.slot) != null)
			return false;
		inventory.set(gun.slot, gun);
		return true;
	}
	
	public boolean pickup(Ammo ammo) {
		if (inventory.get(ammo.slot) == null)
			return false;
		inventory.get(ammo.slot).addAmmo(ammo.quantity);
		return true;
	}
	
	public void update(OrthographicCamera camera, boolean present, MyMap map,World world, float delta) {
		if (!dead()) {
			move(camera, world, delta);
			Filter f = new Filter();
			
			if (present && (body.getFixtureList().get(0).getFilterData().maskBits & Utils.PRESENT_BITS) == 0) {
//				f.categoryBits = Utils.PRESENT_BITS | Utils.PLAYER_BITS;
//				f.maskBits = Utils.PRESENT_BITS;
				f.categoryBits = Utils.PLAYER_BITS;
				f.maskBits = Utils.PRESENT_BITS;
				body.getFixtureList().get(0).setFilterData(f);
				map.swapDraw();
			}
			else if(!present && (body.getFixtureList().get(0).getFilterData().maskBits & Utils.PAST_BITS) == 0) {
//				f.categoryBits = MainGame.PAST;
//				f.maskBits = MainGame.BULLETMASK|MainGame.PAST;
				f.categoryBits = Utils.PLAYER_BITS;
				f.maskBits = Utils.PAST_BITS;
				body.getFixtureList().get(0).setFilterData(f);
				map.swapDraw();
			}
			
		}
		else {
			movement = Movement.STANDING;
		}
	}
	public PlayerState saveState() {
		return new PlayerState(body.getPosition(),body.getLinearVelocity(),inventory,health,dimension);
	}
	public void setState(PlayerState state) {
		body.setTransform(state.position,body.getAngle());
		body.setLinearVelocity(state.velocity);
		health = state.health;
		dimension = state.dimension;
	}
	public void move(OrthographicCamera camera,World world, float delta) {
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
		
		if (!(Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.A)) && Math.signum(oldVx) != Math.signum(velocity.x)) {
			velocity.x = 0;
			movement = Movement.STANDING;
		}
		
		if (sprintMul > 1.2 && velocity.x != 0)
			movement = Movement.SPRINTING;
		else if (velocity.x != 0)
			movement = Movement.WALKING;
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
			body.setLinearVelocity(body.getLinearVelocity().x, 13);
		
		body.setLinearVelocity(velocity.x, body.getLinearVelocity().y);
		
		Vector3 unprojected = camera.unproject(unprojector.set(Gdx.input.getX(), Gdx.input.getY(), 0));
		aim.set(unprojected.x - (position.x + width / 2f), unprojected.y - (position.y + height / 2f));
		
		arm.setTransform(body.getPosition(), (float) (aim.angleRad()));
		
		Vector2 hand = arm.getWorldPoint(Utils.gameToBox(new Vector2(armRegion.getRegionWidth(),0)));
		
		if(selection != null) {
			selection.setPosition(body.getPosition().add(aim.nor().scl((armRegion.getRegionWidth())/ Utils.pixelRatio)),aim.angleRad());
		}
		
		position = Utils.boxToGame(body.getPosition().x, body.getPosition().y).sub(0, height);
		position.sub(standing.getRegionWidth() / 2f, -standing.getRegionWidth());
		camera.position.set(position.x, position.y, 0);
		
		
		
		if (selection.firemode == Gun.Firemode.AUTO && Gdx.input.isTouched())
			selection.shoot(world,dimension,body);
		else if (Gdx.input.justTouched())
			selection.shoot(world,dimension,body);
	}
	
	public void draw(SpriteBatch batch) {//batch must already have begun and must be ended after
		if (movement == Movement.SPRINTING)
			walkAnimation.setFrameDuration(.07f);
		else if (movement == Movement.WALKING)
			walkAnimation.setFrameDuration(.1f);
		
		if(body.getLinearVelocity().x < 0)
			walkAnimation.setPlayMode(Animation.PlayMode.LOOP_REVERSED);
		else
			walkAnimation.setPlayMode(Animation.PlayMode.LOOP);
		
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
		else if (aim.angle() > 90 && aim.angle() < 270 && !current.isFlipX()) {
			current.flip(true, false);
		}
		Vector2 armPos = Utils.boxToGame(arm.getPosition());
		batch.draw(current, position.x, position.y, 4f, 7f, current.getRegionWidth(), current.getRegionHeight(), 1, 1, 0);
		batch.draw(armRegion,armPos.x,armPos.y,0,armRegion.getRegionHeight()/2f,armRegion.getRegionWidth(),armRegion.getRegionHeight(),1,1, (float) (arm.getAngle() * (180/Math.PI)));
		//selection.draw(batch, position.x + width / 2f, position.y + height / 2f, 0, selection.armHeight(), aim.angle());
	}
	
	public boolean dead() {
		return health <= 0;
	}
	
	
	@Override
	public void dispose() {
		atlas.dispose();
		
	}
	public Body getBody() {
		return body;
	}
	@Override
	public void hit(Bullet b) {
		health -= b.damage;
		body.applyLinearImpulse(b.velocity,body.getWorldCenter(),true);
	}
}
