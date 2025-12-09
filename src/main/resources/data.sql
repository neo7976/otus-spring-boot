INSERT INTO authors (name) VALUES
                               ('Лев Толстой'),
                               ('Фёдор Достоевский');

INSERT INTO genres (name) VALUES
                              ('Роман'),
                              ('Психологический роман');

INSERT INTO books (title, author_id, genre_id) VALUES
                                                   ('Война и мир', 1, 1),
                                                   ('Анна Каренина', 1, 1),
                                                   ('Преступление и наказание', 2, 2);