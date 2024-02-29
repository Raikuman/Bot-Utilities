package com.raikuman.botutilities.invocation.component;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;

public record ComponentInteraction(Message message, InteractionHook hook) { }
