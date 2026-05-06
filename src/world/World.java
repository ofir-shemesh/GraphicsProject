package world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.joml.Vector2f;
import org.joml.Vector3f;

import camera.Camera;
import inventory.Inventory;
import render.components.ShaderProgram;
import render.components.Texture;
import render.factory.RawModelFactory;
import render.raw.components.RawModel;
import render.scene.Renderable;
import sky.Light;

public class World {
	private static WorldEntity[][] grid;
	private static final int size = 10;
	private static final float cell_size = 2.0f;
	
	private static List<CoinEntity> coins;
	private static Renderable floor_rend;

	private static void spreadRandomly() {
		Random rand = new Random();

		int center = size / 2;
		int range = 4;

		Set<Long> used = new HashSet<>();

		int placed = 0;

		while (placed < Inventory.MAX_COINS) {
		    int i = center + rand.nextInt(range * 2 + 1) - range;
		    int j = center + rand.nextInt(range * 2 + 1) - range;

		    long key = (((long) i) << 32) | (j & 0xffffffffL);

		    if (used.contains(key)) {
		        continue;
		    }

		    used.add(key);
		    setToCoin(i, j);
		    placed++;
		}
	}
	
	public static float getFullSize() {
		return cell_size * size;
	}
	
	private static void applyCameraShaders(Camera camera) {
		Runnable setCameraUniforms = () -> {
			for (CoinEntity coin : coins) {
				coin.getRenderable().getShaderProgram().editUniform("camTrans", camera.getModelViewProjectionMatrix());				
			}
			
			floor_rend.getShaderProgram().editUniform("camTrans", camera.getModelViewProjectionMatrix());
			floor_rend.getShaderProgram().editUniform("camTranslationTrans", camera.getTranslationTransformation());

		};
		
		setCameraUniforms.run();
		camera.addPositionChangeListener(setCameraUniforms);
	}
	
	private static void applyLightShaders() {
		Runnable editLightShaders = () -> {
			Vector3f direction = Light.getDirection();
			for (CoinEntity coin : coins) {
				coin.getRenderable().getShaderProgram().editUniform("lightDir", direction);				
			}
		};
		
		Light.addPostRotEdit(editLightShaders);
		editLightShaders.run();
	}
	
	private static Vector3f getPosition(int i, int j) {
		int nor_i = i - size/2;
		int nor_j = j - size/2;
		
		return new Vector3f((nor_i+0.5f) * cell_size, 0.0f, (nor_j+0.5f) * cell_size);
	}
	
	public static int toI(float x) {
    return Math.round(x / cell_size + size / 2.0f - 0.5f);
}

	public static int toJ(float z) {
	    return Math.round(z / cell_size + size / 2.0f - 0.5f);
	}
	
	public static Vector3f requestMovement(Vector3f position) {
		float minX = - (size / 2.0f) * cell_size;
	    float maxX =   (size / 2.0f) * cell_size;

	    float minZ = - (size / 2.0f) * cell_size;
	    float maxZ =   (size / 2.0f) * cell_size;

	    float newX = Math.clamp(position.x, minX, maxX);
	    float newZ = Math.clamp(position.z, minZ, maxZ);

	    return new Vector3f(newX, position.y, newZ);
	}
	
	public static void setToCoin(int i, int j) {
		if (!(0 <= i && i < size && 0 <= j && j < size)) {
		    return;
		}
		
		CoinEntity coin = new CoinEntity(getPosition(i, j));
		coins.add(coin);
		
		grid[i][j] = coin;
	}

	public static void setToEmpty(int i, int j) {
		if (!(0 <= i && i < size && 0 <= j && j < size)) {
		    return;
		}
		
		if (grid[i][j] instanceof CoinEntity) {
			CoinEntity coin = (CoinEntity) grid[i][j];
			coins.remove(coin);
			coin.clean();
		}
		
		grid[i][j] = EmptyEntity.INSTANCE;
	}
	
	public static boolean inBounds(int i, int j) {
	    return i >= 0 && i < size && j >= 0 && j < size;
	}
	
	public static void checkForCoin(Vector3f position) {
		int i = toI(position.x);
		int j = toJ(position.z);

		if (!inBounds(i,j)) return;

		if (grid[i][j] instanceof CoinEntity) {
			Inventory.addCoins(1);
			setToEmpty(i, j);
		}
		
	}
	
	private static void initFloorRenderable() {
		float size = getFullSize();
		
		RawModel model = RawModelFactory.quadFloor(new Vector3f(0.0f, -0.5f, 0.0f), new Vector2f(size, size));
		ShaderProgram program = new ShaderProgram("res/shaders/floor/vert.vert", "res/shaders/floor/frag.frag");
		
		program.editUniform("cellSize", cell_size);
		
		floor_rend = new Renderable(model, program, new Texture[] {});
		
	}	
	
	// --- Game Loop ---	
	
	public static void init(Camera camera) {
		grid = new WorldEntity[size][size];
		
		coins = new ArrayList<>();
		
		for (int i = 0; i < size; i++) {
		    for (int j = 0; j < size; j++) {
		        grid[i][j] = EmptyEntity.INSTANCE;
		    }
		}
		
		spreadRandomly();
			
		initFloorRenderable();
		
		applyCameraShaders(camera);
		applyLightShaders();
	}
	
	public static void tick() {
		for (CoinEntity coin : coins) {
			coin.tick();
		}
	}
	
	public static void render() {
		for (CoinEntity coin : coins) {
			coin.render();
		}
		
		floor_rend.render();
	}
	
	public static void clean() {
		for (CoinEntity coin : coins) {
			coin.clean();
		}
		
		floor_rend.clean();
		
	}

}
