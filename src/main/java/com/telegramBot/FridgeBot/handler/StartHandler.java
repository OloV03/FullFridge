package com.telegramBot.FridgeBot.handler;

import com.telegramBot.FridgeBot.DB.User;
import com.telegramBot.FridgeBot.DB.UserRepository;
import com.telegramBot.FridgeBot.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import static com.telegramBot.FridgeBot.handler.ListHandler.ITEMS_LIST;
import static com.telegramBot.FridgeBot.handler.AddHandler.ADD_ITEM;
import static com.telegramBot.FridgeBot.handler.ShoppingHandler.SHOPPING_LIST;
import static com.telegramBot.FridgeBot.util.TelegramUtil.*;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Component
public class StartHandler implements Handler{
    private final UserRepository userRepository;

    public StartHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        SendMessage welcomeMessage = createMessageTemplate(user);
        String text = "Привет, "+user.getName()+"\nИлюха сделал меня, " +
                "чтобы наш холодильник не остался пустым.\n\nЕсли в нем что-то закончилось, добавь это ЧТО-ТО в список покупок";

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> buttonAdd = List.of(createInlineKeyboardButton("Добавить товар", ADD_ITEM));
        List<InlineKeyboardButton> buttonList = List.of(createInlineKeyboardButton("Что надо купить?", ITEMS_LIST));
        List<InlineKeyboardButton> buttonMarket = List.of(createInlineKeyboardButton("Идем за покупками!", SHOPPING_LIST));
        keyboard.setKeyboard(List.of(buttonAdd, buttonList, buttonMarket));

        welcomeMessage.setText(text);
        welcomeMessage.setReplyMarkup(keyboard);

        user.setBotSate(State.NONE);
        userRepository.save(user);

        return List.of(welcomeMessage);
    }

    @Override
    public State operatedBotState() {
        return State.START;
    }

    @Override
    public List<String> operatedCallbackQuery() {
        return Collections.emptyList();
    }
}
