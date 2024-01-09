package com.raikuman.botutilities;

import com.raikuman.botutilities.config.Config;
import com.raikuman.botutilities.config.ConfigHandler;
import com.raikuman.botutilities.database.DatabaseStartup;
import com.raikuman.botutilities.defaults.DatabaseEventListener;
import com.raikuman.botutilities.defaults.DefaultConfig;
import com.raikuman.botutilities.defaults.DefaultDatabaseStartup;
import com.raikuman.botutilities.invocation.listener.CommandEventListener;
import com.raikuman.botutilities.invocation.listener.SlashEventListener;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.invocation.type.Slash;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.CheckReturnValue;

import java.util.ArrayList;
import java.util.List;

public class BotSetup {

    private static final Logger logger = LoggerFactory.getLogger(BotSetup.class);
    private final JDABuilder jdaBuilder;
    private List<Config> configs;
    private List<DatabaseStartup> databases;
    private List<Command> commands;
    private List<Slash> slashes;

    private BotSetup(JDABuilder jdaBuilder) {
        this.jdaBuilder = jdaBuilder;
        this.configs = new ArrayList<>();
        this.databases = new ArrayList<>();
        this.commands = new ArrayList<>();
    }

    public static BotSetup setup(JDABuilder jdaBuilder) {
        return new BotSetup(jdaBuilder);
    }

    public BotSetup addCommands(List<Command> commands) {
        this.commands = commands;
        return this;
    }

    public BotSetup addSlashes(List<Slash> slashes) {
        this.slashes = slashes;
        return this;
    }

    @CheckReturnValue
    public BotSetup setConfigs(Config... configs) {
        this.configs = new ArrayList<>(List.of(configs));
        return this;
    }

    @CheckReturnValue
    public BotSetup setDatabases(DatabaseStartup... databases) {
        this.databases = new ArrayList<>(List.of(databases));
        return this;
    }

    public JDA build(String token) {
        // Add listeners
        List<ListenerAdapter> listeners = new ArrayList<>();
        listeners.add(new DatabaseEventListener());

        if (!commands.isEmpty()) {
            listeners.add(new CommandEventListener(commands));
        } else {
            logger.info("No commands found, disabling command event listener");
        }

        if (!slashes.isEmpty()) {
            listeners.add(new SlashEventListener(slashes));
        } else {
            logger.info("No slashes found, disabling slash event listener");
        }

        jdaBuilder.addEventListeners(listeners.toArray());

        // Build JDA
        jdaBuilder.setToken(token);
        JDA jda = jdaBuilder.build();

        logger.info("JDA object created: " + jda);

        // Connect
        try {
            jda.awaitStatus(JDA.Status.CONNECTED);
            logger.info("Bot connected to Discord: " + jda);
        } catch (InterruptedException e) {
            logger.info("Error connecting to Discord: " + jda);
            System.exit(0);
        };

        // Setup configs
        this.configs.add(new DefaultConfig());
        ConfigHandler.writeConfigs(configs);

        // Setup databases
        this.databases.add(new DefaultDatabaseStartup());
        for (DatabaseStartup database : databases) {
            database.startup(jda);
        }

        return jda;
    }
}
