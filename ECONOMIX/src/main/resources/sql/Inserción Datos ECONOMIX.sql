/***************************************
*		Inserción Datos ECONOMIX
*****************************************/
USE economix;
START TRANSACTION;

/* ---------- 1)  tbl_usuario ---------- */
INSERT INTO tbl_usuario (perfilUsuario, contraseñaUsuario) VALUES
  ('admin'      , 'admin$2025'),
  ('estudiante' , 'estud$2025'),
  ('docente'    , 'doc$2025' ),
  ('invitado'   , 'guest$25' ),
  ('analista'   , 'data$25'  );

/* ---------- 2)  tbl_persona ---------- */
INSERT INTO tbl_persona (nombrePersona, apellidoP, apellidoM, idUsuario) VALUES
  ('Ana'  ,'López'   ,'Hernández', 1),
  ('Bruno','García'  ,'Ortiz'    , 2),
  ('Carla','Ramírez' ,'Soto'     , 3),
  ('Diego','Torres'  ,'Núñez'    , 4),
  ('Elena','Pérez'   ,'Vega'     , 5);

/* ---------- 3)  tbl_contactos ---------- */
INSERT INTO tbl_contactos (numCelular, Correo, idPersona) VALUES
  ('5512340001','ana@example.com'  ,1),
  ('5512340002','bruno@example.com',2),
  ('5512340003','carla@example.com',3),
  ('5512340004','diego@example.com',4),
  ('5512340005','elena@example.com',5);

/* ---------- 4)  tbl_domicilio ---------- */
INSERT INTO tbl_domicilio (ciudad, calle, colonia, número, idPersona) VALUES
  ('CDMX'       ,'Insurgentes 123','Centro' , '10',1),
  ('CDMX'       ,'Reforma 456'   ,'Juárez' , '20',2),
  ('Guadalajara','Av. Vallarta 7','Arcos'  , '30',3),
  ('Monterrey'  ,'Constitución 1','Centro' , '40',4),
  ('Puebla'     ,'5 de Mayo 202' ,'Centro' , '50',5);

/* ---------- 5)  tbl_ingresos ---------- */
INSERT INTO tbl_ingresos (montoIngreso, periodicidadIngreso, fechaIngresos, descripcionIngreso, idUsuario) VALUES
  (15000.00,'Mensual'      ,'2025-07-01','Salario'             ,1),
  (2000.00 ,'Una sola vez' ,'2025-07-05','Venta artículos'     ,2),
  (3000.00 ,'Mensual'      ,'2025-07-03','Beca'                ,3),
  (500.00  ,'Semanal'      ,'2025-07-06','Clases particulares' ,4),
  (10000.00,'Mensual'      ,'2025-07-02','Consultoría'         ,5);

/* ---------- 6)  tbl_conceptoingresos ---------- */
INSERT INTO tbl_conceptoingresos (nombreConcepto, descripcionConcepto, precioConcepto, idIngresos) VALUES
  ('Nómina'        ,'Pago quincenal', 7500.00 ,1),
  ('Bonus'         ,'Productividad' , 7500.00 ,1),
  ('Venta de libros','Librería usada',2000.00 ,2),
  ('Beca CONACYT'  ,'Apoyo mensual' , 3000.00 ,3),
  ('Consultoría TI','Proyecto externo',10000.00,5);

/* ---------- 7)  tbl_gastos ---------- */
INSERT INTO tbl_gastos (descripciónGasto, artículoGasto, montoGasto, fechaGastos, periodoGastos, idUsuario) VALUES
  ('Pago de renta'   ,'Renta'       , 6000.00,'2025-07-01','Mensual' ,1),
  ('Compra despensa' ,'Supermercado', 1500.00,'2025-07-02','Semanal' ,2),
  ('Servicio internet','Internet'   , 600.00 ,'2025-07-03','Mensual' ,3),
  ('Gasolina'        ,'Combustible' , 800.00 ,'2025-07-04','Semanal' ,4),
  ('Cena restaurante','Restaurante' , 1200.00,'2025-07-05','Ocasional',5);

/* ---------- 8)  tbl_conceptogastos ---------- */
INSERT INTO tbl_conceptogastos (nombreConcepto, descripciónConcepto, precioConcepto, idGastos) VALUES
  ('Vivienda'  ,'Pago mensual de renta',6000.00,1),
  ('Alimentos' ,'Compra quincenal'    , 750.00,2),
  ('Abarrotes' ,'Compra quincenal'    , 750.00,2),
  ('Servicios' ,'Pago de internet'    , 600.00,3),
  ('Transporte','Gasolina vehículo'   , 800.00,4);

/* ---------- 9)  tbl_presupuesto ---------- */
INSERT INTO tbl_presupuesto (idUsuario, categoria, montoMaximo, montoGastado, mes, anio) VALUES
  (1,'Vivienda'       ,6500.00,6000.00,7,2025),
  (2,'Alimentos'      ,2000.00,1500.00,7,2025),
  (3,'Servicios'      ,1000.00, 600.00,7,2025),
  (4,'Transporte'     ,1200.00, 800.00,7,2025),
  (5,'Entretenimiento',1500.00,1200.00,7,2025);

COMMIT;