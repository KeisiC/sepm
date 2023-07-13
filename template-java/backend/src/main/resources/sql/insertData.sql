-- insert initial test data
-- the IDs are hardcoded to enable references between further test data
-- negative IDs are used to not interfere with user-entered data and allow clean deletion of test data

DELETE FROM horse where id < 0;
DELETE FROM owner where id < 0;

INSERT INTO owner (id, first_name, last_name, email)
VALUES (-1, 'James', 'Potter', 'potter.james@gmail.com'),
       (-2, 'Hermione', 'Granger', 'granger.hermione@icloud.com'),
       (-3, 'Albus', 'Dumbledore', 'bumbledore.albus@icloud.com'),
       (-4, 'Severus', 'Snape', 'snape.severus@icloud.com'),
       (-5, 'Jinny', 'Weasly', 'weasly.jinny@icloud.com'),
       (-6, 'Ron', 'Weasly', 'weasly.ron@icloud.com'),
       (-7, 'Tom', 'Riddle', 'riddle.tom@icloud.com'),
       (-8, 'Sirius', 'Black', 'black.sirius@icloud.com'),
       (-9, 'Harry', 'Potter', 'potter.harry@icloud.com'),
       (-10, 'Lilly', 'Potter', 'potter.lilly@icloud.com')
;

INSERT INTO horse (id, name, description, date_of_birth, sex)
VALUES (-1, 'Wendy', 'cute and friendly', '1967-02-06', 'FEMALE'),
       (-2, 'Bella', 'always hungry', '1969-03-08', 'FEMALE'),
       (-3, 'Thunder', 'sleepy', '1999-05-13', 'MALE'),
       (-4, 'Alexa', 'hard working', '1999-11-29', 'FEMALE'),
       (-5, 'Tommy', 'sneaky', '2002-06-09', 'MALE'),
       (-6, 'Spirit', 'does not know when to stop', '2001-09-12', 'FEMALE'),
       (-7, 'Charlie', 'friends with Thunder', '2001-12-12', 'MALE'),
       (-8, 'Misha', 'likes Charlie', '2015-09-07', 'FEMALE'),
       (-9, 'Sugar', 'fastest on track', '2014-08-30', 'MALE'),
       (-10, 'Storm', 'just a normal horse', '2020-12-12', 'MALE')
;