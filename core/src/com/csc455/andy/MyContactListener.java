package com.csc455.andy;

import com.badlogic.gdx.physics.box2d.*;

public class MyContactListener implements ContactListener {
	
	@Override
	public void beginContact(Contact contact) {
		Fixture a = contact.getFixtureA();
		Fixture b = contact.getFixtureB();
		Object dataA = a.getBody().getUserData();
		Object dataB = b.getBody().getUserData();
		
		if(a.isSensor() && dataB instanceof MyMap)
			a.setUserData(true);
		else if (dataA instanceof  MyMap && b.isSensor())
			b.setUserData(true);
		 
		if(dataA instanceof Hittable && dataB instanceof Bullet) {
			Bullet bullet = (Bullet) dataB;
			Hittable hittable =(Hittable) dataA;
			if(!hittable.getBody().equals(bullet.owner))
				hittable.hit(bullet);
			bullet.destroy();
		}
		else if(dataA instanceof Bullet && dataB instanceof Hittable) {
			Bullet bullet = (Bullet) dataA;
			Hittable hittable = (Hittable) dataB;
			if(!hittable.getBody().equals(bullet.owner))
				hittable.hit(bullet);
			bullet.destroy();
		}
		else if(a.getBody().getType() == BodyDef.BodyType.StaticBody || b.getBody().getType() == BodyDef.BodyType.StaticBody) {
			if(dataA instanceof Bullet)
				((Bullet) dataA).destroy();
			else if(dataB instanceof Bullet)
				((Bullet) dataB).destroy();
		}
		else if(dataA instanceof Bullet && dataB instanceof Bullet) {
			((Bullet) dataA).destroy();
			((Bullet) dataB).destroy();
		}
	}
	
	@Override
	public void endContact(Contact contact) {}
	
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {}
	
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {}
}
