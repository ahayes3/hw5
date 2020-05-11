package com.csc455.andy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;

public class WinScreen implements Screen {
	Homework5 game;
	OrthographicCamera camera;
	SpriteBatch batch;
	Label dead;
	Rectangle buttonRect;
	TextButton button;
	public WinScreen(Homework5 game) {
		this.game = game;
		camera = new OrthographicCamera();
		camera.setToOrtho(false,384,216);
		batch = game.batch;
		camera.position.set(camera.viewportWidth/2f,camera.viewportHeight/2f,0);
		button = new TextButton("Main Menu",game.skin,"default");
		button.setSize(50,50);
		button.getLabel().setFontScale(.5f);
		button.setPosition(50,50);
		buttonRect = new Rectangle();
		buttonRect.x=50;
		buttonRect.y = 50;
		buttonRect.width=50;
		buttonRect.height=50;
		button.setTransform(true);
		dead = new Label("ded",game.skin);
		dead.setPosition(camera.viewportWidth/2f,camera.viewportHeight/2f);
	}
	@Override
	public void show() {
		camera.viewportHeight = 384;
		camera.viewportWidth = 216;
		camera.position.set(camera.viewportWidth/2f,camera.viewportHeight/2f,0);
		
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		
		if(Gdx.input.justTouched()) {
			Vector3 projected = new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
			Vector3 unprojected = camera.unproject(projected);
			Vector2 mousePos = new Vector2(unprojected.x,unprojected.y);
			if(buttonRect.contains(mousePos)) {
				restart();
			}
			
		}
		
		batch.begin();
		game.font.draw(batch,"Level completed",camera.viewportWidth/2f - (camera.viewportWidth/3f/2f),camera.viewportHeight/2f,camera.viewportWidth/3f,Align.center,true);
		button.draw(batch,1);
		batch.end();
		camera.update();
	}
	public void restart() {
		Screen old  = game.lastScreen;
		old.dispose();
		Screen newScreen = game.menu;
		game.setScreen(game.menu);
	}
	@Override
	public void resize(int width, int height) {
	
	}
	
	@Override
	public void pause() {
	
	}
	
	@Override
	public void resume() {
	
	}
	
	@Override
	public void hide() {
		game.lastScreen = this;
	}
	
	@Override
	public void dispose() {
	
	}
}
