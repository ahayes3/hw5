package com.csc455.andy;

import com.badlogic.gdx.physics.box2d.Body;

public interface Hittable {
	void hit(Bullet b);
	void subHealth(int health);
	Body getBody();
}
