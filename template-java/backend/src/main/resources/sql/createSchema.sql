DROP TABLE horse;
DROP TABLE owner;

CREATE TABLE IF NOT EXISTS owner
(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL,
  email VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS horse
(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(4095),
  date_of_birth DATE NOT NULL,
  sex ENUM('MALE', 'FEMALE') NOT NULL,
  owner_id BIGINT,
  father_id BIGINT,
  mother_id BIGINT
);

ALTER TABLE horse ADD CONSTRAINT IF NOT EXISTS FK_owner FOREIGN KEY (owner_id) REFERENCES owner(id);
ALTER TABLE horse ADD CONSTRAINT IF NOT EXISTS FK_father FOREIGN KEY (father_id) REFERENCES horse(id);
ALTER TABLE horse ADD CONSTRAINT IF NOT EXISTS FK_mother FOREIGN KEY (mother_id) REFERENCES horse(id);

SET REFERENTIAL_INTEGRITY FALSE;
DELETE FROM horse where id < 0;
DELETE FROM owner where id < 0;
SET REFERENTIAL_INTEGRITY TRUE;

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

INSERT INTO horse (id, name, description, date_of_birth, sex, owner_id, father_id, mother_id)
VALUES (-1, 'Wendy', 'cute and friendly', '1967-02-06', 'FEMALE', -1, null, null),
       (-2, 'Bella', 'always hungry', '1969-03-08', 'FEMALE', -2, null, null),
       (-3, 'Thunder', 'sleepy', '1999-05-13', 'MALE', -3, null, -1),
       (-4, 'Alexa', 'hard working', '1999-11-29', 'FEMALE', -4, null, -2),
       (-5, 'Tommy', 'sneaky', '2002-06-09', 'MALE', -5, -3, -1),
       (-6, 'Spirit', 'does not know when to stop', '2001-09-12', 'FEMALE', -6, -3, -2),
       (-7, 'Charlie', 'friends with Thunder', '2001-12-12', 'MALE', -7, null, null),
       (-8, 'Misha', 'likes Charlie', '2015-09-07', 'FEMALE', -8, -5, -8),
       (-9, 'Sugar', 'fastest on track', '2014-08-30', 'MALE', -9, -5, -4),
       (-10, 'Storm', 'just a normal horse', '2020-12-12', 'MALE', -10, -7, -6)
;