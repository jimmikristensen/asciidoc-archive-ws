DROP TABLE IF EXISTS `apikeys`;
CREATE TABLE IF NOT EXISTS `apikeys` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `apikey` VARCHAR(40) NOT NULL,
  `owner` VARCHAR(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UNIQUE1` (`apikey` ASC));

DROP TABLE IF EXISTS `asciidocs`;
CREATE TABLE IF NOT EXISTS `asciidocs` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(100) NOT NULL,
  `filename` VARCHAR(100) NOT NULL,
  `path` VARCHAR(255) NOT NULL,
  `sha` VARCHAR(40) NOT NULL,
  `created` DATETIME NOT NULL,
  `url` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`, `title`),
  UNIQUE INDEX `UNIQUE2` (`path` ASC));

DROP TABLE IF EXISTS `categories`;
CREATE TABLE IF NOT EXISTS `categories` (
  `asciidocId` INT UNSIGNED NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`asciidocId`, `name`),
  INDEX `INDEX3` (`name` ASC),
  CONSTRAINT `fk_categories_asciidocs1`
    FOREIGN KEY (`asciidocId`)
    REFERENCES `asciidocs` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

DROP TABLE IF EXISTS `contents`;
CREATE TABLE IF NOT EXISTS `contents` (
  `asciidocId` INT UNSIGNED NOT NULL,
  `type` VARCHAR(20) NOT NULL,
  `doc` MEDIUMTEXT NOT NULL,
  PRIMARY KEY (`asciidocId`, `type`),
  CONSTRAINT `fk_translation_asciidocs1`
    FOREIGN KEY (`asciidocId`)
    REFERENCES `asciidocs` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);