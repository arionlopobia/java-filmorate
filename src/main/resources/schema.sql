-- src/main/resources/schema.sql

DROP TABLE IF EXISTS film_likes;
DROP TABLE IF EXISTS friendships;
DROP TABLE IF EXISTS film_genres;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS mpa_ratings;

CREATE TABLE users (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       login VARCHAR(255) NOT NULL,
                       name VARCHAR(255),
                       birthday DATE
);

CREATE TABLE mpa_ratings (
                             id INT PRIMARY KEY AUTO_INCREMENT,
                             name VARCHAR(10) NOT NULL UNIQUE
);

CREATE TABLE films (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       name VARCHAR(255) NOT NULL,
                       description VARCHAR(200),
                       release_date DATE NOT NULL,
                       duration BIGINT NOT NULL,
                       mpa_id INT NOT NULL,
                       FOREIGN KEY (mpa_id) REFERENCES mpa_ratings(id) ON DELETE CASCADE
);

CREATE TABLE genres (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE film_genres (
                             film_id INT NOT NULL,
                             genre_id INT NOT NULL,
                             PRIMARY KEY (film_id, genre_id),
                             FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE,
                             FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
);

CREATE TABLE film_likes (
                            film_id INT NOT NULL,
                            user_id BIGINT NOT NULL,
                            PRIMARY KEY (film_id, user_id),
                            FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE,
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE friendships (
                             user_id BIGINT NOT NULL,
                             friend_id BIGINT NOT NULL,
                             status VARCHAR(20),
                             PRIMARY KEY (user_id, friend_id),
                             FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                             FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE
);
