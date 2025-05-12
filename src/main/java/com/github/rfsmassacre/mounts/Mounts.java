package com.github.rfsmassacre.mounts;

import com.github.rfsmassacre.heavenlibrary.paper.HeavenPaperPlugin;
import com.github.rfsmassacre.heavenlibrary.paper.configs.PaperConfiguration;
import com.github.rfsmassacre.heavenlibrary.paper.configs.PaperLocale;

public final class Mounts extends HeavenPaperPlugin
{
    @Override
    public void onEnable()
    {
        getDataFolder().mkdir();
        addYamlManager(new PaperConfiguration(this, "", "config.yml"));
        addYamlManager(new PaperLocale(this, "", "locale.yml"));
        getServer().getPluginManager().registerEvents(new MountListener(), this);
        getCommand("mount").setExecutor(new MountCommand(this));
    }
}
