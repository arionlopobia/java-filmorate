package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaRatingService {
    private final MpaRatingStorage mpaStorage;

    public List<MpaRating> getAllRatings() {
        return mpaStorage.getAll();
    }

    public MpaRating getRatingById(int id) {
        return mpaStorage.getById(id);
    }
}

