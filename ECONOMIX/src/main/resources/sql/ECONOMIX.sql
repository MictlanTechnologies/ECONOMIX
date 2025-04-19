-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
SHOW WARNINGS;
-- -----------------------------------------------------
-- Schema economix
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `economix` ;

-- -----------------------------------------------------
-- Schema economix
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `economix` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
SHOW WARNINGS;
USE `economix` ;

-- -----------------------------------------------------
-- Table `economix`.`usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`usuario` (
  `idUsuario` INT NOT NULL,
  `nombreUsuario` VARCHAR(100) NULL DEFAULT NULL,
  `contraseñaUsuario` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`idUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `economix`.`gastos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`gastos` (
  `idGastos` INT NOT NULL,
  `descripcionGasto` VARCHAR(200) NULL DEFAULT NULL,
  `articuloGasto` VARCHAR(100) NULL DEFAULT NULL,
  `montoGasto` DECIMAL(10,2) NULL DEFAULT NULL,
  `fechaGastos` DATE NULL DEFAULT NULL,
  `periodoGastos` VARCHAR(50) NULL DEFAULT NULL,
  `idUsuario` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idGastos`),
  CONSTRAINT `gastos_ibfk_1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `economix`.`usuario` (`idUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;
CREATE INDEX `idUsuario` ON `economix`.`gastos` (`idUsuario` ASC) VISIBLE;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `economix`.`conceptogastos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`conceptogastos` (
  `idConcepto` INT NOT NULL,
  `nombreConcepto` VARCHAR(100) NULL DEFAULT NULL,
  `descripcionConcepto` VARCHAR(200) NULL DEFAULT NULL,
  `precioConcepto` DECIMAL(10,2) NULL DEFAULT NULL,
  `idGastos` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idConcepto`),
  CONSTRAINT `conceptogastos_ibfk_1`
    FOREIGN KEY (`idGastos`)
    REFERENCES `economix`.`gastos` (`idGastos`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;
CREATE INDEX `idGastos` ON `economix`.`conceptogastos` (`idGastos` ASC) VISIBLE;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `economix`.`ingresos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`ingresos` (
  `idIngresos` INT NOT NULL,
  `montoIngreso` DECIMAL(10,2) NULL DEFAULT NULL,
  `periodicidadIngreso` VARCHAR(50) NULL DEFAULT NULL,
  `fechaIngresos` DATE NULL DEFAULT NULL,
  `descripcionIngreso` VARCHAR(200) NULL DEFAULT NULL,
  `idUsuario` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idIngresos`),
  CONSTRAINT `ingresos_ibfk_1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `economix`.`usuario` (`idUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;
CREATE INDEX `idUsuario` ON `economix`.`ingresos` (`idUsuario` ASC) VISIBLE;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `economix`.`conceptoingresos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`conceptoingresos` (
  `idConcepto` INT NOT NULL,
  `nombreConcepto` VARCHAR(100) NULL DEFAULT NULL,
  `descripcionConcepto` VARCHAR(200) NULL DEFAULT NULL,
  `precioConcepto` DECIMAL(10,2) NULL DEFAULT NULL,
  `idIngresos` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idConcepto`),
  CONSTRAINT `conceptoingresos_ibfk_1`
    FOREIGN KEY (`idIngresos`)
    REFERENCES `economix`.`ingresos` (`idIngresos`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;
CREATE INDEX `idIngresos` ON `economix`.`conceptoingresos` (`idIngresos` ASC) VISIBLE;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `economix`.`contactos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`contactos` (
  `idContactos` INT NOT NULL,
  `numCelular` VARCHAR(15) NULL DEFAULT NULL,
  `Correo` VARCHAR(100) NULL DEFAULT NULL,
  `idUsuario` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idContactos`),
  CONSTRAINT `contactos_ibfk_1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `economix`.`usuario` (`idUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;
CREATE INDEX `idUsuario` ON `economix`.`contactos` (`idUsuario` ASC) VISIBLE;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `economix`.`domicilio`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`domicilio` (
  `idDomicilio` INT NOT NULL,
  `Ciudad` VARCHAR(100) NULL DEFAULT NULL,
  `Calle` VARCHAR(100) NULL DEFAULT NULL,
  `Colonia` VARCHAR(100) NULL DEFAULT NULL,
  `Número` VARCHAR(10) NULL DEFAULT NULL,
  `idUsuario` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idDomicilio`),
  CONSTRAINT `domicilio_ibfk_1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `economix`.`usuario` (`idUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;
CREATE INDEX `idUsuario` ON `economix`.`domicilio` (`idUsuario` ASC) VISIBLE;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `economix`.`nombrepersona`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`nombrepersona` (
  `idNombrePersona` INT NOT NULL,
  `Nombre` VARCHAR(100) NULL DEFAULT NULL,
  `apellidoP` VARCHAR(100) NULL DEFAULT NULL,
  `apellidoM` VARCHAR(100) NULL DEFAULT NULL,
  `idUsuario` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idNombrePersona`),
  CONSTRAINT `nombrepersona_ibfk_1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `economix`.`usuario` (`idUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;
CREATE INDEX `idUsuario` ON `economix`.`nombrepersona` (`idUsuario` ASC) VISIBLE;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `economix`.`presupuesto`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `economix`.`presupuesto` (
  `idPresupuesto` INT NOT NULL,
  `fechaPresupuesto` DATE NULL DEFAULT NULL,
  `fechaActualizaciónP` DATE NULL DEFAULT NULL,
  `periodoTPresupuesto` VARCHAR(50) NULL DEFAULT NULL,
  `montoPresupuesto` DECIMAL(10,2) NULL DEFAULT NULL,
  `idIngresos` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idPresupuesto`),
  CONSTRAINT `presupuesto_ibfk_1`
    FOREIGN KEY (`idIngresos`)
    REFERENCES `economix`.`ingresos` (`idIngresos`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

SHOW WARNINGS;
CREATE INDEX `idIngresos` ON `economix`.`presupuesto` (`idIngresos` ASC) VISIBLE;

SHOW WARNINGS;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
