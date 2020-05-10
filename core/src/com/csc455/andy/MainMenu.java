package com.csc455.andy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class MainMenu implements Screen
{
	final Homework5 game;
	OrthographicCamera camera;
	TextButton testLevel,level1;
	SpriteBatch batch;
	Rectangle testRect,level1Rect;
	public MainMenu(final Homework5 game) {
		this.game = game;
		this.batch = game.batch;
		camera = new OrthographicCamera();
		camera.setToOrtho(false,384,216);
		testLevel = new TextButton("Test Level",game.skin);
		testLevel.getLabel().setScale(.25f);
		testLevel.setPosition(camera.viewportWidth/2 - 150,camera.viewportHeight - 50);
		testLevel.setSize(150,50);
		level1 = new TextButton("Level 1",game.skin);
		level1.getLabel().setScale(.25f);
		level1.setPosition(camera.viewportWidth/2f,camera.viewportHeight -50);
		level1.setSize(150,50);
		
		testRect = new Rectangle();
		testRect.x=camera.viewportWidth/2 - 100;
		testRect.y = camera.viewportHeight - 50;
		testRect.width=100;
		testRect.height=50;
		
		level1Rect = new Rectangle(testRect);
		level1Rect.setPosition(level1.getX(),level1.getY());
		
	}
	@Override
	public void show()
	{
	
	}
	
	@Override
	public void render(float delta)
	{
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.batch.setProjectionMatrix(camera.combined);
		
		if(Gdx.input.justTouched()) {
			Vector3 projected = new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
			Vector3 unprojected = camera.unproject(projected);
			Vector2 mousePos = new Vector2(unprojected.x,unprojected.y);
			if(testRect.contains(mousePos))
				testRect();
			else if(level1Rect.contains(mousePos))
				level1();
			
		}
		batch.begin();
		testLevel.draw(batch,1);
		level1.draw(batch,1);
		batch.end();
		camera.update();
	}
	public void testRect() {
		game.setScreen(new MainGame(game,-1));
	}
	public void level1() {
		game.setScreen(new MainGame(game,1));
	}
	@Override
	public void resize(int width, int height)
	{
	
	}
	
	@Override
	public void pause()
	{
	
	}
	
	@Override
	public void resume()
	{
	
	}
	
	@Override
	public void hide()
	{
	
	}
	
	@Override
	public void dispose()
	{
	
	}
}
