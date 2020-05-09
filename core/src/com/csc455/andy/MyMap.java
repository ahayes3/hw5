package com.csc455.andy;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;


public class MyMap implements Disposable {
	TiledMap past,present;
	MapRenderer presentRenderer,pastRenderer,draw;
	AssetManager manager;
	Array<Body> tileBodies;
	TiledMapTileLayer presentLayer,pastLayer;
	float tileWidth,tileHeight;
	public MyMap(String pastPath,String presentPath, OrthographicCamera camera, float scale,World world,boolean startPresent) {
		manager = new AssetManager();
		TmxMapLoader loader = new TmxMapLoader();
		manager.setLoader(TiledMap.class,loader);
		manager.load(presentPath,TiledMap.class);
		manager.finishLoading();
		present = manager.get(presentPath,TiledMap.class);
		
		presentRenderer = new OrthogonalTiledMapRenderer(present,scale);
		presentRenderer.setView(camera);
		
		manager.load(pastPath,TiledMap.class);
		manager.finishLoading();
		past = manager.get(pastPath,TiledMap.class);
		
		pastRenderer = new OrthogonalTiledMapRenderer(past,scale);
		pastRenderer.setView(camera);
		
		if(startPresent)
			draw = presentRenderer;
		else
			draw = pastRenderer;
		
		
		tileBodies = new Array<>();
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		PolygonShape shape = new PolygonShape();
		
		tileWidth =((TiledMapTileLayer) present.getLayers().get(0)).getTileWidth()*scale;
		tileHeight = ((TiledMapTileLayer) present.getLayers().get(0)).getTileHeight()*scale;
		
		
		shape.setAsBox((tileWidth/2)/ Utils.pixelRatio,(tileHeight/2)/ Utils.pixelRatio);
		FixtureDef fixtureDef = new FixtureDef();
		ChainShape cs = new ChainShape();
		Vector2[] vertices =new Vector2[5];
		
		
		for(int i=0;i<shape.getVertexCount();i++) {
			Vector2 v = new Vector2();
			shape.getVertex(i,v);
			vertices[i] = v;
		}
		Vector2 v= new Vector2();
		shape.getVertex(0,v);
		vertices[4] = v;
		cs.createChain(vertices);
		fixtureDef.shape = cs;
		fixtureDef.density = 1;
		
		fixtureDef.filter.maskBits =Utils.PAST_BITS|Utils.PLAYER_BITS|Utils.BULLET_BITS;
		fixtureDef.filter.categoryBits=Utils.PAST_BITS;
		
		pastLayer = (TiledMapTileLayer) past.getLayers().get("collision");
		presentLayer = (TiledMapTileLayer) present.getLayers().get("collision");

		for(int i = pastLayer.getWidth(); i>=0; i--) {
			for(int j = pastLayer.getHeight(); j>=0; j--) {
				TiledMapTileLayer.Cell c = pastLayer.getCell(i,j);
				if(c!=null && c.getTile().getProperties().containsKey("block")) {
					bodyDef.position.set(Utils.gameToBox(i *tileWidth + tileWidth/2,j*tileHeight + tileHeight /2));
					Body b = world.createBody(bodyDef);
					b.setUserData(this);
					Fixture f = b.createFixture(fixtureDef);
					tileBodies.add(b);
				}
			}
		}

		fixtureDef.filter.categoryBits = Utils.PRESENT_BITS;
		fixtureDef.filter.maskBits = Utils.BULLET_BITS|Utils.PLAYER_BITS|Utils.PRESENT_BITS;
		for(int i = presentLayer.getWidth(); i>=0; i--) {
			for(int j = presentLayer.getHeight(); j>=0; j--) {
				TiledMapTileLayer.Cell c = presentLayer.getCell(i,j);
				if(c!=null && c.getTile().getProperties().containsKey("block")) {
					bodyDef.position.set(Utils.gameToBox(i *tileWidth + tileWidth/2,j*tileHeight + tileHeight /2));
					Body b = world.createBody(bodyDef);
					b.setUserData(this);
					Fixture f = b.createFixture(fixtureDef);
					tileBodies.add(b);
				}
			}
		}
		
	}
	public void swapDraw() {
		if(draw.equals(presentRenderer))
			draw = pastRenderer;
		else if(draw.equals(pastRenderer))
			draw = presentRenderer;
	}
	public void draw(OrthographicCamera camera) {
		draw.setView(camera);
		draw.render();
	}
	@Override
	public void dispose() {
		manager.dispose();
		past.dispose();
		present.dispose();
	}
}
