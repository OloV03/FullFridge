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

@Component
@Slf4j
public class AddHandler implements Handler{
    public static final String ADD_ITEM = "/add";
    public static final String ADD_NEW_ITEM = "/add_new";
    private static List<String> itemList = new ArrayList<>();
    private Note note;
    private final UserRepository userRep;
    private final NoteRepository noteRep;

    public AddHandler(UserRepository userRep, NoteRepository noteRep) {
        this.userRep = userRep;
        this.noteRep = noteRep;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        if (itemList.contains(message)) {
            return addItemFromList(user, message);
        } else if (message.equals(ADD_ITEM)) {
            return getItemsList(user);
        } else if (message.equals(ADD_NEW_ITEM)){
            return addNewItem(user);
        } else {
            return newItemTry(user, message);
        }
    }

    private List<PartialBotApiMethod<? extends Serializable>> addItemFromList(User user, String message){
        String item = message.substring(5);
        log.info(user.getName()+" добавил в список покупок "+item);

        Note note = noteRep.findByName(item);
        note.setNeed(1);
        noteRep.save(note);

        SendMessage sendMessage = createMessageTemplate(user);
        sendMessage.setText("В список покупок добавлен(о) "+item);

        user.setBotSate(State.NONE);
        userRep.save(user);

        return List.of(sendMessage);
    }

    private List<PartialBotApiMethod<? extends Serializable>> getItemsList(User user){
        itemList.clear();
        noteRep.findAll().forEach(x -> itemList.add("/add_"+x.getName()));
        log.info(user.getName()+" выбирает что добавить");
        user.setBotSate(State.ADDING);
        userRep.save(user);

        SendMessage variants = createMessageTemplate(user);
        variants.setText("Выбери из предложенного или напиши новое");

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> line = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++){
            if (i % 2 == 0){
                line = new ArrayList<>();
                line.add(createInlineKeyboardButton(itemList.get(i).substring(5), itemList.get(i)));

                if (i == itemList.size()-1)
                    buttons.add(line);
            } else {
                line.add(createInlineKeyboardButton(itemList.get(i).substring(5), itemList.get(i)));
                buttons.add(line);
            }
        }
        keyboard.setKeyboard(buttons);
        variants.setReplyMarkup(keyboard);

        return List.of(variants);
    }

    private List<PartialBotApiMethod<? extends Serializable>> newItemTry(User user, String message){
        note = new Note(message);
        note.setNeed(1);

        if (noteRep.findByName(message)!=null){
            SendMessage alertMessage = createMessageTemplate(user);
            alertMessage.setText("Такой пункт в списке уже есть!");

            user.setBotSate(State.NONE);
            userRep.save(user);

            return List.of(alertMessage);
        }
        else{
            SendMessage messageForAccept = createMessageTemplate(user);
            messageForAccept.setText("Проверь ввод: "+ message);

            SendMessage messageWithButton = createMessageTemplate(user);
            messageWithButton.setText("Если все верно, жми кнопку \"Добавить\"\n\nЕсли есть ошибка, напиши название по-новой");
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            List<InlineKeyboardButton> button = List.of(createInlineKeyboardButton("Добавить", ADD_NEW_ITEM));
            keyboard.setKeyboard(List.of(button));

            messageWithButton.setReplyMarkup(keyboard);

            return List.of(messageForAccept, messageWithButton);
        }
    }

    private List<PartialBotApiMethod<? extends Serializable>> addNewItem(User user){
        noteRep.save(note);
        return getItemsList(user);
    }

    @Override
    public State operatedBotState() {
        return State.ADDING;
    }

    @Override
    public List<String> operatedCallbackQuery() {
        return List.of(ADD_ITEM, ADD_NEW_ITEM);
    }
}