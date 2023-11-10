CREATE DATABASE training;
use training;

CREATE TABLE `user` (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(30),
    last_name VARCHAR(30),
    email VARCHAR(50) NOT NULL UNIQUE,
    `role` VARCHAR(10) NOT NULL,
    `password` VARCHAR(255)
);


CREATE TABLE address (
    address_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    street VARCHAR(100),
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(100),
    pincode INT,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);


CREATE TABLE reset_password_token (
    token_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    token VARCHAR(255),
    valid_till TIMESTAMP,
    is_used BOOL,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);


DELIMITER //
CREATE TRIGGER `delete_address_before_user` BEFORE DELETE ON `user` FOR EACH ROW 
BEGIN
    DELETE FROM address WHERE address.user_id = OLD.user_id;
    DELETE FROM reset_password_token WHERE reset_password_token.user_id = OLD.user_id;
END//
DELIMITER ;


INSERT INTO training.`user` (username,first_name,last_name,email,`role`,password) VALUES
('admin','inexture','admin','shahnavazsaiyad@inexture.com','Admin','�iv�A���M�߱g��s�K��o*�H�');

