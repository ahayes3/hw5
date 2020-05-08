package com.csc455.andy;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

public class Ammo {
	TextureRegion texture;
	int quantity,slot;
	public Ammo(TextureRegion texture,int quantity, int slot) {
		this.texture = texture;
		this.quantity = quantity;
		this.slot = slot;
	}
	
}
