package com.raikuman.botutilities;

import com.raikuman.botutilities.config.Config;
import com.raikuman.botutilities.config.ConfigHandler;
import com.raikuman.botutilities.database.DatabaseStartup;
import com.raikuman.botutilities.defaults.database.DatabaseEventListener;
import com.raikuman.botutilities.defaults.DefaultConfig;
import com.raikuman.botutilities.defaults.database.DefaultDatabaseStartup;
import com.raikuman.botutilities.defaults.invocation.Prefix;
import com.raikuman.botutilities.invocation.component.ComponentHandler;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BotSetup {

    private static final Logger logger = LoggerFactory.getLogger(BotSetup.class);
    private final JDABuilder jdaBuilder;
    private ExecutorService executor;
    private List<Config> configs;
    private List<DatabaseStartup> databases;
    private List<Command> commands;
    private List<Slash> slashes;
    private List<ListenerAdapter> listeners;
    private boolean disableDatabase;

    private BotSetup(JDABuilder jdaBuilder) {
        this.jdaBuilder = jdaBuilder;
        this.configs = new ArrayList<>();
        this.databases = new ArrayList<>();
        this.commands = new ArrayList<>();
        this.slashes = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.disableDatabase = false;

        // Default thread pool size
        int threadPoolSize = Runtime.getRuntime().availableProcessors() / 4;
        if (threadPoolSize <= 0) {
            threadPoolSize = 1;
        }
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
    }

    @CheckReturnValue
    public static BotSetup setup(JDABuilder jdaBuilder) {
        return new BotSetup(jdaBuilder);
    }

    @CheckReturnValue
    public BotSetup addCommands(List<Command> commands) {
        this.commands = commands;
        return this;
    }

    @CheckReturnValue
    public BotSetup addSlashes(List<Slash> slashes) {
        this.slashes = slashes;
        slashes.add(new Prefix());
        return this;
    }

    @CheckReturnValue
    public BotSetup addListeners(List<ListenerAdapter> listeners) {
        this.listeners = listeners;
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

    @CheckReturnValue
    public BotSetup setExecutorThreads(int threads) {
        if (threads <= 0) {
            threads = 1;
        }
        this.executor = Executors.newFixedThreadPool(threads);
        return this;
    }

    @CheckReturnValue
    public BotSetup disableDatabase(boolean disable) {
        this.disableDatabase = disable;
        return this;
    }

    public ExecutorService getExecutorService() {
        return executor;
    }

    public JDA build(String token) {
        // Add listeners
        jdaBuilder.addEventListeners(buildListeners());

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

        setupData(jda);

        return jda;
    }

    private Object[] buildListeners() {
        ComponentHandler componentHandler = new ComponentHandler();

        List<ListenerAdapter> listeners = new ArrayList<>(this.listeners);
        if (!disableDatabase) {
            listeners.add(new DatabaseEventListener());
        }

        if (!commands.isEmpty()) {
            listeners.add(new CommandEventListener(commands, executor, componentHandler, disableDatabase));
        } else {
            logger.info("No commands found, disabling command event listener");
        }

        if (!slashes.isEmpty()) {
            listeners.add(new SlashEventListener(slashes, executor, componentHandler));
        } else {
            logger.info("No slashes found, disabling slash event listener");
        }

        if (commands.isEmpty() && slashes.isEmpty()) {
            logger.info("No commands or slashes found, disabling component event listeners");
        } else {
            listeners.addAll(componentHandler.getListeners(executor));
        }

        return listeners.toArray();
    }

    private void setupData(JDA jda) {
        // Setup configs
        this.configs.add(new DefaultConfig());
        ConfigHandler.writeConfigs(configs);

        // Setup databases
        if (!disableDatabase) {
            this.databases.add(0, new DefaultDatabaseStartup());
            for (DatabaseStartup database : databases) {
                database.startup(jda);
            }
        }
    }
}
