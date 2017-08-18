# --- !Ups
 Create TABLE IF NOT EXISTS "user_details" (
firstName VARCHAR(150) NOT NULL,
middleName VARCHAR(50) ,
lastName VARCHAR(50) NOT NULL,
email VARCHAR(100) NOT NULL,
mobileNo BIGINT NOT NULL,
gender VARCHAR(15) NOT NULL,
password VARCHAR(100) NOT NULL,
PRIMARY KEY(email)
);

# --- !Downs

DROP TABLE "user_details";