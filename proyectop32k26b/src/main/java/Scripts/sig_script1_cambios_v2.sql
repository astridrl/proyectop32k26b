-- ============================================================
-- SCRIPT 1: SOLO CAMBIOS DE ESTRUCTURA (CORREGIDO)
-- Compatible con la BD existente del script unificado
-- Nombres de tablas exactos según sigScriptModulosUnificados.sql
-- ============================================================

USE sig;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- PROCEDIMIENTOS AUXILIARES
-- ============================================================

DROP PROCEDURE IF EXISTS agregarColumna;
DELIMITER $$
CREATE PROCEDURE agregarColumna(
    IN tabla VARCHAR(64),
    IN columna VARCHAR(64),
    IN definicion TEXT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = 'sig'
          AND TABLE_NAME   = tabla
          AND COLUMN_NAME  = columna
    ) THEN
        SET @sql = CONCAT('ALTER TABLE `', tabla, '` ADD COLUMN `', columna, '` ', definicion);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS agregarFK;
DELIMITER $$
CREATE PROCEDURE agregarFK(
    IN tabla VARCHAR(64),
    IN nombreFK VARCHAR(64),
    IN definicion TEXT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
        WHERE TABLE_SCHEMA    = 'sig'
          AND TABLE_NAME      = tabla
          AND CONSTRAINT_NAME = nombreFK
    ) THEN
        SET @sql = CONCAT('ALTER TABLE `', tabla, '` ADD CONSTRAINT `', nombreFK, '` ', definicion);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$
DELIMITER ;

-- ============================================================
-- BLOQUE 1: ACTUALIZAR productos
-- Agregar columnas nuevas que no existían
-- La tabla ya existe con: Prodid, Prodnombre, Prodstockactual,
--                         Prodpuntoreorden, Prodprecioventa
-- ============================================================

CALL agregarColumna('productos', 'Prodcomision',
  'decimal(5,2) NULL COMMENT ''Porcentaje de comision del producto''');
CALL agregarColumna('productos', 'lineaid',
  'int(11) NULL COMMENT ''Linea a la que pertenece''');
CALL agregarColumna('productos', 'marcaid',
  'int(11) NULL COMMENT ''Marca del producto''');

-- ============================================================
-- BLOQUE 2: NUEVAS TABLAS DE CLASIFICACIÓN
-- lineas y marcas no existen en el script unificado
-- ============================================================

CREATE TABLE IF NOT EXISTS `lineas` (
  `lineaid`     int(11)       NOT NULL AUTO_INCREMENT,
  `linnombre`   varchar(100)  NOT NULL,
  `linestado`   tinyint(1)    DEFAULT 1,
  `lincomision` decimal(10,2) DEFAULT 0.00,
  PRIMARY KEY (`lineaid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `marcas` (
  `marcaid`   int(11)      NOT NULL AUTO_INCREMENT,
  `marnombre` varchar(100) NOT NULL,
  `marestado` tinyint(1)   DEFAULT 1,
  PRIMARY KEY (`marcaid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- FKs de productos hacia lineas y marcas
-- (después de crear las tablas)
CALL agregarFK('productos', 'fk_prod_linea',
  'FOREIGN KEY (lineaid) REFERENCES lineas(lineaid)');
CALL agregarFK('productos', 'fk_prod_marca',
  'FOREIGN KEY (marcaid) REFERENCES marcas(marcaid)');

-- ============================================================
-- BLOQUE 3: TABLA IMPUESTOS (nueva)
-- ============================================================

CREATE TABLE IF NOT EXISTS `impuestos` (
  `Impid`     int(11)      NOT NULL AUTO_INCREMENT,
  `Impnombre` varchar(50)  NOT NULL,
  `Impvalor`  decimal(5,2) NOT NULL COMMENT 'Ej: 12.00 para IVA 12%',
  `Impestado` varchar(10)  DEFAULT 'Activo',
  PRIMARY KEY (`Impid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- BLOQUE 4: ACTUALIZAR facturasventa
-- Ya existe con: Facid, Cliid, Venid, Facfecha, Factotal
-- Agregar campos nuevos
-- ============================================================

CALL agregarColumna('facturasventa', 'Facvenumero',
  'varchar(50) NULL AFTER Facid');
CALL agregarColumna('facturasventa', 'Facvesubtotal',
  'decimal(18,2) NULL');
CALL agregarColumna('facturasventa', 'Facveiva',
  'decimal(18,2) NULL');
CALL agregarColumna('facturasventa', 'Facveestado',
  'varchar(20) DEFAULT ''Vigente''');
CALL agregarColumna('facturasventa', 'Impid',
  'int(11) NULL');

CALL agregarFK('facturasventa', 'fk_facventa_imp',
  'FOREIGN KEY (Impid) REFERENCES impuestos(Impid)');

-- ============================================================
-- BLOQUE 5: ACTUALIZAR facturaventadetalle
-- Ya existe con: Detfacid, Facid, Prodid, Cantidad, Preciounitario
-- ============================================================

CALL agregarColumna('facturaventadetalle', 'Facvesubtotal',
  'decimal(18,2) NULL');
CALL agregarColumna('facturaventadetalle', 'Pronombre',
  'varchar(100) NULL COMMENT ''Nombre del producto al momento de la factura''');

-- ============================================================
-- BLOQUE 6: CREAR facturascompras (nueva)
-- ============================================================

CREATE TABLE IF NOT EXISTS `facturascompras` (
  `Faccomid`       int(11)       NOT NULL AUTO_INCREMENT,
  `Faccomnumero`   varchar(50)   NOT NULL UNIQUE,
  `Faccomfecha`    datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Procodigo`      int(11)       NULL,
  `Acrecodigo`     int(11)       NULL,
  `Impid`          int(11)       NULL,
  `Faccomsubtotal` decimal(18,2) NOT NULL,
  `Faccomiva`      decimal(18,2) NOT NULL,
  `Faccomtotal`    decimal(18,2) NOT NULL,
  `Faccomestado`   varchar(20)   DEFAULT 'Vigente',
  PRIMARY KEY (`Faccomid`),
  FOREIGN KEY (`Procodigo`)  REFERENCES `proveedores`(`Procodigo`),
  FOREIGN KEY (`Acrecodigo`) REFERENCES `acreedores`(`Acrecodigo`),
  FOREIGN KEY (`Impid`)      REFERENCES `impuestos`(`Impid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- BLOQUE 7: CREAR facturadetallecompras (nueva)
-- ============================================================

CREATE TABLE IF NOT EXISTS `facturadetallecompras` (
  `Faccomdetid`    int(11)       NOT NULL AUTO_INCREMENT,
  `Faccomid`       int(11)       NOT NULL,
  `Prodid`         int(11)       NOT NULL,
  `Pronombre`      varchar(100)  NULL,
  `Faccomcantidad` decimal(12,2) NOT NULL,
  `Faccomprecio`   decimal(18,2) NOT NULL,
  `Faccomsubtotal` decimal(18,2) NOT NULL,
  PRIMARY KEY (`Faccomdetid`),
  FOREIGN KEY (`Faccomid`) REFERENCES `facturascompras`(`Faccomid`),
  FOREIGN KEY (`Prodid`)   REFERENCES `productos`(`Prodid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- BLOQUE 8: CREAR movimientoscc (nueva)
-- ============================================================

CREATE TABLE IF NOT EXISTS `movimientoscc` (
  `Mccid`       int(11)       NOT NULL AUTO_INCREMENT,
  `Mccfecha`    datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Mccmonto`    decimal(12,2) NOT NULL,
  `Mcctipo`     ENUM('CARGO','ABONO') NOT NULL,
  `Mccconcepto` varchar(255)  NOT NULL,
  `Mccestado`   char(1)       NOT NULL DEFAULT 'A',
  `Mccsaldo`    decimal(12,2) NOT NULL DEFAULT 0.00,
  `Cliid`       int(11)       NULL,
  `Procodigo`   int(11)       NULL,
  `Acrecodigo`  int(11)       NULL,
  `Venid`       int(11)       NULL,
  `TTid`        int(11)       NOT NULL,
  `Mccmodulo`   ENUM('VENTAS','COMPRAS','PLANILLA','BANCOS','CC','LOGISTICA') NOT NULL,
  `Mccorigenid` int(11)       NULL,
  PRIMARY KEY (`Mccid`),
  FOREIGN KEY (`Cliid`)      REFERENCES `clientes`(`Cliid`),
  FOREIGN KEY (`Procodigo`)  REFERENCES `proveedores`(`Procodigo`),
  FOREIGN KEY (`Acrecodigo`) REFERENCES `acreedores`(`Acrecodigo`),
  FOREIGN KEY (`Venid`)      REFERENCES `vendedores`(`Venid`),
  FOREIGN KEY (`TTid`)       REFERENCES `Cattipotransaccion`(`TTid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- BLOQUE 9: ACTUALIZAR comisionesvendedores
-- Ya existe con: Comid, Venid, Commontoventas, Commeta,
--               Commarca, Comventasadicionales, Comcomision, Cppcodigo
-- Agregar columnas nuevas del módulo Dulce
-- ============================================================

CALL agregarColumna('comisionesvendedores', 'lincomision',
  'decimal(10,2) DEFAULT 0.00');
CALL agregarColumna('comisionesvendedores', 'prodcomision',
  'decimal(10,2) DEFAULT NULL');
CALL agregarColumna('comisionesvendedores', 'marcomision',
  'decimal(10,2) DEFAULT NULL');

-- ============================================================
-- BLOQUE 10: NUEVA TABLA reportescomisionventa
-- Diferente a la tabla reportes que ya existe
-- ============================================================

CREATE TABLE IF NOT EXISTS `reportescomisionventa` (
  `Repid`       int(11)       NOT NULL AUTO_INCREMENT,
  `Repfecha`    date          NOT NULL,
  `Rephora`     time          NOT NULL,
  `Venid`       int(11)       NOT NULL,
  `Vennombre`   varchar(100)  NOT NULL,
  `Comcomision` decimal(10,2) NOT NULL,
  PRIMARY KEY (`Repid`),
  FOREIGN KEY (`Venid`) REFERENCES `vendedores`(`Venid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- BLOQUE 11: NUEVA TABLA BitacoraComisionVenta
-- ============================================================

CREATE TABLE IF NOT EXISTS `BitacoraComisionVenta` (
  `BCVid`            int(11)      NOT NULL AUTO_INCREMENT,
  `BCVusuarioaccion` int(11)      NOT NULL,
  `BCVaccion`        varchar(200) NOT NULL,
  `BCVtabla`         int(11)      NOT NULL,
  `BCVregistroid`    varchar(50)  DEFAULT NULL,
  `BCVfecha`         datetime     DEFAULT CURRENT_TIMESTAMP,
  `BCVdescripcion`   varchar(255) DEFAULT NULL,
  PRIMARY KEY (`BCVid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- BLOQUE 12: ACTUALIZAR vendedores
-- Ya existe con: Venid, Empcodigo, Vennombre, Ventelefono,
--               Vendireccion, Vencorreo
-- Agregar Vencomisiones
-- ============================================================

CALL agregarColumna('vendedores', 'Vencomisiones',
  'decimal(18,2) DEFAULT 0.00');

-- ============================================================
-- BLOQUE 13: NUEVOS TIPOS EN Cattipotransaccion
-- La tabla se llama Cattipotransaccion (según script unificado)
-- ============================================================

INSERT IGNORE INTO `Cattipotransaccion` (`TTnombretipo`, `TTdescripcion`) VALUES
  ('NOTA_CREDITO',   'Reduccion de saldo por devolucion o ajuste'),
  ('FACTURA_VENTA',  'Cargo por factura de venta a cliente'),
  ('FACTURA_COMPRA', 'Cargo por factura de compra a proveedor'),
  ('IMPUESTO',       'Cargo por impuesto aplicado en factura');

-- ============================================================
-- LIMPIAR PROCEDIMIENTOS
-- ============================================================

DROP PROCEDURE IF EXISTS agregarColumna;
DROP PROCEDURE IF EXISTS agregarFK;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- RESUMEN:
-- TABLAS ACTUALIZADAS (ya existían):
--   ✔ productos        → Prodcomision, lineaid, marcaid
--   ✔ facturasventa    → Facvenumero, subtotal, iva, estado, Impid
--   ✔ facturaventadetalle → Facvesubtotal, Pronombre
--   ✔ comisionesvendedores → lincomision, prodcomision, marcomision
--   ✔ vendedores       → Vencomisiones
-- TABLAS NUEVAS:
--   ✔ lineas
--   ✔ marcas
--   ✔ impuestos
--   ✔ facturascompras  (con Procodigo, Acrecodigo, Impid)
--   ✔ facturadetallecompras
--   ✔ movimientoscc
--   ✔ reportescomisionventa
--   ✔ BitacoraComisionVenta
-- CATÁLOGOS:
--   ✔ Cattipotransaccion → 4 tipos nuevos
-- ============================================================
