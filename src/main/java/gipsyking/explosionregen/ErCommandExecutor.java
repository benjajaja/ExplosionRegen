package gipsyking.explosionregen;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ErCommandExecutor implements CommandExecutor {

	private TaskList taskList;

	public ErCommandExecutor(TaskList taskList) {
		this.taskList = taskList;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String comandName,
			String[] args) {
		if (sender.isOp()) {
			if (args.length == 1 && args[0].equals("info")) {
				sender.sendMessage(taskList.getPendingCount() + " pending regenerations");
				
			} else {
				sender.sendMessage("Regenerated " + taskList.runAllPending() + " explosions");
				return true;
			}
		}
		return false;
	}

}
