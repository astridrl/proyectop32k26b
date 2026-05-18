-- ============================================================
-- SCRIPT 2: SOLO INSERTS DE DATOS (CORREGIDO)
-- Ejecutar DESPUÉS del Script 1
-- INSERT IGNORE evita duplicados si ya existen datos
-- ============================================================

USE sig;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- CATÁLOGOS BANCOS
-- Tabla: Cattipocuenta (nombre exacto del script unificado)
-- ============================================================

INSERT IGNORE INTO `CattipoCuenta` (`TCnombretipo`, `TCdescripcion`) VALUES
  ('Monetaria',   'Cuenta de uso diario'),
  ('Ahorro',      'Cuenta de ahorro personal'),
  ('Empresarial', 'Cuenta para operaciones empresariales');

-- Tabla: Cattipotransaccion (nombre exacto del script unificado)
INSERT IGNORE INTO `Cattipotransaccion` (`TTnombretipo`, `TTdescripcion`) VALUES
  ('Deposito',       'Ingreso de dinero'),
  ('Retiro',         'Salida de dinero'),
  ('Transferencia',  'Movimiento entre cuentas'),
  ('Pago',           'Pago realizado'),
  ('Cobro',          'Cobro recibido'),
  ('PLANILLA',       'Egreso por pago de nomina'),
  ('COMISION',       'Pago de comision a vendedor'),
  ('PROV',           'Pago a proveedor o acreedor'),
  ('NOTA_CREDITO',   'Reduccion de saldo por devolucion o ajuste'),
  ('FACTURA_VENTA',  'Cargo por factura de venta a cliente'),
  ('FACTURA_COMPRA', 'Cargo por factura de compra a proveedor'),
  ('IMPUESTO',       'Cargo por impuesto aplicado en factura');

-- Tabla: Catestadoconciliacion (nombre exacto del script unificado)
INSERT IGNORE INTO `Catestadoconciliacion` (`Catesnombreestado`) VALUES
  ('Conciliado'),
  ('Pendiente'),
  ('Con Diferencia');

-- ============================================================
-- IMPUESTOS (tabla nueva del script 1)
-- ============================================================

INSERT IGNORE INTO `impuestos` (`Impnombre`, `Impvalor`, `Impestado`) VALUES
  ('IVA', 12.00, 'Activo');

-- ============================================================
-- PLANILLA: DEPARTAMENTOS
-- IDs explícitos para respetar los del archivo Db.txt
-- ============================================================

INSERT IGNORE INTO `departamentos` (`Depcodigo`, `Depnombre`, `Depestado`) VALUES
  (1, 'ventas',          1),
  (2, 'Administración',  1),
  (3, 'Contabilidad',    0),
  (4, 'Gerencia',        0),
  (7, 'RRHH',            0),
  (9, 'seguridad',       1);

-- ============================================================
-- PUESTOS
-- Depcodigo=1 existe (ventas) ✔
-- ============================================================

INSERT IGNORE INTO `puestos` (`Puecodigo`, `Puenombre`, `Puesalario_base`, `Depcodigo`) VALUES
  (1, 'guardia',                           5000.00, 1),
  (2, 'recepcionista',                     6000.00, 1),
  (3, 'atencion al cliente',               5000.00, 1),
  (4, 'asesor de eliminacion de desechos', 5000.00, 1),
  (5, 'intendente',                        4000.00, 1);

-- ============================================================
-- EMPLEADOS
-- Puecodigo 1,2,3 existen ✔
-- Empdpi es UNIQUE — INSERT IGNORE evita duplicados
-- ============================================================

INSERT IGNORE INTO `empleados`
  (`Empcodigo`, `Empnombre`, `Empdpi`, `Puecodigo`, `Empfecha_ingreso`, `Empestado`) VALUES
  (1, 'Ruben',    '24354361',   1, '2019-01-02', 1),
  (2, 'luisa',    '12345678',   2, '2000-04-12', 0),
  (4, 'ana',      '12345679',   1, '2014-12-03', 1),
  (5, 'fernando', '5832824733', 3, '2012-02-12', 1),
  (6, 'carlos',   '24732842',   1, '2008-03-12', 0);

-- ============================================================
-- LOGÍSTICA: BODEGAS
-- ============================================================

INSERT IGNORE INTO `bodegas` (`bodegaid`, `Bodnombre`, `Bodubicacion`) VALUES
  (1, 'BodegA',    'CENTRAL'),
  (2, 'Bodeguita', 'Portales'),
  (3, 'San Jose',  'Escuintla'),
  (4, '19-20',     'Zona 2'),
  (5, 'UMG',       'zona1'),
  (6, 'Sur',       'Puerto San Jose');

-- ============================================================
-- LINEAS Y MARCAS (tablas nuevas del script 1)
-- ============================================================

INSERT IGNORE INTO `lineas` (`lineaid`, `linnombre`, `linestado`, `lincomision`) VALUES
  (1, 'Ferretería',  1,  0.00),
  (2, 'Jugueteria',  1,  5.00),
  (3, 'Abarrotes',   1, 20.00),
  (4, 'Cocina',      1,  0.00),
  (5, 'Tecnologia',  1,  0.00),
  (6, 'Alimentos',   1,  0.00);

INSERT IGNORE INTO `marcas` (`marcaid`, `marnombre`, `marestado`) VALUES
  (1, 'Truper',         1),
  (2, 'Petronas',       1),
  (3, 'Tornillo Feliz', 1),
  (4, 'TungTung Sahur', 1),
  (5, 'Panini',         1);

-- ============================================================
-- PRODUCTOS
-- lineaid y marcaid ya existen ✔
-- La tabla ya tiene Prodid PK — INSERT IGNORE evita duplicados
-- ============================================================

INSERT IGNORE INTO `productos`
  (`Prodid`, `Prodnombre`, `Prodstockactual`, `Prodpuntoreorden`,
   `Prodprecioventa`, `lineaid`, `marcaid`, `Prodcomision`) VALUES
  (1, 'Martillo',                    100,  NULL,   NULL, 1, 1,  NULL),
  (2, 'Pinza',                       100,  NULL,   NULL, 1, 1,  NULL),
  (3, 'Clavos',                      500,   100,   3.00, 1, 3,  0.25),
  (4, 'Peluche tamaño real TT Sahur', 67,    67,  85.00, 2, 4,  3.25),
  (5, 'Taladro',                      80,   200, 500.00, 1, 1, 10.00);

-- ============================================================
-- MOVIMIENTOS INVENTARIO
-- Columnas exactas de la tabla en el script unificado:
-- Movimientoid, Prodid, bodegaid, Movtipomovimiento,
-- Movmotivo, Movcantidad, Movfecha
-- Movtiporeferencia y Movobservacion tienen DEFAULT NULL ✔
-- ============================================================

INSERT IGNORE INTO `movimientosinventario`
  (`Movimientoid`, `Prodid`, `bodegaid`,
   `Movtipomovimiento`, `Movmotivo`, `Movcantidad`, `Movfecha`) VALUES
  (2, 1, 1, 'entrada', 'compra', 50, '2026-05-13 19:27:07'),
  (3, 1, 1, 'entrada', 'compra', 10, '2026-05-15 11:48:53'),
  (4, 2, 2, 'salida',  'venta',   5, '2026-05-15 11:50:45'),
  (5, 3, 3, 'salida',  'merma',   3, '2026-05-15 11:51:19'),
  (6, 1, 2, 'salida',  'venta',   2, '2026-05-15 11:53:21');

-- ============================================================
-- PEDIDOS
-- Columnas exactas: Pedid, Cliid, Pedfecha, Pedestado
-- Cliid=1 → debe existir en clientes
-- El script unificado ya inserta cliente Cliid=1 ✔
-- ============================================================

INSERT IGNORE INTO `pedidos` (`Pedid`, `Cliid`, `Pedfecha`, `Pedestado`) VALUES
  (1, 1, '2026-05-13 00:00:00', 'Pendiente'),
  (2, 1, '2026-05-13 00:00:00', 'Pendiente'),
  (3, 1, '2026-05-13 00:00:00', 'Pendiente'),
  (4, 1, '2026-05-13 00:00:00', 'Entregado'),
  (6, 1, '2026-05-13 00:00:00', 'Transito'),
  (7, 1, '2026-05-13 00:00:00', 'Perdido');

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- RESUMEN DE INSERTS:
-- ✔ CattipoCuenta         → 3 registros (INSERT IGNORE)
-- ✔ Cattipotransaccion    → 12 registros (INSERT IGNORE)
-- ✔ Catestadoconciliacion → 3 registros (INSERT IGNORE)
-- ✔ impuestos             → 1 registro (IVA 12%)
-- ✔ departamentos         → 6 registros con IDs específicos
-- ✔ puestos               → 5 registros
-- ✔ empleados             → 5 registros (Empdpi UNIQUE protegido)
-- ✔ bodegas               → 6 registros
-- ✔ lineas                → 6 registros
-- ✔ marcas                → 5 registros
-- ✔ productos             → 5 registros (con lineaid y marcaid)
-- ✔ movimientosinventario → 5 registros (columnas extra eliminadas)
-- ✔ pedidos               → 6 registros (Cliid=1 existe)
-- ✗ INSERT aplicaciones   → tabla no existe, omitido
-- ============================================================
