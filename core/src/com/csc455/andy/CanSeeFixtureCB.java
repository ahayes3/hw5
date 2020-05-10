package com.csc455.andy;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.utils.Array;

public class CanSeeFixtureCB implements RayCastCallback {
	Array<Fixture> fixtures;
	Box<Boolean> bool;
	public CanSeeFixtureCB(Array<Fixture> fixtures, Box<Boolean> bool) {
		this.fixtures = fixtures;
		this.bool = bool;
	}
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
		bool.value = fixtures.contains(fixture,false);
		return 0;
	}
}
