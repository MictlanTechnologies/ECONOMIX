/***************************************
*		Update Datos ECONOMIX
*****************************************/

USE economix;

DELIMITER //

/* ---------- a)  tbl_gastos ---------- */
DROP PROCEDURE IF EXISTS sp_update_gasto //
CREATE PROCEDURE sp_update_gasto (
    IN p_idGastos     INT,
    IN p_descripcion  TEXT,
    IN p_articulo     VARCHAR(100),
    IN p_monto        DECIMAL(10,2),
    IN p_fecha        DATE,
    IN p_periodo      VARCHAR(50)
)
BEGIN
    UPDATE tbl_gastos
       SET descripciónGasto = COALESCE(p_descripcion, descripciónGasto),
           artículoGasto    = COALESCE(p_articulo   , artículoGasto),
           montoGasto       = COALESCE(p_monto      , montoGasto),
           fechaGastos      = COALESCE(p_fecha      , fechaGastos),
           periodoGastos    = COALESCE(p_periodo    , periodoGastos)
     WHERE idGastos = p_idGastos;
END //

/* ---------- b)  tbl_ingresos ---------- */
DROP PROCEDURE IF EXISTS sp_update_ingreso //
CREATE PROCEDURE sp_update_ingreso (
    IN p_idIngresos   INT,
    IN p_monto        DECIMAL(10,2),
    IN p_periodicidad VARCHAR(50),
    IN p_fecha        DATE,
    IN p_descripcion  TEXT
)
BEGIN
    UPDATE tbl_ingresos
       SET montoIngreso        = COALESCE(p_monto      , montoIngreso),
           periodicidadIngreso = COALESCE(p_periodicidad, periodicidadIngreso),
           fechaIngresos       = COALESCE(p_fecha       , fechaIngresos),
           descripcionIngreso  = COALESCE(p_descripcion , descripcionIngreso)
     WHERE idIngresos = p_idIngresos;
END //

/* ---------- c)  tbl_presupuesto ---------- */
DROP PROCEDURE IF EXISTS sp_update_presupuesto //
CREATE PROCEDURE sp_update_presupuesto (
    IN p_idPresupuesto INT,
    IN p_montoGastado  DECIMAL(12,2),
    IN p_montoMaximo   DECIMAL(12,2)
)
BEGIN
    UPDATE tbl_presupuesto
       SET montoGastado = COALESCE(p_montoGastado, montoGastado),
           montoMaximo  = COALESCE(p_montoMaximo , montoMaximo)
     WHERE idPresupuesto = p_idPresupuesto;
END //

-- ---------- Restauramos delimitador ----------
DELIMITER ;

/* ----- Ejemplos de uso -----
CALL sp_update_gasto(3, NULL, NULL, 650.00, NULL, NULL);
CALL sp_update_ingreso(1, 16000.00, NULL, NULL, NULL);
CALL sp_update_presupuesto(2, 1800.00, NULL);
*/