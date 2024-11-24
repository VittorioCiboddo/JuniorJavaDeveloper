-- phpMyAdmin SQL Dump
-- version 4.5.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Creato il: Nov 24, 2024 alle 14:44
-- Versione del server: 10.1.8-MariaDB
-- Versione PHP: 5.6.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `quadrangolare_calcio`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `giocatore`
--

CREATE TABLE `giocatore` (
  `id_giocatore` int(11) NOT NULL,
  `nome` varchar(50) NOT NULL,
  `cognome` varchar(50) NOT NULL,
  `immagine` longtext NOT NULL,
  `numero_maglia` int(11) NOT NULL,
  `heat_map` longtext NOT NULL,
  `data_nascita` date NOT NULL,
  `descrizione` varchar(250) NOT NULL,
  `fk_id_ruolo` int(11) DEFAULT NULL,
  `fk_id_squadra` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `modulo`
--

CREATE TABLE `modulo` (
  `id_modulo` int(11) NOT NULL,
  `schema_gioco` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `modulo`
--

INSERT INTO `modulo` (`id_modulo`, `schema_gioco`) VALUES
(1, '4-4-2'),
(2, '4-3-3'),
(3, '4-2-3-1'),
(4, '3-5-2'),
(5, '3-4-3'),
(6, '4-3-2-1'),
(7, '4-3-1-2'),
(8, '3-4-2-1'),
(9, '4-1-4-1'),
(10, '5-4-1');

-- --------------------------------------------------------

--
-- Struttura della tabella `ruolo`
--

CREATE TABLE `ruolo` (
  `id_ruolo` int(11) NOT NULL,
  `sigla` varchar(5) NOT NULL,
  `descrizione` varchar(50) DEFAULT NULL,
  `fk_id_tipologia` int(11) DEFAULT NULL,
  `fk_id_modulo` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `ruolo`
--

INSERT INTO `ruolo` (`id_ruolo`, `sigla`, `descrizione`, `fk_id_tipologia`, `fk_id_modulo`) VALUES
(1, 'PT', 'Portiere', 1, 3),
(2, 'DC', 'Difensore Centrale', 2, 4),
(3, 'DC', 'Difensore Centrale', 2, 5),
(4, 'DC', 'Difensore Centrale', 2, 8),
(5, 'DC', 'Difensore Centrale', 2, 10),
(7, 'DD', 'Difensore Destro', 2, 2),
(8, 'DD', 'Difensore Destro', 2, 3),
(9, 'DD', 'Difensore Destro', 2, 6),
(10, 'DD', 'Difensore Destro', 2, 7),
(11, 'DD', 'Difensore Destro', 2, 9),
(12, 'DD', 'Difensore Destro', 2, 10),
(14, 'DS', 'Difensore Sinistro', 2, 2),
(15, 'DS', 'Difensore Sinistro', 2, 3),
(16, 'DS', 'Difensore Sinistro', 2, 6),
(17, 'DS', 'Difensore Sinistro', 2, 7),
(18, 'DS', 'Difensore Sinistro', 2, 9),
(19, 'DS', 'Difensore Sinistro', 2, 10),
(21, 'TD', 'Terzino Destro', 2, 2),
(22, 'TD', 'Terzino Destro', 2, 3),
(23, 'TD', 'Terzino Destro', 2, 6),
(24, 'TD', 'Terzino Destro', 2, 7),
(25, 'TD', 'Terzino Destro', 2, 9),
(26, 'TD', 'Terzino Destro', 2, 10),
(28, 'TS', 'Terzino Sinistro', 2, 2),
(29, 'TS', 'Terzino Sinistro', 2, 3),
(30, 'TS', 'Terzino Sinistro', 2, 6),
(31, 'TS', 'Terzino Sinistro', 2, 7),
(32, 'TS', 'Terzino Sinistro', 2, 9),
(33, 'TS', 'Terzino Sinistro', 2, 10),
(34, 'DCD', 'Difensore Centrale Destro', 2, 4),
(35, 'DCD', 'Difensore Centrale Destro', 2, 5),
(36, 'DCD', 'Difensore Centrale Destro', 2, 8),
(37, 'DCS', 'Difensore Centrale Sinistro', 2, 4),
(38, 'DCS', 'Difensore Centrale Sinistro', 2, 5),
(39, 'DCS', 'Difensore Centrale Sinistro', 2, 8),
(41, 'PT', 'Portiere', 1, 2),
(42, 'PT', 'Portiere', 1, 4),
(43, 'PT', 'Portiere', 1, 5),
(44, 'PT', 'Portiere', 1, 6),
(45, 'PT', 'Portiere', 1, 7),
(46, 'PT', 'Portiere', 1, 8),
(47, 'PT', 'Portiere', 1, 9),
(48, 'PT', 'Portiere', 1, 10),
(49, 'MED', 'Mediano', 3, 3),
(50, 'MED', 'Mediano', 3, 9),
(51, 'CC', 'Centrocampista Centrale', 3, 2),
(52, 'CC', 'Centrocampista Centrale', 3, 4),
(53, 'CC', 'Centrocampista Centrale', 3, 6),
(54, 'CC', 'Centrocampista Centrale', 3, 7),
(55, 'CCD', 'Centrocampista Centrale Destro', 3, 2),
(56, 'CCD', 'Centrocampista Centrale Destro', 3, 4),
(57, 'CCD', 'Centrocampista Centrale Destro', 3, 6),
(58, 'CCD', 'Centrocampista Centrale Destro', 3, 7),
(59, 'CCS', 'Centrocampista Centrale Sinistro', 3, 2),
(60, 'CCS', 'Centrocampista Centrale Sinistro', 3, 4),
(61, 'CCS', 'Centrocampista Centrale Sinistro', 3, 6),
(62, 'CCS', 'Centrocampista Centrale Sinistro', 3, 7),
(64, 'CD', 'Centrocampista Destro', 3, 5),
(65, 'CD', 'Centrocampista Destro', 3, 8),
(66, 'CD', 'Centrocampista Destro', 3, 10),
(68, 'CS', 'Centrocampista Sinistro', 3, 5),
(69, 'CS', 'Centrocampista Sinistro', 3, 8),
(70, 'CS', 'Centrocampista Sinistro', 3, 10),
(71, 'COD', 'Centrocampista Offensivo Destro', 3, 9),
(72, 'COS', 'Centrocampista Offensivo Sinistro', 3, 9),
(73, 'TRQ', 'Trequartista', 3, 3),
(74, 'TRQ', 'Trequartista', 3, 6),
(75, 'TRQ', 'Trequartista', 3, 7),
(76, 'TRQ', 'Trequartista', 3, 8),
(77, 'ED', 'Esterno Destro', 3, 4),
(78, 'ED', 'Esterno Destro', 3, 5),
(79, 'ED', 'Esterno Destro', 3, 8),
(80, 'ES', 'Esterno Sinistro', 3, 4),
(81, 'ES', 'Esterno Sinistro', 3, 5),
(82, 'ES', 'Esterno Sinistro', 3, 8),
(84, 'ECD', 'Esterno di Centrocampo Destro', 3, 10),
(86, 'ECS', 'Esterno di Centrocampo Sinistro', 3, 1),
(87, 'EOD', 'Esterno Offensivo Destro', 3, 3),
(88, 'EOD', 'Esterno Offensivo Destro', 3, 9),
(89, 'EOS', 'Esterno Offensivo Sinistro', 3, 3),
(90, 'EOS', 'Esterno Offensivo Sinistro', 3, 9),
(91, 'AD', 'Ala Destra', 4, 2),
(92, 'AD', 'Ala Destra', 4, 5),
(93, 'AS', 'Ala Sinistra', 4, 2),
(94, 'AS', 'Ala Sinistra', 4, 5),
(96, 'P', 'Punta ', 4, 2),
(97, 'P', 'Punta ', 4, 3),
(98, 'P', 'Punta ', 4, 4),
(99, 'P', 'Punta ', 4, 5),
(100, 'P', 'Punta ', 4, 6),
(101, 'P', 'Punta ', 4, 7),
(102, 'P', 'Punta ', 4, 8),
(103, 'P', 'Punta ', 4, 9),
(104, 'P', 'Punta ', 4, 10),
(106, 'SP', 'Seconda Punta', 4, 4),
(107, 'SP', 'Seconda Punta', 4, 7),
(108, 'PT', 'Portiere', 1, 1),
(109, 'P', 'Punta ', 4, 1),
(110, 'DD', 'Difensore Destro', 2, 1),
(111, 'DS', 'Difensore Sinistro', 2, 1),
(112, 'TD', 'Terzino Destro', 2, 1),
(113, 'TS', 'Terzino Sinistro', 2, 1),
(114, 'CD', 'Centrocampista Destro', 3, 1),
(115, 'CS', 'Centrocampista Sinistro', 3, 1),
(116, 'ECS', 'Esterno di Centrocampo Sinistro', 3, 1),
(117, 'SP', 'Seconda Punta', 4, 1),
(118, 'ECD', 'Esterno di Centrocampo Destro', 3, 1);

-- --------------------------------------------------------

--
-- Struttura della tabella `squadra`
--

CREATE TABLE `squadra` (
  `id_squadra` int(11) NOT NULL,
  `nome` varchar(50) NOT NULL,
  `logo` longtext NOT NULL,
  `allenatore` varchar(50) NOT NULL,
  `capitano` varchar(50) NOT NULL,
  `descrizione` varchar(250) NOT NULL,
  `fk_id_modulo` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `tipologia`
--

CREATE TABLE `tipologia` (
  `id_tipologia` int(11) NOT NULL,
  `categoria` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `tipologia`
--

INSERT INTO `tipologia` (`id_tipologia`, `categoria`) VALUES
(1, 'Portiere'),
(2, 'Difensore'),
(3, 'Centrocampista'),
(4, 'Attaccante');

--
-- Indici per le tabelle scaricate
--

--
-- Indici per le tabelle `giocatore`
--
ALTER TABLE `giocatore`
  ADD PRIMARY KEY (`id_giocatore`),
  ADD KEY `fk_id_ruolo` (`fk_id_ruolo`),
  ADD KEY `fk_id_squadra` (`fk_id_squadra`);

--
-- Indici per le tabelle `modulo`
--
ALTER TABLE `modulo`
  ADD PRIMARY KEY (`id_modulo`);

--
-- Indici per le tabelle `ruolo`
--
ALTER TABLE `ruolo`
  ADD PRIMARY KEY (`id_ruolo`),
  ADD KEY `fk_id_tipologia` (`fk_id_tipologia`),
  ADD KEY `fk_id_modulo` (`fk_id_modulo`);

--
-- Indici per le tabelle `squadra`
--
ALTER TABLE `squadra`
  ADD PRIMARY KEY (`id_squadra`),
  ADD KEY `fk_id_modulo` (`fk_id_modulo`);

--
-- Indici per le tabelle `tipologia`
--
ALTER TABLE `tipologia`
  ADD PRIMARY KEY (`id_tipologia`);

--
-- AUTO_INCREMENT per le tabelle scaricate
--

--
-- AUTO_INCREMENT per la tabella `giocatore`
--
ALTER TABLE `giocatore`
  MODIFY `id_giocatore` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT per la tabella `modulo`
--
ALTER TABLE `modulo`
  MODIFY `id_modulo` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;
--
-- AUTO_INCREMENT per la tabella `ruolo`
--
ALTER TABLE `ruolo`
  MODIFY `id_ruolo` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=119;
--
-- AUTO_INCREMENT per la tabella `squadra`
--
ALTER TABLE `squadra`
  MODIFY `id_squadra` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT per la tabella `tipologia`
--
ALTER TABLE `tipologia`
  MODIFY `id_tipologia` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
--
-- Limiti per le tabelle scaricate
--

--
-- Limiti per la tabella `giocatore`
--
ALTER TABLE `giocatore`
  ADD CONSTRAINT `giocatore_ruolo` FOREIGN KEY (`fk_id_ruolo`) REFERENCES `ruolo` (`id_ruolo`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `giocatore_squadra` FOREIGN KEY (`fk_id_squadra`) REFERENCES `squadra` (`id_squadra`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Limiti per la tabella `ruolo`
--
ALTER TABLE `ruolo`
  ADD CONSTRAINT `ruolo_modulo` FOREIGN KEY (`fk_id_modulo`) REFERENCES `modulo` (`id_modulo`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `ruolo_tipologia` FOREIGN KEY (`fk_id_tipologia`) REFERENCES `tipologia` (`id_tipologia`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
