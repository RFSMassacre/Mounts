package com.github.rfsmassacre.mounts;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.TriggeredSkill;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MountListener implements Listener
{
    @EventHandler(ignoreCancelled = true)
    public void onMount(EntityMountEvent event)
    {
        if (!(event.getEntity() instanceof Player player))
        {
            return;
        }

        ActiveMob mount = MythicBukkit.inst().getMobManager().getActiveMob(event.getMount().getUniqueId())
                .orElse(null);
        if (mount == null)
        {
            return;
        }

        TriggeredSkill skill = MythicBukkit.inst().getSkillManager().getEventBus()
                .processTrigger(SkillTriggers.MOUNT, mount, BukkitAdapter.adapt(player));
        if (skill.getCancelled())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDismount(EntityDismountEvent event)
    {
        if (!(event.getEntity() instanceof Player player))
        {
            return;
        }

        ActiveMob mount = MythicBukkit.inst().getMobManager().getActiveMob(event.getDismounted().getUniqueId())
                .orElse(null);
        if (mount == null)
        {
            return;
        }

        TriggeredSkill skill = MythicBukkit.inst().getSkillManager().getEventBus()
                .processTrigger(SkillTriggers.UNMOUNT, mount, BukkitAdapter.adapt(player));
        if (skill.getCancelled())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        Entity dismounted = player.getVehicle();
        if (dismounted == null)
        {
            return;
        }

        ActiveMob mount = MythicBukkit.inst().getMobManager().getActiveMob(dismounted.getUniqueId())
                .orElse(null);
        if (mount == null)
        {
            return;
        }

        MythicBukkit.inst().getSkillManager().getEventBus().processTrigger(SkillTriggers.UNMOUNT, mount,
                BukkitAdapter.adapt(player));
    }
}
