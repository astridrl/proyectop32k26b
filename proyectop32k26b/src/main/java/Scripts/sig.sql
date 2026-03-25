-- phpMyAdmin SQL Dump
-- ver-- phpMyAdmin SQL Dump
sion 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1:3306
-- Tiempo de generación: 17-03-2026 a las 05:16:14
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

CREATE DATABASE IF NOT EXISTS sig
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci;

USE sig;

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";
SET FOREIGN_KEY_CHECKS = 0;

START TRANSACTION;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `sig`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `aplicaciones`
--

CREATE TABLE IF NOT EXISTS `aplicaciones` (
  `Aplcodigo` int(11) NOT NULL,
  `Aplnombre` varchar(100) NOT NULL,
  `Aplestado` varchar(100) NOT NULL,
  PRIMARY KEY (`Aplcodigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------


--
-- Estructura de tabla para la tabla `perfiles`
--

CREATE TABLE IF NOT EXISTS `perfiles` (
  `Percodigo` int(11) NOT NULL AUTO_INCREMENT,
  `Pernombre` varchar(100) NOT NULL,
  `Perestado` char(1) NOT NULL,
  PRIMARY KEY (`Percodigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE IF NOT EXISTS `usuario` (
	usuid INT NOT NULL AUTO_INCREMENT,
	usunombre VARCHAR(45) NOT NULL,
	usucontrasena VARCHAR(25) NOT NULL,
	usuultimasesion DATE,
	usuestatus VARCHAR(1) NOT NULL,
	usunombrereal VARCHAR(60),
	usucorreoe VARCHAR(60),
	usutelefono VARCHAR(25),
	usudireccion VARCHAR(80),	
	PRIMARY KEY (usuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
--
-- Estructura de tabla para la tabla `bitacora`
--

CREATE TABLE IF NOT EXISTS `bitacora` (
  `Bitcodigo` int(11) NOT NULL AUTO_INCREMENT,
  `UsuId` int(11) DEFAULT NULL,
  `Aplcodigo` int(11) DEFAULT NULL,
  `Bitfecha` datetime DEFAULT NULL,
  `Bitip` varchar(50) DEFAULT NULL,
  `Bitequipo` varchar(100) DEFAULT NULL,
  `Bitaccion` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`Bitcodigo`),
  FOREIGN KEY (usuid) references usuario (usuid), 
  FOREIGN KEY (aplcodigo) references aplicaciones (aplcodigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
--
-- Estructura de tabla para la tabla `asignacionaplicacionperfil`
--

CREATE TABLE IF NOT EXISTS `asignacionaplicacionperfil` (
  `Aplcodigo` int(11) NOT NULL,
  `Percodigo` int(11) NOT NULL,
  `APLPins` varchar(1) NOT NULL,
  `APLPsel` varchar(1) NOT NULL,
  `APLPupd` varchar(1) NOT NULL,
  `APLPdel` varchar(1) NOT NULL,
  `APLPrep` varchar(1) NOT NULL,
  PRIMARY KEY (`Aplcodigo`,`Percodigo`),
  FOREIGN KEY (Aplcodigo) references aplicaciones(Aplcodigo), 
  FOREIGN KEY (Percodigo) references perfiles(Percodigo)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `asignacionaplicacionusuarios`
--

CREATE TABLE IF NOT EXISTS `asignacionaplicacionusuarios` (
  `Aplcodigo` int(11) NOT NULL,
  `UsuId` int(11) NOT NULL,
  `APLUins` varchar(1) NOT NULL,
  `APLUsel` varchar(1) NOT NULL,
  `APLUupd` varchar(1) NOT NULL,
  `APLUdel` varchar(1) NOT NULL,
  `APLUrep` varchar(1) NOT NULL,
  PRIMARY KEY (`Aplcodigo`,`UsuId`),
  FOREIGN KEY (Aplcodigo) references aplicaciones(Aplcodigo), 
  FOREIGN KEY (UsuID) references usuario(UsuID)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `asignacionperfilusuario`
--

CREATE TABLE IF NOT EXISTS `asignacionperfilusuario` (
  `UsuId` int(11) NOT NULL,
  `Percodigo` int(11) NOT NULL,
  PRIMARY KEY (`UsuId`,`Percodigo`),
  FOREIGN KEY (UsuId) references usuario(UsuId),   
  FOREIGN KEY (Percodigo) references perfiles(Percodigo)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
