package com.csc455.andy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class MyMap implements Disposable {
	TiledMap map;
	MapRenderer renderer;
	AssetManager manager;
	public MyMap(String fileName, OrthographicCamera camera, float scale) {
		manager = new AssetManager();
		manager.setLoader(TiledMap.class,new TmxMapLoader());
		manager.load("tstMap.tmx",TiledMap.class);
		manager.finishLoading();
		map = manager.get(fileName,TiledMap.class);
		renderer = new OrthogonalTiledMapRenderer(map,scale);
		renderer.setView(camera);
		
	}
	public TiledMapTileLayer.Cell cellAt(float x,float y,TiledMapTileLayer layer) {
		return layer.getCell((int) (x/layer.getTileWidth()),(int) (y/layer.getTileHeight()));
	}
	public void draw(OrthographicCamera camera) {
		renderer.setView(camera);
		renderer.render();
	}
	@Override
	public void dispose() {
		manager.dispose();
		map.dispose();
	}
}
