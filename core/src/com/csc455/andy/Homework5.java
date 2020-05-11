package com.csc455.andy;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

public class Homework5 extends Game
{
	public SpriteBatch batch;
	public BitmapFont font;
	public Array<Screen> screens;
	public Skin skin;
	Screen lastScreen,win;
	public Screen menu;
	@Override
	public void create () {
		skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
		batch = new SpriteBatch();
		font = new BitmapFont();
		screens = new Array<>();
		menu = new MainMenu(this);
		win = new WinScreen(this);
		screens.add(menu);
		screens.add(new DeathScreen(this));
		
		this.setScreen(menu);
	}

	@Override
	public void render () {
		super.render();
		
	}
	
	@Override
	public void dispose () {
		screens.forEach(Screen::dispose);
		batch.dispose();
		font.dispose();
	}
}
