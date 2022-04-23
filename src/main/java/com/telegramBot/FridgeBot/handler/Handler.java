package com.telegramBot.FridgeBot.handler;

import com.telegramBot.FridgeBot.DB.User;
import com.telegramBot.FridgeBot.State;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.util.List;

public interface Handler {
    // метод обрабатывающий действие пользователя
    List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message);

    // метод возвращающий текущий STATE
    State operatedBotState();

    //метод возвращающий команды, которые может обработать Callback
    List<String> operatedCallbackQuery();
}
