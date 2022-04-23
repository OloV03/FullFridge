package com.telegramBot.FridgeBot.DB;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> getByChatId(Long chatId);
}
