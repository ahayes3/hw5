package com.csc455.andy;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
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
	TiledMap map;
	MapRenderer renderer;
	AssetManager manager;
	Array<Body> tileBodies;
	TiledMapTileLayer collisionLayer;
	float tileWidth,tileHeight;
	public MyMap(String fileName, OrthographicCamera camera, float scale,World world) {
		manager = new AssetManager();
		manager.setLoader(TiledMap.class,new TmxMapLoader());
		manager.load(fileName,TiledMap.class);
		manager.finishLoading();
		map = manager.get(fileName,TiledMap.class);
		renderer = new OrthogonalTiledMapRenderer(map,scale);
		renderer.setView(camera);
		collisionLayer = (TiledMapTileLayer) map.getLayers().get("collision");
		tileBodies = new Array<>();
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		PolygonShape shape = new PolygonShape();
		tileWidth = collisionLayer.getTileWidth()*scale;
		tileHeight = collisionLayer.getTileHeight()*scale;
		
		shape.setAsBox((tileWidth/2)/Coords.ratio,(tileHeight/2)/Coords.ratio);
		FixtureDef fixtureDef = new FixtureDef();
		ChainShape cs = new ChainShape();
		Vector2[] vertices =new Vector2[4];
		for(int i=0;i<shape.getVertexCount();i++) {
			Vector2 v = new Vector2();
			shape.getVertex(i,v);
			vertices[i] = v;
		}
		cs.createChain(vertices);
		fixtureDef.shape = cs;
		fixtureDef.density = 1;
		for(int i=collisionLayer.getWidth();i>=0;i--) {
			for(int j=collisionLayer.getHeight();j>=0;j--) {
				TiledMapTileLayer.Cell c = collisionLayer.getCell(i,j);
				if(c!=null && c.getTile().getProperties().containsKey("block")) {
					bodyDef.position.set(Coords.gameToBox(i *tileWidth + tileWidth/2,j*tileHeight + tileHeight /2));
					Body b = world.createBody(bodyDef);
					b.setUserData(this);
					Fixture f = b.createFixture(fixtureDef);
					tileBodies.add(b);
				}
			}
		}
	}
	public void draw(OrthographicCamera camera) {
		renderer.setView(camera);
		renderer.render();
	}
	public void debugDraw(ShapeRenderer sr) {
		for(int i = 0; i< collisionTiles.length; i++) {
			for(int j = 0; j< collisionTiles[i].length; j++) {
				if(collisionTiles[i][j] != null)
					sr.rect(collisionTiles[i][j].x,collisionTiles[i][j].y,collisionTiles[i][j].width,collisionTiles[i][j].height);
			}
		}
	}
	@Override
	public void dispose() {
		manager.dispose();
		map.dispose();
	}
}
