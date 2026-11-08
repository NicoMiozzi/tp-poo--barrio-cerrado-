# Plan de pruebas

## 1. Pruebas unitarias (JUnit 5)

Están en `/test`. Usan un repositorio en memoria y fechas fijas, así el resultado no depende del día en que se corren. Resultado de la última ejecución (`mvn package`): **40 pruebas, 40 correctas, 0 fallidas**.

| Clase de prueba | Qué verifica | Pruebas | Resultado |
|---|---|---|---|
| `ReclamoTest` | Prioridad según tipo (RN-01), estados en orden sin saltos (RN-02), un único responsable (RN-04), reapertura dentro de 5 días y cierre después (RN-03), escalamiento a las 24 h (RN-06), orden natural | 9 | OK |
| `LoteYExpensaTest` | Titular o residente autorizado (RN-07), expensa = presupuesto × coeficiente (RN-08), vencimiento día 10 (RN-09), interés 3 % mensual (RN-10), morosidad con más de 3 períodos (RN-12) | 6 | OK |
| `ServicioExpensasTest` | Emisión por lote, imputación de pagos parciales: intereses primero y luego capital del período más antiguo (RN-11), interés posterior sobre el capital que quedó, pago mayor a la deuda rechazado (A-04), asiento de ajuste sin borrar el pago (RN-13), notificación de morosidad y quita de la marca al regularizar | 7 | OK |
| `GestorReclamosTest` | Presentar reclamo (caso de uso de secuencia, flujo normal y alternativo), máximo 5 reclamos en curso por empleado (RN-05), escalamiento con notificación, cierre automático | 5 | OK |
| `GestorMantenimientoTest` | Cámara → prioridad alta (RN-15), luminaria normal → alta si fallan más de 3 del sector (RN-16), aviso de cámara con más de 48 h (RN-17), cierre de orden con datos completos (RN-18), reporte de equipos | 6 | OK |
| `ControladorAccesoTest` | Ingreso con autorización vigente (caso de uso de secuencia), acceso denegado (RN-19), segundo ingreso sin egreso (RN-21), proveedor recurrente de hasta 30 días (RN-20), autoriza solo quien vive en el lote (A-07) | 5 | OK |
| `RepositorioDatosTest` | Los datos se conservan entre ejecuciones (escritura y lectura del archivo); archivo dañado → `IOException` | 2 | OK |

## 2. Pruebas funcionales (desde el menú)

Se ejecutaron sobre el programa compilado, con los datos de prueba recién cargados (carpeta `datos/` borrada antes de empezar), el 07/11/2026.

| N.º | Caso de uso | Dato de entrada | Resultado esperado | Resultado obtenido | OK |
|---|---|---|---|---|---|
| CP-01 | Presentar reclamo | Lote 1, DNI 20111111 (titular), tipo Convivencia, «Música fuerte de noche» | Reclamo creado con número y prioridad BAJA | «Reclamo creado: numero 3, prioridad BAJA» | ✔ |
| CP-02 | Presentar reclamo (alternativo) | Lote 1, DNI 35111111 (inquilino no autorizado) | Rechazo: solo el titular o una persona autorizada | «NO SE PUDO: Solo el titular o una persona autorizada puede presentar reclamos.» | ✔ |
| CP-03 | Presentar reclamo | Lote 1, DNI 40111111 (familiar autorizada), tipo Administrativo | Reclamo creado | «Reclamo creado: numero 4, prioridad BAJA» | ✔ |
| CP-04 | Seguimiento de reclamo | Reclamo 2: asignar a legajo 21 → En curso → Resuelto → Reabrir | Avanza en orden y al reabrir vuelve a ASIGNADO; historial completo | Historial: ABIERTO, ASIGNADO, EN_CURSO, RESUELTO, ASIGNADO (Reabierto) | ✔ |
| CP-05 | Cambiar estado (error) | Reclamo 3 (abierto) → Resuelto | Rechazo por saltear estados | «No se puede pasar de ABIERTO a RESUELTO. El siguiente estado es ASIGNADO.» | ✔ |
| CP-06 | Registrar ingreso | DNI 30111222, lote 1 | Ingreso habilitado | «Ingreso habilitado: Juan Visitante ... DENTRO DEL BARRIO» | ✔ |
| CP-07 | Registrar ingreso (alternativo) | DNI 30111222 otra vez, sin egreso | Error: ya tiene un ingreso sin egreso | «NO SE PUDO: La persona con documento 30111222 ya tiene un ingreso sin egreso.» | ✔ |
| CP-08 | Registrar ingreso (alternativo) | DNI 99999999, lote 1 | Acceso denegado | «NO SE PUDO: Acceso denegado: no hay autorizacion vigente ...» | ✔ |
| CP-09 | Registrar egreso | DNI 30111222 | Egreso registrado | «Egreso registrado: ... egreso 2026-11-07T17:39» | ✔ |
| CP-10 | Autorizar proveedor (error) | Lote 1, titular, del 07/11 al 07/12 (31 días) | Rechazo por superar 30 días | «Una autorizacion recurrente puede durar hasta 30 dias (se pidieron 31).» | ✔ |
| CP-11 | Falla y orden | Falla en LUM-04 y orden con legajo 20 | Orden con prioridad ALTA (4.ª luminaria de Calle 1) | «Orden 1 generada con prioridad ALTA.» | ✔ |
| CP-12 | Cerrar orden (error y correcto) | Orden 1 sin descripción; después con «Se cambió la lámpara» | Primero rechazo; después cierre y equipo operativo | «Para cerrar la orden hay que registrar fecha, tecnico y descripcion...» y luego «Orden cerrada. El equipo vuelve a estar OPERATIVO.» | ✔ |
| CP-13 | Registrar pago parcial | Lote 7 (deuda $430.000), $50.000 en efectivo | $30.000 a intereses de los 4 períodos y $20.000 al capital de julio | Julio: interés 12000 + capital 20000; agosto 9000; septiembre 6000; octubre 3000. Deuda restante $380.000, sigue moroso | ✔ |
| CP-14 | Registrar pago (error) | Lote 12, $9.999.999 | Rechazo: supera la deuda | «El pago ($9999999.0) supera la deuda del lote ($209000.0)...» | ✔ |
| CP-15 | Controles automáticos | Opción 5 del menú de reportes | Escala el reclamo 1 y avisa por CAM-02 (72 h) | «Reclamos escalados: 1», «Camaras informadas: 1» y las dos notificaciones | ✔ |
| CP-16 | Reportes | Reporte de morosidad y de equipos; exportar | Lote 7 MOROSO primero, lote 12 después; equipos con falla; 3 archivos .txt | Coincide; se generaron `reportes/*.txt` | ✔ |
| CP-17 | Persistencia | Salir y volver a ejecutar; ver reclamo 3 | «Datos cargados» y el reclamo 3 sigue existiendo | «Datos cargados de datos/barrio.dat» y se muestra el reclamo 3 | ✔ |
| CP-18 | Entrada inválida | Escribir «abc» como opción | Pide de nuevo un número, sin cortar el programa | «Tiene que ser un numero entero.» | ✔ |

## 3. Errores encontrados y corregidos

| Error | Cómo se detectó | Corrección |
|---|---|---|
| Después de un pago parcial, el interés de los meses siguientes se calculaba como si el capital pagado nunca se hubiera adeudado, y el interés del mes siguiente quedaba en $0 | Caso CP-13: el estado de cuenta mostraba «interés a hoy $0» en julio | `Expensa` ahora guarda el interés pendiente y la fecha del último pago («corte»); desde ahí solo suma interés sobre el capital que quedó. Se agregó la verificación en `ServicioExpensasTest` |
