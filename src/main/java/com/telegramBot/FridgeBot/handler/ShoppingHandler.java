package com.telegramBot.FridgeBot.handler;

import com.telegramBot.FridgeBot.DB.Note;
import com.telegramBot.FridgeBot.DB.NoteRepository;
import com.telegramBot.FridgeBot.DB.User;
import com.telegramBot.FridgeBot.DB.UserRepository;
import com.telegramBot.FridgeBot.State;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import static com.telegramBot.FridgeBot.util.TelegramUtil.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Component @Slf4j
public class ShoppingHandler implements Handler{
    public static final String SHOPPING_LIST = "/market";
    public static final String STOP_SHOPPING = "/stop";
    private final List<String> shoppingList = new ArrayList<>();
    private final UserRepository userRepository;
    private final NoteRepository noteRepository;

    public ShoppingHandler(UserRepository userRepository, NoteRepository noteRepository) {
        this.userRepository = userRepository;
        this.noteRepository = noteRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        log.info("["+user.getName()+ "] message: " + message);
        if (message.equals(SHOPPING_LIST )){
            return getShoppingList(user);
        } else if (message.equals(STOP_SHOPPING)){
            return stopButton(user);
        } else  {
            return removeItem(user, message);
        }
    }

    private List<PartialBotApiMethod<? extends Serializable>> removeItem(User user, String message){
        String item = message.substring(5);
        Note note = noteRepository.findByName(item);
        note.setNeed(0);
        noteRepository.save(note);
        log.info(user.getName()+" купил "+item);
        return getShoppingList(user);
    }

    private List<PartialBotApiMethod<? extends Serializable>> getShoppingList(User user){
        if (noteRepository.findAllByNeed(1).isEmpty()){
            SendMessage message = createMessageTemplate(user);
            message.setText("Все что нужно уже купили!");
            user.setBotSate(State.NONE);
            userRepository.save(user);

            return List.of(message);
        } else  {
            shoppingList.clear();
            noteRepository.findAllByNeed(1).forEach(item -> shoppingList.add("/rem_"+item.getName()));
            SendMessage items = createMessageTemplate(user);
            items.setText("Отметь товары, которые уже взяли:");
            items.setReplyMarkup(getKeyboard());
            return List.of(items);
        }
    }

    private List<PartialBotApiMethod<? extends Serializable>> stopButton(User user){
        user.setBotSate(State.NONE);
        userRepository.save(user);

        SendMessage sm = createMessageTemplate(user);
        sm.setText("Закупка завершена!");
        return List.of(sm);
    }

    private InlineKeyboardMarkup getKeyboard(){
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> line = new ArrayList<>();
        buttons.add(List.of(createInlineKeyboardButton("Выход", "/stop")));
        for (int i = 0; i < shoppingList.size(); i++){
            if (i % 2 == 0){
                line = new ArrayList<>();
                line.add(createInlineKeyboardButton(shoppingList.get(i).substring(5), shoppingList.get(i)));

                if (i == shoppingList.size()-1)
                    buttons.add(line);
            } else {
                line.add(createInlineKeyboardButton(shoppingList.get(i).substring(5), shoppingList.get(i)));
                buttons.add(line);
            }
        }
        keyboard.setKeyboard(buttons);
        return keyboard;
    }

    @Override
    public State operatedBotState() {
        return State.SHOPPING;
    }

    @Override
    public List<String> operatedCallbackQuery() {
        List<String> commands = new ArrayList<>();
        commands.add(SHOPPING_LIST);
        commands.add(STOP_SHOPPING);
        shoppingList.forEach(x -> commands.add(x));
        commands.forEach(x-> log.info("command: "+x));
        return commands;
    }
}
