package com.telegramBot.FridgeBot;

import com.telegramBot.FridgeBot.DB.User;
import com.telegramBot.FridgeBot.DB.UserRepository;
import com.telegramBot.FridgeBot.handler.Handler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Component @Slf4j @AllArgsConstructor
public class UpdateReceiver {
    private final List<Handler> handlers;
    private final UserRepository userRepository;

    //обработка полученного Update
    public List<PartialBotApiMethod<? extends Serializable>> handle(Update update){
        try{
            if (isMessageWithText(update)){
                final String name = update.getMessage().getFrom().getFirstName();
                final Message message = update.getMessage();
                final long chatId = message.getFrom().getId();

                final User user = userRepository.getByChatId(chatId)
                        .orElseGet(() -> userRepository.save(new User(chatId, name)));

                if (user.getBotSate().equals(State.NONE)){
                    user.setBotSate(setStateFromCommand(message.getText()));
                    userRepository.save(user);
                }

                return getHandlerByState(user.getBotSate()).handle(user, message.getText());
            }
            else if (update.hasCallbackQuery()){
                final CallbackQuery callbackQuery = update.getCallbackQuery();
                final long chatId = callbackQuery.getFrom().getId();
                final User user = userRepository.getByChatId(chatId).orElseThrow();

                return getHandlerByCallBackQuery(callbackQuery.getData()).handle(user, callbackQuery.getData());
            }

            throw new UnsupportedOperationException();
        } catch (UnsupportedOperationException e){
            return Collections.emptyList();
        }
    }

    private Handler getHandlerByState(State state) {
        return handlers.stream()
                .filter(h -> h.operatedBotState() != null)
                .filter(h -> h.operatedBotState().equals(state))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }

    private Handler getHandlerByCallBackQuery(String query) {
        return handlers.stream()
                .filter(h -> h.operatedCallbackQuery().stream()
                        .anyMatch(query::startsWith))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }

    private State setStateFromCommand(String message) {
        switch (message){
            case "/add":
                return State.ADDING;
            case "/list":
                return State.LIST;
            case "/market":
                return State.SHOPPING;
            default:
                return State.START;
        }
    }

    private boolean isMessageWithText(Update update) {
        return !update.hasCallbackQuery() && update.hasMessage() && update.getMessage().hasText();
    }
}
