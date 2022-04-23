package com.telegramBot.FridgeBot.util;

import com.telegramBot.FridgeBot.DB.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;;

public class TelegramUtil {
    public static SendMessage createMessageTemplate(User user){
        return createMessageTemplate(String.valueOf(user.getChatId()));
    }

    public static SendMessage createMessageTemplate(String chatId) {
        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        sm.enableMarkdown(true);
        return sm;
    }

    public static InlineKeyboardButton createInlineKeyboardButton(String text, String command) {
        InlineKeyboardButton btn = new InlineKeyboardButton();
        btn.setText(text);
        btn.setCallbackData(command);
        return btn;
    }
}
