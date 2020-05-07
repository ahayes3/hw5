package com.csc455.andy;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;

public class MainGame implements Screen {
	final Homework5 game;
	SpriteBatch batch;
	TextureAtlas atlas;
	OrthographicCamera camera;
	TiledMap map;
	AssetManager manager;
	OrthogonalTiledMapRenderer mapRenderer;
	float stateTime;
	Player player;
	
	public MainGame(final Homework5 game) {
		this.game = game;
		camera = new OrthographicCamera();
		batch = game.batch;
		camera.setToOrtho(false,384,216);
		player = new Player(new TextureAtlas(Gdx.files.internal("PlayerCharacter.atlas")));
		
		manager = new AssetManager();
		manager.setLoader(TiledMap.class,new TmxMapLoader());
		manager.load("tstMap.tmx",TiledMap.class);
		manager.finishLoading();
		map = manager.get("tstMap.tmx",TiledMap.class);
		mapRenderer = new OrthogonalTiledMapRenderer(map,.5f);
		mapRenderer.setView(camera);
	}
	@Override
	public void show() {
	
	}
	
	@Override
	public void render(float delta) {
		player.move(camera);
		
		
		draw(delta);
	}
	public void draw(float delta) {
		Gdx.gl.glClearColor(1,1,1,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		
		mapRenderer.setView(camera);
		mapRenderer.render();
		
		batch.begin();
		player.draw(batch);
		batch.end();
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
		atlas.dispose();
		manager.dispose();
		map.dispose();
	}
}
