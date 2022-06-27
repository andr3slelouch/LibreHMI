-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema bd_driver_eip
-- -----------------------------------------------------


-- -----------------------------------------------------
-- Schema bd_driver_eip
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `bd_driver_eip` DEFAULT CHARACTER SET utf8 ;
USE `bd_driver_eip` ;

-- -----------------------------------------------------
-- Table `bd_driver_eip`.`plcs`
-- -----------------------------------------------------


CREATE TABLE IF NOT EXISTS `bd_driver_eip`.`plcs` (
  `idPLCS` INT NOT NULL AUTO_INCREMENT,
  `plcNombre` VARCHAR(45) NOT NULL,
  `direccionIP` VARCHAR(45) NOT NULL,
  `deviceGroup` VARCHAR(45) NOT NULL,
  `tiempoPLC` INT NULL,
  PRIMARY KEY (`idPLCS`),
  UNIQUE INDEX `plcNombre_UNIQUE` (`plcNombre` ASC) VISIBLE,
  UNIQUE INDEX `deviceGroup_UNIQUE` (`deviceGroup` ASC) VISIBLE,
  UNIQUE INDEX `direccionIP_UNIQUE` (`direccionIP` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `bd_driver_eip`.`tags`
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS `bd_driver_eip`.`tags` (
  `idTAGS` INT NOT NULL AUTO_INCREMENT,
  `nombreTag` VARCHAR(45) NOT NULL,
  `tipoTag` VARCHAR(45) NOT NULL,
  `tag` VARCHAR(45) NOT NULL,
  `accion` VARCHAR(45) NOT NULL,
  `historico` VARCHAR(45) NULL,
  PRIMARY KEY (`idTAGS`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `bd_driver_eip`.`flotante`
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS `bd_driver_eip`.`flotante` (
  `idFLOTANTE` INT NOT NULL AUTO_INCREMENT,
  `valor` FLOAT NOT NULL,
  `idTagF` INT NOT NULL,
  `nombreTag` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idFLOTANTE`),
  UNIQUE INDEX `idFLOTANTES_UNIQUE` (`idFLOTANTE` ASC) VISIBLE,
  INDEX `idTAGS_idx` (`idTagF` ASC) VISIBLE,
  CONSTRAINT `idTAGSF`
    FOREIGN KEY (`idTagF`)
    REFERENCES `bd_driver_eip`.`tags` (`idTAGS`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `bd_driver_eip`.`entero`
-- -----------------------------------------------------


CREATE TABLE IF NOT EXISTS `bd_driver_eip`.`entero` (
  `idENTERO` INT NOT NULL AUTO_INCREMENT,
  `valor` INT NOT NULL,
  `idTagE` INT NOT NULL,
  `nombreTag` VARCHAR(45) NOT NULL,
  INDEX `idTAGS_idx` (`idTagE` ASC) VISIBLE,
  PRIMARY KEY (`idENTERO`),
  CONSTRAINT `idTAGSE`
    FOREIGN KEY (`idTagE`)
    REFERENCES `bd_driver_eip`.`tags` (`idTAGS`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `bd_driver_eip`.`boolean`
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS `bd_driver_eip`.`boolean` (
  `idBOOLEAN` INT NOT NULL AUTO_INCREMENT,
  `valor` INT NOT NULL,
  `idTagB` INT NOT NULL,
  `nombreTag` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idBOOLEAN`),
  UNIQUE INDEX `nombreTag_UNIQUE` (`idTagB` ASC) VISIBLE,
  CONSTRAINT `idTAGSB`
    FOREIGN KEY (`idTagB`)
    REFERENCES `bd_driver_eip`.`tags` (`idTAGS`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `bd_driver_eip`.`intermedia`
-- -----------------------------------------------------


CREATE TABLE IF NOT EXISTS `bd_driver_eip`.`intermedia` (
  `idINTERMEDIA` INT NOT NULL AUTO_INCREMENT,
  `idPLCS` INT NOT NULL,
  `idTAGS` INT NOT NULL,
  PRIMARY KEY (`idINTERMEDIA`),
  INDEX `idPLCS_idx` (`idPLCS` ASC) VISIBLE,
  INDEX `idTAGS_idx` (`idTAGS` ASC) VISIBLE,
  CONSTRAINT `idPLCS`
    FOREIGN KEY (`idPLCS`)
    REFERENCES `bd_driver_eip`.`plcs` (`idPLCS`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `idTAGS`
    FOREIGN KEY (`idTAGS`)
    REFERENCES `bd_driver_eip`.`tags` (`idTAGS`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
