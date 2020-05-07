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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class MyMap implements Disposable {
	TiledMap map;
	MapRenderer renderer;
	AssetManager manager;
	Rectangle[][] collisionTiles;
	Vector2 tl,tr,bl,br;
	TiledMapTileLayer collisionLayer;
	public MyMap(String fileName, OrthographicCamera camera, float scale) {
		manager = new AssetManager();
		manager.setLoader(TiledMap.class,new TmxMapLoader());
		manager.load("tstMap.tmx",TiledMap.class);
		manager.finishLoading();
		map = manager.get(fileName,TiledMap.class);
		renderer = new OrthogonalTiledMapRenderer(map,scale);
		renderer.setView(camera);
		MapLayer collisionObjects = new MapLayer();
		map.getLayers().add(collisionObjects);
		collisionLayer = (TiledMapTileLayer) map.getLayers().get("collision");
		collisionTiles = new Rectangle[collisionLayer.getWidth()][collisionLayer.getHeight()];
		
		for(int i=0;i<collisionLayer.getWidth();i++) {
			for(int j=0;j<collisionLayer.getHeight();j++) {
				TiledMapTileLayer.Cell cell = collisionLayer.getCell(i,j);
				if(cell==null)
					collisionTiles[i][j]=null;
				else if(cell.getTile().getProperties().containsKey("block") && ((Boolean) cell.getTile().getProperties().get("block"))) {
					collisionTiles[i][j] = new Rectangle(i*collisionLayer.getTileWidth()*scale,j*collisionLayer.getTileHeight()*scale,collisionLayer.getTileWidth()*scale,collisionLayer.getTileHeight()*scale);
				}
			}
		}
		tl = new Vector2(0,0);
		tr = new Vector2(0,0);
		bl = new Vector2(0,0);
		br = new Vector2(0,0);
	}
	public Array<Rectangle> tilesNear(Rectangle rect) {
		Array<Rectangle> out = new Array<>();
		tl.set(rect.x,rect.y+rect.height);
		tr.set(rect.x+rect.width,rect.y+rect.height);
		bl.set(rect.x,rect.y);
		br.set(rect.x+rect.width,rect.y);
		
		tl.scl(1/8f).set((int)tl.x,(int)tl.y);
		tr.scl(1/8f).set((int)tr.x,(int)tr.y);
		bl.scl(1/8f).set((int)bl.x,(int)bl.y);
		br.scl(1/8f).set((int)br.x,(int)br.y);
		try {
			for (int i = (int) bl.x; i <= (int) br.x; i++) {
				for (int j = (int) bl.y; j <= (int) tl.y; j++) {
					if(collisionTiles[i][j]!=null)
						out.add(collisionTiles[i][j]);
				}
			}
		}
		catch(IndexOutOfBoundsException e) {
			return out;
		}
		return out;
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
