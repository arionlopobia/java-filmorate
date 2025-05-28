# java-filmorate

Template repository for Filmorate project.
![ER Diagram](C:\Users\User\IdeaProjects\java-filmorate\diagram\Untitled.png)
Диаграмма описывает основные сущности приложения Filmorate: пользователей, фильмы, жанры, возрастные рейтинги, лайки и
дружбу между пользователями.
> Диаграмма описывает основные сущности приложения **Filmorate**:
>
> - пользователей;
> - фильмы;
> - жанры;
> - возрастные рейтинги;
> - лайки;
> - дружбу между пользователями.

-- Получить всех пользователей
SELECT * FROM users;

-- Добавить нового пользователя
INSERT INTO users (email, login, name, birthday)
VALUES ('user@example.com', 'user123', 'User Name', DATE '1990-01-01');

-- Обновить имя пользователя
UPDATE users
SET name = 'New Name'
WHERE id = 1;

-- Добавить друга
INSERT INTO friendships (user_id, friend_id, status)
VALUES (1, 2, 'UNCONFIRMED');

-- Подтвердить дружбу
UPDATE friendships
SET status = 'CONFIRMED'
WHERE user_id = 1 AND friend_id = 2;

-- Получить друзей пользователя
SELECT u.*
FROM users u
JOIN friendships f ON u.id = f.friend_id
WHERE f.user_id = 1;

-- Получить общих друзей двух пользователей
SELECT u.*
FROM users u
JOIN friendships f1 ON u.id = f1.friend_id AND f1.user_id = 1
JOIN friendships f2 ON u.id = f2.friend_id AND f2.user_id = 2;

-- Получить все фильмы
SELECT * FROM films;

-- Добавить фильм
INSERT INTO films (name, description, release_date, duration, mpa_rating)
VALUES (
'Inception',
'A mind-bending thriller',
DATE '2010-07-16',
INTERVAL '2 hours 28 minutes',
'PG-13'
);

-- Обновить описание фильма
UPDATE films
SET description = 'Updated description'
WHERE id = 1;

-- Добавить жанр фильму
INSERT INTO film_genres (film_id, genre_id)
VALUES (1, 3); -- где 3 = "Триллер"

-- Получить все жанры для фильма
SELECT g.name
FROM genres g
JOIN film_genres fg ON g.id = fg.genre_id
WHERE fg.film_id = 1;

-- Добавить лайк
INSERT INTO film_likes (film_id, user_id)
VALUES (1, 2);

-- Удалить лайк
DELETE FROM film_likes
WHERE film_id = 1 AND user_id = 2;

-- Получить топ-10 популярных фильмов
SELECT f.*, COUNT(fl.user_id) AS likes_count
FROM films f
LEFT JOIN film_likes fl ON f.id = fl.film_id
GROUP BY f.id
ORDER BY likes_count DESC
LIMIT 10;

