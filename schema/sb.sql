
Create TABLE IF NOT EXISTS "user_details" (
user_id serial NOT NULL,
firstname VARCHAR(150) NOT NULL,
middlename VARCHAR(50) ,
lastname VARCHAR(50) NOT NULL,
email VARCHAR(100) NOT NULL,
mobilenumber BIGINT NOT NULL,
gender VARCHAR(15) NOT NULL,
password VARCHAR(100) NOT NULL,
isAdmin BOOLEAN,
isEnable BOOLEAN,
PRIMARY KEY(email)
);

CREATE TABLE IF NOT EXISTS "assignments"(
assign_id serial NOT NULL,
title VARCHAR(200) NOT NULL,
description TEXT,
PRIMARY KEY(assign_id)
);