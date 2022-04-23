package com.telegramBot.FridgeBot.DB;

import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface NoteRepository extends CrudRepository<Note, Integer> {
    Note findByName(String name);

    List<Note> findAllByNeed(Integer need);
}
