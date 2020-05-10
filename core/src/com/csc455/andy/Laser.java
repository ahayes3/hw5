package com.csc455.andy;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Laser {
	Vector2 a,b;
	float width,timer;
	static Color color = new Color(1,0,0,.5f);
	public Laser(Vector2 a,Vector2 b,float width) {
		this.a =a;
		this.b = b;
		this.width = width;
		timer = 0;
	}
	public void draw(ShapeRenderer sr) {
		sr.setColor(color);
		sr.rectLine(a,b,width);
	}
}
