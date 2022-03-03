
DROP TABLE IF EXISTS language;
CREATE TABLE language(id VARCHAR(255), name VARCHAR(255), author VARCHAR(255), fileExtension VARCHAR(255));

INSERT INTO language(id, name, author, fileExtension)
       VALUES('java', 'Java', 'James Gosling', 'java'),
            ('cpp', 'C++', 'Bjarne Stroustrup', 'cpp'),
            ('csharp', 'C#', 'Andres Hejlsberg', 'cs'),
            ('perl', 'Perl', 'Larry Wall', 'pl'),
            ('haskel', 'Haskell', 'Simon Peyton', 'hs'),
            ('lua', 'Lua', 'Luiz Henrique', 'lua'),
            ('python', 'Python', 'Guido van Rossum', 'py');