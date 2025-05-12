package com.github.rfsmassacre.mounts;

import com.github.rfsmassacre.heavenlibrary.paper.HeavenPaperPlugin;
import com.github.rfsmassacre.heavenlibrary.paper.commands.SimplePaperCommand;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class MountCommand extends SimplePaperCommand
{
    public MountCommand(HeavenPaperPlugin plugin)
    {
        super(plugin, "mount");
    }

    @Override
    protected void onRun(CommandSender sender, String... args)
    {
        if (!(sender instanceof Player player))
        {
            onConsole(sender);
            return;
        }

        if (args.length < 1)
        {
            onInvalidArgs(player, "<mob>");
            return;
        }

        String mobName = args[0];
        MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob(mobName).orElse(null);
        if (mob == null || !"Mounts".equals(mob.getFaction()))
        {
            locale.sendLocale(player, "invalid-mount", "{mount}", mobName);
            playSound(player, SoundKey.INCOMPLETE);
            return;
        }

        if (!player.hasPermission("mounts.mount." + mob.getInternalName().toLowerCase()))
        {
            locale.sendLocale(player, "no-mount-perm", "{mount}", mob.getDisplayName().get());
            playSound(player, SoundKey.NO_PERM);
            return;
        }

        Entity mount = player.getVehicle();
        if (mount != null)
        {
            if (MythicBukkit.inst().getMobManager().isMythicMob(mount))
            {
                mount.remove();
            }
            else
            {
                mount.removePassenger(player);
            }
        }

        ActiveMob activeMount = mob.spawn(BukkitAdapter.adapt(player.getLocation()), 1.0);
        activeMount.setOwner(player.getUniqueId());
        activeMount.getEntity().getBukkitEntity().addPassenger(player);
        locale.sendLocale(player, "mounted", "{mount}", activeMount.getDisplayName());
        playSound(player, SoundKey.SUCCESS);
    }

    @Override
    protected List<String> onTabComplete(CommandSender sender, String... args)
    {
        if (args.length == 1)
        {
            return MythicBukkit.inst().getMobManager().getMobTypes().stream().filter((mob) ->
                    "Mounts".equals(mob.getFaction()) && sender.hasPermission("mount.mount" + mob.getInternalName()))
                            .map(MythicMob::getInternalName).toList();
        }

        return Collections.emptyList();
    }
}
