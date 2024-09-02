CREATE TABLE company
(
    id integer NOT NULL,
    name character varying,
    CONSTRAINT company_pkey PRIMARY KEY (id)
);

CREATE TABLE person
(
    id integer NOT NULL,
    name character varying,
    company_id integer references company(id),
    CONSTRAINT person_pkey PRIMARY KEY (id)
);

INSERT INTO company (id, name) VALUES
                                   (1, 'Company A'),
                                   (2, 'Company B'),
                                   (3, 'Company C'),
                                   (4, 'Company D'),
                                   (5, 'Company E'),
                                   (6, 'Company F'),
                                   (7, 'Company G'),
                                   (8, 'Company H'),
                                   (9, 'Company I'),
                                   (10, 'Company J');

INSERT INTO person (id, name, company_id) VALUES
-- Сотрудники компании 1
(1, 'Person 1', 1),
(2, 'Person 2', 1),
(3, 'Person 3', 1),
(4, 'Person 4', 1),
(5, 'Person 5', 1),
(6, 'Person 6', 1),
(7, 'Person 7', 1),
(8, 'Person 8', 1),
(9, 'Person 9', 1),
(10, 'Person 10', 1),

-- Сотрудники компании 2
(11, 'Person 11', 2),
(12, 'Person 12', 2),
(13, 'Person 13', 2),
(14, 'Person 14', 2),
(15, 'Person 15', 2),
(16, 'Person 16', 2),
(17, 'Person 17', 2),
(18, 'Person 18', 2),
(19, 'Person 19', 2),
(20, 'Person 20', 2),

-- Сотрудники компании 3
(21, 'Person 21', 3),
(22, 'Person 22', 3),
(23, 'Person 23', 3),
(24, 'Person 24', 3),
(25, 'Person 25', 3),
(26, 'Person 26', 3),
(27, 'Person 27', 3),
(28, 'Person 28', 3),
(29, 'Person 29', 3),
(30, 'Person 30', 3),

-- Сотрудники компании 4
(31, 'Person 31', 4),
(32, 'Person 32', 4),
(33, 'Person 33', 4),
(34, 'Person 34', 4),
(35, 'Person 35', 4),
(36, 'Person 36', 4),
(37, 'Person 37', 4),
(38, 'Person 38', 4),
(39, 'Person 39', 4),
(40, 'Person 40', 4),

-- Сотрудники компании 5
(41, 'Person 41', 5),
(42, 'Person 42', 5),
(43, 'Person 43', 5),
(44, 'Person 44', 5),
(45, 'Person 45', 5),
(46, 'Person 46', 5),
(47, 'Person 47', 5),
(48, 'Person 48', 5),
(49, 'Person 49', 5),
(50, 'Person 50', 5),

-- Сотрудники компании 6
(51, 'Person 51', 6),
(52, 'Person 52', 6),
(53, 'Person 53', 6),
(54, 'Person 54', 6),
(55, 'Person 55', 6),
(56, 'Person 56', 6),
(57, 'Person 57', 6),
(58, 'Person 58', 6),
(59, 'Person 59', 6),
(60, 'Person 60', 6),

-- Сотрудники компании 7
(61, 'Person 61', 7),
(62, 'Person 62', 7),
(63, 'Person 63', 7),
(64, 'Person 64', 7),
(65, 'Person 65', 7),
(66, 'Person 66', 7),
(67, 'Person 67', 7),
(68, 'Person 68', 7),
(69, 'Person 69', 7),
(70, 'Person 70', 7),

-- Сотрудники компании 8
(71, 'Person 71', 8),
(72, 'Person 72', 8),
(73, 'Person 73', 8),
(74, 'Person 74', 8),
(75, 'Person 75', 8),
(76, 'Person 76', 8),
(77, 'Person 77', 8),
(78, 'Person 78', 8),
(79, 'Person 79', 8),
(80, 'Person 80', 8),

-- Сотрудники компании 9
(81, 'Person 81', 9),
(82, 'Person 82', 9),
(83, 'Person 83', 9),
(84, 'Person 84', 9),
(85, 'Person 85', 9),
(86, 'Person 86', 9),
(87, 'Person 87', 9),
(88, 'Person 88', 9),
(89, 'Person 89', 9),
(90, 'Person 90', 9),

-- Сотрудники компании 10
(91, 'Person 91', 10),
(92, 'Person 92', 10),
(93, 'Person 93', 10),
(94, 'Person 94', 10),
(95, 'Person 95', 10),
(96, 'Person 96', 10),
(97, 'Person 97', 10),
(98, 'Person 98', 10),
(99, 'Person 99', 10),
(100, 'Person 100', 10);

INSERT INTO person (id, name, company_id) VALUES
(101, 'Person 101', 1),
(102, 'Person 102', 10);



SELECT p.name, c.name
FROM person p
    JOIN company c ON c.id = p.company_id
WHERE c.id != 5;

SELECT c.name, COUNT(p.id) AS employee_count
FROM company c JOIN person p ON c.id = p.company_id
GROUP BY c.name
HAVING COUNT(p.id) = (SELECT COUNT(p2.id)
                      FROM person p2
                      GROUP BY p2.company_id
                      ORDER BY COUNT(p2.id) DESC
                      LIMIT 1);
