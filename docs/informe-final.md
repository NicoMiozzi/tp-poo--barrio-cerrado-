# Informe final — Etapa 2

**Propuesta:** A — Gestión del barrio cerrado «Los Aromos»
**Integrantes:** Pesce, Luca Gabriel · Miozzi, Nicolás Agustín

## 1. Cambios respecto del diseño (diagrama de clases final de la etapa 1)

El diagrama actualizado está en `docs/uml/diagrama-clases-etapa2.png` (fuente PlantUML en el `.puml` de la misma carpeta).

| # | Cambio | Motivo |
|---|---|---|
| 1 | La clase `Visita` se reemplazó por `Autorizacion`, `AutorizacionRecurrente` (subclase) y `RegistroIngreso`, y se agregó `ControladorAcceso` | Así estaba modelado en el diagrama de secuencia «Registrar ingreso y egreso», que debía funcionar tal cual. Además separa la autorización (la da el vecino) del registro (lo hace el guardia), y la subclase resuelve RN-20 sobreescribiendo `esVigente()` |
| 2 | Se agregó `RepositorioDatos` | Aparecía en los dos diagramas de secuencia (`guardar()` → confirmación) pero faltaba en el de clases. Es la clase que guarda en el archivo |
| 3 | `presentarReclamo()` pasó de `Residente` a `ReclamoController` | En el diagrama de secuencia el vecino le pide al controlador, que consulta `Lote.esTitularOAutorizado()` y crea el `Reclamo`. En el código se usa el nombre del diagrama de clases (`ReclamoController`); en la secuencia figuraba como «ControladorReclamos» |
| 4 | `Titular` y `Residente` heredan de `Persona`; se agregaron apellido y correo | El enunciado pide esos datos y la herencia evita repetirlos. `puedePresentarReclamos()` es abstracto y cada subclase lo responde distinto (RN-07) |
| 5 | Se agregó `Administrador extends Personal` | El administrador también es personal (legajo, nombre, fecha de ingreso) |
| 6 | `calcularPrioridadFalla()` recibe la lista de equipos y cada equipo delega en su `EstrategiaPrioridadOrden` | RN-16 necesita contar las luminarias del sector. Así se combinan Experto (el equipo calcula su prioridad) y abierto/cerrado (la regla está en la estrategia) |
| 7 | Los estados, tipos y prioridades pasaron de `String` a `enum` | Evita valores inválidos y permite que `EstadoReclamo.siguiente()` controle RN-02 |
| 8 | Se agregaron `AsientoAjuste`, `Comentario`, `Falla` y `Notificacion` | RN-13 (ajustes), RF-08 (comentarios), RF-19 (fallas) y A-08 (registro de notificaciones) |
| 9 | `Expensa` guarda período (`YearMonth`), capital e interés pagados | Hacen falta para la imputación de pagos (RN-11) y el cálculo del interés |
| 10 | `Notificador.notificar()` recibe fecha, destinatario y mensaje | El mismo notificador sirve para avisar al administrador y para la notificación formal de morosidad al titular |
| 11 | Paquetes nuevos `excepciones` y `ui` | Excepciones propias de las reglas de negocio y el menú por consola |

## 2. Supuestos agregados durante la codificación

Se suman a los A-01 a A-08 de la etapa 1.

- **A-09** Al reabrir un reclamo resuelto vuelve a ASIGNADO, con el mismo responsable.
- **A-10** El cierre a los 5 días, el escalamiento a las 24 h, el aviso de cámaras y la morosidad se controlan con la opción «Correr controles automáticos» (en un sistema real sería una tarea programada).
- **A-11** Un reclamo «fue atendido» cuando pasa a EN_CURSO.
- **A-12** El interés se cobra por mes o fracción: un día después del vencimiento ya es un mes de mora.
- **A-13** Un pago parcial cancela primero los intereses de todos los períodos (del más antiguo al más nuevo) y con el resto cancela capital, también del más antiguo al más nuevo.
- **A-14** El coeficiente de participación es la superficie del lote dividida la superficie total del barrio.
- **A-15** Para RN-16, una luminaria «falla» si está con falla o en reparación.
- **A-16** El asiento de ajuste anula el pago completo; si correspondía otro importe, se registra el pago correcto.
- **A-17** Un reclamo se puede asignar a cualquier empleado (guardia o mantenimiento), según su tipo.
- **A-18** El programa permite cambiar la fecha del sistema para poder probar los plazos sin esperar días.

## 3. Patrones en el código

| Patrón | Dónde se ve |
|---|---|
| SOLID — Responsabilidad única | `GestorReclamos`, `ServicioExpensas`, `GestorMantenimiento`, `ServicioReportes` y `RepositorioDatos`: cada uno se ocupa de un solo tema |
| SOLID — Abierto/cerrado | `EstrategiaPrioridadOrden` con `PrioridadCamara` y `PrioridadLuminaria`. Un equipo nuevo agrega su estrategia sin tocar `GestorMantenimiento` |
| SOLID — Inversión de dependencias | Los gestores reciben un `Notificador` (interfaz) en el constructor; `SistemaBarrio` decide usar `NotificadorAdministrador` |
| GRASP — Controlador | `ReclamoController` y `ControladorAcceso` reciben los pedidos del menú y los delegan |
| GRASP — Experto | `Reclamo` valida sus transiciones, `Lote` sabe si es moroso y quién puede reclamar, `Expensa` calcula su interés, cada `Equipo` calcula su prioridad |
| GRASP — Creador | `Lote.crearExpensa()`: el lote contiene sus expensas y tiene los datos para calcularlas |
| GRASP — Bajo acoplamiento | `GestorMantenimiento` no pregunta qué tipo de equipo es: llama a `calcularPrioridadFalla()` (polimorfismo) y notifica por la interfaz |

## 4. Dificultades encontradas

> Completar con lo que vivió el grupo. Algunas que surgieron al programar:

- Coordinar los dos diagramas de la etapa 1, que no usaban los mismos nombres (por ejemplo `Visita` en el de clases y `Autorizacion`/`RegistroIngreso` en el de secuencia).
- El cálculo de intereses con pagos parciales: la primera versión no seguía sumando interés correctamente después de un pago (ver plan de pruebas, sección 3).
- Probar reglas que dependen del tiempo (24 h, 48 h, 5 días, vencimientos): se resolvió pasando la fecha como parámetro y con la opción para cambiar la fecha del sistema.

## 5. Distribución de tareas

> Completar con lo que hizo cada uno (tiene que coincidir con los commits del repositorio).

| Integrante | Tareas |
|---|---|
| Pesce, Luca Gabriel | |
| Miozzi, Nicolás Agustín | |

## 6. Reflexión del grupo

> Completar con la opinión del grupo.
