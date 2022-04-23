package com.telegramBot.FridgeBot.DB;

import com.telegramBot.FridgeBot.State;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter @Setter @NoArgsConstructor
public class User {
    @Id
    private Long chatId;
    private State botSate;
    private String name;

    // Конструктор нужен для создания нового пользователя
    public User(Long chatId, String name) {
        this.chatId = chatId;
        this.name = name;
        botSate = State.START;
    }
}