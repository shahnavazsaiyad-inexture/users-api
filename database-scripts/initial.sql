CREATE TABLE `user` (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(30),
    last_name VARCHAR(30),
    email VARCHAR(50) NOT NULL UNIQUE,
    `role` VARCHAR(10) NOT NULL,
    `password` VARCHAR(255) NOT NULL
);


CREATE TABLE address (
    address_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    street VARCHAR(100),
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(100),
    pincode VARCHAR(10),
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
