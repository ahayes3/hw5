package com.csc455.andy;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.utils.Box2DBuild;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Array;

import java.io.File;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;

enum ScreenState {
	SHOWN,HIDDEN;
}
public class MainGame extends Stage implements Screen {
	static Dimension dimension;
	final Homework5 game;
	SpriteBatch batch;
	OrthographicCamera camera;
	MyMap map;
	Player player;
	World world;
	ScreenState screenState;
	Box2DDebugRenderer debugRenderer;
	Array<Gun> guns;
	Array<Enemy> enemies;
	public static char fs = File.separatorChar;
	TextureAtlas pistolAtlas;
	int mapNum;
	ShapeRenderer shapeRenderer;
	public MainGame(final Homework5 game,int mapNum) {
		shapeRenderer = new ShapeRenderer();
		guns = new Array<>();
		enemies = new Array<>();
		this.mapNum = mapNum;
		
		this.game = game;
		camera = new OrthographicCamera();
		batch = game.batch;
		world = new World(new Vector2(0,-30f),true);
		
		camera.setToOrtho(false,384,216);
		player = new Player(new TextureAtlas(Gdx.files.internal("sprites"+ File.separator+"pc"+File.separator+ "PlayerCharacter.atlas")),world);
		if(mapNum == -1)
			map = new MyMap("maps/tstMap/past/pastTest.tmx","maps/tstMap/present/presentTest.tmx",camera,.5f,world,true,this);
		else
			map = new MyMap("maps/"+mapNum+"/past/past"+mapNum+".tmx","maps/"+mapNum+"/present/present"+1+".tmx",camera,.5f,world,true,this);
		screenState = ScreenState.HIDDEN;
		debugRenderer = new Box2DDebugRenderer();
		
		pistolAtlas = new TextureAtlas("sprites"+MainGame.fs+"guns"+MainGame.fs+"pistol"+MainGame.fs+"Pistol.atlas");
		Gun pistol = new Pistol(10,pistolAtlas,new Vector2(0,0),player.dimension,world);
		guns.add(pistol);
		player.pickup(pistol);
		player.selection = player.inventory.get(0);
		Bullet.textureRegion = new TextureRegion(new Texture("sprites/guns/bullet.png"));
		dimension = Dimension.PRESENT;
		world.setContactListener(new MyContactListener());
		
		//enemies.add(new TrackedRobot(new TextureAtlas("sprites/enemies/trackedRobot/TrackedRobot.atlas"),50,Dimension.PRESENT,new Vector2(64,50),world));
		player.setPosition(map.getSpawn().cpy());
		
		shapeRenderer.setAutoShapeType(true);
	}
	@Override
	public void show() {
		screenState = ScreenState.SHOWN;
	}
	
	@Override
	public void render(float delta) {
		
		if(player.dead()) {
			game.setScreen(game.screens.get(1));
		}
		float dt;
		if(screenState == ScreenState.HIDDEN)
			dt = 0;
		else
			dt = Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
			if(dimension == Dimension.PAST)
				dimension = Dimension.PRESENT;
			else if(dimension == Dimension.PRESENT)
				dimension = Dimension.PAST;
		}
		enemies.forEach(p -> p.think(dt,world));
		player.update(camera,dimension,map,world,dt);
		guns.forEach(p -> p.update(dt));
		world.step(dt,6,2);
		
		if(map.end.contains(Utils.boxToGame(player.body.getPosition().cpy())))
			game.setScreen(game.win);
		
		draw(delta);
	}
	public void draw(float delta) {
		Gdx.gl.glClearColor(1,1,1,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);
		
		map.draw(camera);
		batch.begin();
		enemies.forEach(p -> p.draw(batch,dimension));
		guns.forEach(p -> p.draw(batch));
		player.draw(batch);
		batch.end();
		
		//debugRenderer.render(world,camera.combined.scl(8));
		
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		enemies.forEach(p -> p.drawShapes(shapeRenderer,dimension));
		shapeRenderer.set(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(Color.GREEN);
		shapeRenderer.rect(map.end.x,map.end.y,map.end.width,map.end.height);
		shapeRenderer.end();
		
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
		game.lastScreen = this;
	}
	
	@Override
	public void dispose() {
		player.dispose();
		map.dispose();
		guns.forEach(Gun::dispose);
		pistolAtlas.dispose();
		Bullet.textureRegion.getTexture().dispose();
	}
}
