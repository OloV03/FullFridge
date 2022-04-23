package com.telegramBot.FridgeBot.handler;

import com.telegramBot.FridgeBot.DB.NoteRepository;
import com.telegramBot.FridgeBot.DB.User;
import com.telegramBot.FridgeBot.DB.UserRepository;
import com.telegramBot.FridgeBot.State;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.telegramBot.FridgeBot.util.TelegramUtil.*;

import java.io.Serializable;
import java.util.List;

@Component @AllArgsConstructor
public class ListHandler implements Handler{
    public static String ITEMS_LIST = "/list";
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        SendMessage itemsList = createMessageTemplate(user);
        String text = "";
        for (var item: noteRepository.findAllByNeed(1))
            text = text +"- "+item.getName()+"\n";

        itemsList.setText("Список необходимых товаров:\n" + text);

        user.setBotSate(State.NONE);
        userRepository.save(user);

        return List.of(itemsList);
    }

    @Override
    public State operatedBotState() {
        return State.LIST;
    }

    @Override
    public List<String> operatedCallbackQuery() {
        return List.of(ITEMS_LIST);
    }
}
