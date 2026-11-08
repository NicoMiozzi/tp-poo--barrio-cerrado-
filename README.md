# Barrio Los Aromos — TPO Programación Orientada a Objetos

Sistema de gestión del barrio cerrado «Los Aromos» (Propuesta A). Ordena la relación entre la administración y los vecinos: reclamos, expensas y pagos, luminarias y cámaras, ingreso de visitas y reportes.

**Integrantes:** Pesce, Luca Gabriel · Miozzi, Nicolás Agustín
**Materia:** Programación Orientada a Objetos — Prof. León, María Ángela

## Requisitos

- Java 17 o superior (JDK).
- Para las pruebas: JUnit 5 (Maven lo descarga solo, o IntelliJ lo agrega con un clic).

## Cómo ejecutarlo

Siempre desde la **carpeta raíz del proyecto** (la que tiene este README), porque los datos se guardan en `datos/barrio.dat` y los reportes en `reportes/`.

### Opción 1: IntelliJ IDEA
1. `File > Open` y elegir la carpeta del proyecto. IntelliJ detecta el `pom.xml` y lo abre como proyecto Maven.
2. Ejecutar `src/barrio/ui/Main.java` (botón ▶ al lado del `main`).
3. Pruebas: clic derecho sobre la carpeta `test` > `Run 'All Tests'`.

### Opción 2: Maven
```bash
mvn package                                  # compila y corre las 40 pruebas
java -jar target/barrio-los-aromos-2.0.jar   # ejecuta el programa
```

### Opción 3: solo el JDK (sin Maven)
```bash
# Windows (PowerShell)
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out barrio.ui.Main

# Linux / macOS
javac -d out $(find src -name "*.java")
java -cp out barrio.ui.Main
```

## Datos de prueba

La primera vez que se ejecuta no existe `datos/barrio.dat`, entonces se cargan datos de prueba y se guardan. Las ejecuciones siguientes leen el archivo (los datos se conservan). **Para empezar de cero, borrar la carpeta `datos/`.**

| Qué | Datos |
|---|---|
| Lotes | 150 lotes (1 a 150) de 600 a 1000 m², repartidos entre 10 titulares |
| Lote 1 | Titular Ana Gómez (DNI **20111111**). Residentes: Lucía Gómez, familiar **autorizada** a reclamar (DNI **40111111**), y Mario Ríos, inquilino **no autorizado** (DNI **35111111**) |
| Lote 2 | Titular Bruno Díaz (DNI **20222222**) |
| Personal | Legajo 1 administradora · 10 y 11 guardias · **20** electricista · **21** plomero · **22** jardinera |
| Equipos | Cámaras CAM-01 a CAM-03 · Luminarias LUM-01 a LUM-05 (sector «Calle 1») y LUM-06 («Calle 2») |
| Fallas cargadas | CAM-02 sin funcionar hace 3 días · LUM-01, LUM-02 y LUM-03 con falla |
| Expensas | Emitidas los últimos 4 meses (presupuesto $15.000.000). Todos pagaron, salvo el **lote 7** (debe 4 períodos → **moroso**) y el **lote 12** (debe 2) |
| Reclamos | N.º 1 de seguridad del lote 1, sin atender hace 2 días · N.º 2 de mantenimiento del lote 2 |
| Visitas | Juan Visitante (DNI **30111222**) autorizado hoy al lote 1 · proveedor Pedro Jardines (DNI **28999000**) autorizado 30 días, de 8 a 18 |

### Guion rápido para la demostración
1. **Reclamos > Presentar** con lote 1 y DNI 35111111 → se rechaza (inquilino no autorizado). Con 40111111 → se crea.
2. **Visitas > Registrar ingreso** DNI 30111222, lote 1 → habilitado. Repetirlo → error «ya tiene un ingreso sin egreso». DNI 99999999 → acceso denegado.
3. **Luminarias > Registrar falla** LUM-04 y **Generar orden** con legajo 20 → sale con prioridad **ALTA** (4 luminarias de Calle 1 fallando).
4. **Expensas > Registrar pago** del lote 7 por $50.000 → se ve cómo se imputa primero a intereses y después al capital más viejo.
5. **Reportes > Controles automáticos** → escala el reclamo 1 y avisa por la cámara CAM-02.
6. Menú principal > **Cambiar fecha del sistema**: permite adelantar el reloj para probar los plazos (5 días para reabrir, 24 h, 48 h, vencimientos).

## Estructura

```
src/barrio/
  modelo/        clases del dominio (personas, lotes, expensas, reclamos, equipos, visitas)
  servicios/     GestorReclamos, ServicioExpensas, GestorMantenimiento, ServicioReportes, Notificador
  controladores/ ReclamoController, ControladorAcceso
  persistencia/  RepositorioDatos (lectura y escritura del archivo)
  excepciones/   excepciones propias de las reglas de negocio
  ui/            menú por consola y datos de prueba
test/barrio/     pruebas unitarias (JUnit 5)
docs/            plan de pruebas, informe final y UML (docs/uml)
```

## Requisitos técnicos y dónde se ven

| Requisito | Dónde |
|---|---|
| Herencia y polimorfismo | `Persona` → `Titular`/`Residente`; `Personal` → `Guardia`/`EmpleadoMantenimiento`/`Administrador`; `Equipo` → `Camara`/`Luminaria`; `Autorizacion` → `AutorizacionRecurrente`; `SubMenu` → cada menú |
| Clases abstractas e interfaces | `Persona`, `Personal`, `Equipo`, `SubMenu` (abstractas); `Notificador`, `EstrategiaPrioridadOrden` (interfaces) |
| Encapsulamiento | atributos privados; `protected` en constructores de clases abstractas, `Residente.setAutorizadoReclamos` y `Persona.estaVacio`; métodos de paquete en `Expensa.aplicar/revertir` |
| Colecciones | `List`, `TreeMap` (lotes y equipos ordenados), `HashMap` (numeradores) |
| Comparable | `Lote` (número), `Expensa` (período), `Reclamo` (prioridad y fecha), `Personal` (legajo) |
| Comparator | `ComparadorLotesPorDeuda` (reporte de morosidad, de mayor a menor deuda) y `ComparadorEquiposPorSector` (reporte de equipos) |
| Excepciones | 8 excepciones propias en `barrio.excepciones` con `throw`/`throws`; `try/catch/finally` en `SubMenu.ejecutar`, `Main` y `RepositorioDatos.guardarEnArchivo` |
| Entrada/salida | `RepositorioDatos` (serialización a `datos/barrio.dat`) y exportación de reportes a `.txt` |
| Interfaz | menú por consola con submenús |
| Patrones | ver `docs/informe-final.md` |
