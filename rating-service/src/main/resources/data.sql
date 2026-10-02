-- Стартовые оценки для книг из book-service (id книг 1..3)
insert into ratings (book_id, username, score, updated_at) values (1, 'user', 5, now());
insert into ratings (book_id, username, score, updated_at) values (1, 'admin', 4, now());
insert into ratings (book_id, username, score, updated_at) values (2, 'user', 3, now());
