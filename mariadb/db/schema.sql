
CREATE DATABASE IF NOT EXISTS `studenten` CHARACTER SET = 'utf8mb4';
USE `studenten`;


CREATE TABLE IF NOT EXISTS `Uebungen`
(
    `uebungId`  BIGINT NOT NULL PRIMARY KEY,
    `maxAnzahl`   INT NOT NULL ,
    `minAnzahl`   INT NOT NULL,
    `uebungname`  text NOT NULL,
    `startDatum`  text NOT NULL ,
    `endDatum`    text NOT NULL ,
    `gruppenanmeldung` boolean NOT NULL ,
    `individualanmeldung` boolean NOT NULL
);

CREATE TABLE IF NOT EXISTS `Termine`
(
    `terminId`  BIGINT NOT NULL PRIMARY KEY,
    `tag`        text NOT NULL ,
    `datum`       text NOT NULL ,
    `uhrzeit`     text NOT NULL,
    `maxMember`   INT NOT NULL ,
    `minMember`   INT NOT NULL,
    `tutor`       text NOT NULL ,
    `repoErstellt` boolean NOT NULL ,
    `terminBelegt` boolean NOT NULL ,
    `gruppenname`  TEXT
);

CREATE TABLE IF NOT EXISTS `Termin_Gehoert_Zu_Uebung`
(
    `terminId`  BIGINT NOT NULL ,
    `uebungId`  BIGINT NOT NULL ,
        primary key (`terminId`, `uebungId`),
    key `terminId`(`terminId`),
    CONSTRAINT `terminId` FOREIGN KEY (`terminId`) REFERENCES `Termine` (`terminId`) ,
    key `uebungId`(`uebungId`),
    CONSTRAINT `uebungId` FOREIGN KEY (`uebungId`) REFERENCES `Uebungen` (`uebungId`)

);
CREATE TABLE IF NOT EXISTS `Student_Gehoert_Zu_Termin`
(
    `termin`     BIGINT   NOT NULL,
    `benutzername` varchar(255) NOT NULL,
    primary key (`termin`, `benutzername`),
    key `termin`(`termin`),
    CONSTRAINT `termin` FOREIGN KEY (`termin`) REFERENCES `Termine` (`terminId`)


);
