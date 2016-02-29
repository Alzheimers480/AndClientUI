

CREATE TABLE IF NOT EXISTS `USERS2` (
  `USER_UID` varchar(32) NOT NULL,
  `USER_FNAME` varchar(32) NOT NULL,
  `USER_LNAME` varchar(32) NOT NULL,
  `USER_EMAIL` varchar(128) NOT NULL,
  `USER_PWDHSH` varchar(128) NOT NULL,
  `USER_PWDSALT` varchar(128) NOT NULL,
  `VERIFYED` smallint(1) DEFAULT 1,
  PRIMARY KEY (`USER_UID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE IF NOT EXISTS `ACQUAINTANCE` (
  `ACQUAINTANCE_UID` varchar(32) NOT NULL,
  `ACQUAINTANCE_FNAME` varchar(32) NOT NULL,
  `ACQUAINTANCE_LNAME` varchar(32) NOT NULL,
  `PICTURE_SET` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`ACQUAINTANCE_UID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE IF NOT EXISTS `RELATIONSHIP` (
  `USER_UID` varchar(32) NOT NULL,
  `ACQUAINTANCE_UID` varchar(32) NOT NULL,
  `RELATION` varchar(256) DEFAULT NULL,
  `DESCRIPTION` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`USER_UID`,`ACQUAINTANCE_UID`),
  KEY `ACQUAINTANCE_UID` (`ACQUAINTANCE_UID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `RELATIONSHIP`
  ADD CONSTRAINT `RELATIONSHIP_ibfk_1` FOREIGN KEY (`USER_UID`) REFERENCES `USERS2` (`USER_UID`),
  ADD CONSTRAINT `RELATIONSHIP_ibfk_2` FOREIGN KEY (`ACQUAINTANCE_UID`) REFERENCES `ACQUAINTANCE` (`ACQUAINTANCE_UID`);
