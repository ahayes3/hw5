package com.csc455.andy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Bullet {
	
	Vector2 position,angle;
	int damage;
	Body owner;
	public Bullet(Vector2 position, Vector2 angle, int damage,Body owner, World world) {
		this.owner = owner;
		this.damage =damage;
		this.angle = angle;
	}
	public void draw(SpriteBatch batch) {
	
	}
}
