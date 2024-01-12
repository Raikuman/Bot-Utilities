package com.raikuman.botutilities.invocation.type;

import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.component.ComponentHandler;
import com.raikuman.botutilities.invocation.context.CommandContext;

import java.util.ArrayList;
import java.util.List;

public abstract class Command {

    public ComponentHandler componentHandler;
    public abstract void handle(CommandContext ctx);
    public abstract String getInvoke();
    public List<String> getAliases() {
        return new ArrayList<>();
    }
    public String getUsage() {
        return "";
    }
    public String getDescription() {
        return "";
    }
    public Category getCategory() {
        return null;
    }
}
