package com.raikuman.botutilities.invocation.type;

import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.context.CommandContext;

import java.util.ArrayList;
import java.util.List;

public interface Command {

    void handle(CommandContext ctx);
    String getInvoke();
    default List<String> getAliases() {
        return new ArrayList<>();
    }
    String getUsage();
    String getDescription();
    default Category getCategory() { return null; };
}
