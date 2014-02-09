package gipsyking.explosionregen;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

public class BlockStateHandlerConfiguration {

	public static HashMap<Material, BlockRecordStateHandler> recordHandlers = new HashMap<Material, BlockRecordStateHandler>();
	
	private static void register(Material material, BlockRecordStateHandler handler) {
		recordHandlers.put(material, handler);
	}
	
	private static void register(Material[] materials, BlockRecordStateHandler handler) {
		for (Material material: materials) {
			register(material, handler);
		}
	}

	public static void setup() {
		register(new Material[]{Material.SIGN_POST, Material.WALL_SIGN}, new BlockRecordStateHandler() {
			
			@Override
			public void setData(BlockState currentState, BlockState previousState) {
				currentState.setData(previousState.getData());
				for (int i = 0; i < ((Sign)previousState).getLines().length; i++) {
					((Sign)currentState).setLine(i, ((Sign)previousState).getLine(i));
				}
				
			}
		});
	}
}
