DROP TABLE IF EXISTS `apikeys`;
CREATE TABLE IF NOT EXISTS `apikeys` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `apikey` VARCHAR(40) NOT NULL,
  `owner` VARCHAR(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UNIQUE1` (`apikey` ASC)
);

DROP TABLE IF EXISTS `asciidoc`;
CREATE TABLE IF NOT EXISTS `asciidoc` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `apikeys_id` INT UNSIGNED NOT NULL,
  `doc` MEDIUMBLOB NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  INDEX `fk_asciidoc_apikeys_idx` (`apikeys_id` ASC),
  CONSTRAINT `fk_asciidoc_apikeys`
    FOREIGN KEY (`apikeys_id`)
    REFERENCES `asciidoc_service`.`apikeys` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);

DROP TABLE IF EXISTS `translation`;
CREATE TABLE IF NOT EXISTS `translation` (
  `type` VARCHAR(20) NOT NULL,
  `asciidoc_id` INT UNSIGNED NOT NULL,
  `doc` MEDIUMBLOB NOT NULL DEFAULT '',
  PRIMARY KEY (`type`, `asciidoc_id`),
  INDEX `fk_translation_asciidoc1_idx` (`asciidoc_id` ASC),
  CONSTRAINT `fk_translation_asciidoc1`
    FOREIGN KEY (`asciidoc_id`)
    REFERENCES `asciidoc_service`.`asciidoc` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);