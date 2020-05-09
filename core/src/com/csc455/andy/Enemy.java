package com.csc455.andy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

abstract class Enemy {
	abstract void move();
	abstract void draw(SpriteBatch batch, Dimension dimension);
}
