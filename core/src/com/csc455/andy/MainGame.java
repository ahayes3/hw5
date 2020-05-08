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

import java.io.File;

enum ScreenState {
	SHOWN,HIDDEN;
}
public class MainGame implements Screen {
	final Homework5 game;
	SpriteBatch batch;
	OrthographicCamera camera;
	MyMap map;
	Player player;
	World world;
	ScreenState screenState;
	Box2DDebugRenderer debugRenderer;
	Array<Gun> guns;
	public static char fs = File.separatorChar;

	public MainGame(final Homework5 game) {
		guns = new Array<>();
		this.game = game;
		camera = new OrthographicCamera();
		batch = game.batch;
		world = new World(new Vector2(0,-30f),true);
		
		camera.setToOrtho(false,384,216);
		
		player = new Player(new TextureAtlas(Gdx.files.internal("sprites"+ File.separator+"pc"+File.separator+ "PlayerCharacter.atlas")),world);
		map = new MyMap("maps"+File.separator + "tstMap" +File.separator+ "tstMap.tmx",camera,.5f,world);
		screenState = ScreenState.HIDDEN;
		debugRenderer = new Box2DDebugRenderer();
		Gun pistol = new Pistol(10,"sprites"+MainGame.fs+"guns"+MainGame.fs+"pistol"+MainGame.fs+"Pistol.atlas");
		guns.add(pistol);
		player.pickup(pistol);
	}
	@Override
	public void show() {
		screenState = ScreenState.SHOWN;
	}
	
	@Override
	public void render(float delta) {
		float dt;
		if(screenState == ScreenState.HIDDEN)
			dt = 0;
		else
			dt = Gdx.graphics.getDeltaTime();
		player.update(camera,dt);
		guns.forEach(p -> p.update(dt));
		world.step(dt,6,2);
		
		
		draw(delta);
	}
	public void draw(float delta) {
		Gdx.gl.glClearColor(0,0,1,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		
		
		map.draw(camera);
		batch.begin();
		player.draw(batch);
		batch.end();
		debugRenderer.render(world,camera.combined);
		camera.update();
	}
	@Override
	public void resize(int width, int height) {
	
	}
	
	@Override
	public void pause() {
		screenState = ScreenState.HIDDEN;
	}
	
	@Override
	public void resume() {
		screenState = ScreenState.SHOWN;
	}
	
	@Override
	public void hide() {
		screenState = ScreenState.HIDDEN;
	}
	
	@Override
	public void dispose() {
		player.dispose();
		map.dispose();
	}
}
