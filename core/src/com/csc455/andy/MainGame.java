package com.csc455.andy;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import java.io.File;

enum ScreenState {
	SHOWN,HIDDEN;
}
public class MainGame implements Screen {
	final static short PRESENT = 2;
	final static short PAST = 1;
	final Homework5 game;
	SpriteBatch batch;
	OrthographicCamera camera;
	MyMap map;
	Player player;
	World world;
	ScreenState screenState;
	Box2DDebugRenderer debugRenderer;
	Array<Gun> guns;
	short state;
	boolean present;
	public static char fs = File.separatorChar;
	TextureAtlas pistolAtlas;
	public MainGame(final Homework5 game) {
		state = PRESENT;
		guns = new Array<>();
		this.game = game;
		camera = new OrthographicCamera();
		batch = game.batch;
		world = new World(new Vector2(0,-30f),true);
		
		camera.setToOrtho(false,384,216);
		player = new Player(new TextureAtlas(Gdx.files.internal("sprites"+ File.separator+"pc"+File.separator+ "PlayerCharacter.atlas")),world);
		map = new MyMap("maps/tstMap/past/pastTest.tmx","maps/tstMap/present/presentTest.tmx",camera,.5f,world,true);
		screenState = ScreenState.HIDDEN;
		debugRenderer = new Box2DDebugRenderer();
		pistolAtlas = new TextureAtlas("sprites"+MainGame.fs+"guns"+MainGame.fs+"pistol"+MainGame.fs+"Pistol.atlas");
		Gun pistol = new Pistol(10,pistolAtlas);
		guns.add(pistol);
		player.pickup(pistol);
		player.selection = player.inventory.get(0);
		present = true;
		
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
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.Q))
			present = !present;
		
		player.update(camera,present,map,dt);
		guns.forEach(p -> p.update(dt));
		world.step(dt,6,2);
		
		
		draw(delta);
	}
	public void draw(float delta) {
		Gdx.gl.glClearColor(1,1,1,1);
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
	public void resize(int width, int height) {}
	
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
		pistolAtlas.dispose();
	}
}
