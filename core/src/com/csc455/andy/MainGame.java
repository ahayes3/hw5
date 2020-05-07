package com.csc455.andy;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class MainGame implements Screen {
	final Homework5 game;
	SpriteBatch batch;
	OrthographicCamera camera;
	MyMap map;
	Player player;
	ShapeRenderer sr;
	
	public MainGame(final Homework5 game) {
		this.game = game;
		camera = new OrthographicCamera();
		batch = game.batch;
		camera.setToOrtho(false,384,216);
		player = new Player(new TextureAtlas(Gdx.files.internal("PlayerCharacter.atlas")));
		map = new MyMap("tstMap.tmx",camera,.5f);
		sr = new ShapeRenderer();
	}
	@Override
	public void show() {
	
	}
	
	@Override
	public void render(float delta) {
		player.move(camera,map);
		
		draw(delta);
	}
	public void draw(float delta) {
		Gdx.gl.glClearColor(0,0,1,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		sr.setProjectionMatrix(camera.combined);
		
		map.draw(camera);
		batch.begin();
		player.draw(batch);
		batch.end();
		sr.setAutoShapeType(true);
		sr.begin();
		player.debugDraw(sr);
		map.debugDraw(sr);
		sr.end();
		camera.update();
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
	
	}
	
	@Override
	public void dispose() {
		player.dispose();
		map.dispose();
	}
}
