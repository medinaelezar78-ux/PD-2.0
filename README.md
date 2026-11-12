# Sistema de registro de stands – FILEY

Sistema en Java para administrar los stands de la feria FILEY: inventario ordenado por área, reservas de editoriales aprobadas, pagos (anticipo y pago total), notificaciones por correo, transferencia de stands bloqueados y mapa de colores por estado.

Es la migración a Java de un programa en C (lista enlazada de stands), ampliado con los requisitos de las notas de análisis.

## Características

- Inventario de stands ordenado por área (menor a mayor), con búsqueda, actualización y eliminación.
- Cuatro estados con color en el mapa:
  - 🟢 **Disponible**
  - 🟡 **Reservado**
  - 🔴 **Ocupado**
  - ⚪ **Bloqueado** (vendido con anterioridad)
- Reservas solo para editoriales aprobadas, con fecha límite para el pago total.
- Con el **15 %** pagado, la reserva pasa de Reservado a Ocupado.
- **Descuento por pronto pago**; pasada la fecha, se elimina.
- Cancelación automática de reservas que vencen sin anticipo.
- Notificaciones por correo (reserva, pago por vencer, stand ocupado, reserva cancelada, transferencia). El envío está simulado.
- Transferencia de stands bloqueados directo a la editorial, sin pasar por Disponible.
- Parámetros configurables: costo por m², porcentaje de anticipo, descuento, plazos y correo remitente.
- Base de datos **H2 en memoria** como respaldo de los datos.

## Estructura del proyecto

```
.
├── Main.java                         Pruebas del sistema por línea de comandos
├── DOCUMENTACION.md                  Casos de uso, historias de usuario y diagrama de clases
├── datos/                            Clases de datos
│   ├── EstadoStand.java
│   ├── Stand.java
│   ├── Editorial.java
│   ├── Reserva.java
│   ├── Configuracion.java
│   └── RepositorioH2.java
├── operaciones/                      Lógica de negocio
│   ├── GestorStands.java
│   ├── GestorReservas.java
│   └── MapaStands.java
└── notificaciones/                   Avisos por correo
    ├── Notificador.java
    ├── NotificadorCorreo.java
    └── ServicioNotificaciones.java
```

## Requisitos

- **JDK 17 o superior** (comprueba con `java -version`).
- **H2** (opcional): el jar de `com.h2database:h2` (probado con 2.2.224) dentro de una carpeta `lib/`. Sin el jar, el programa funciona igual pero sin base de datos y lo avisa al iniciar.

> El jar de H2 no está incluido en el repositorio. Descárgalo desde [Maven Central](https://central.sonatype.com/artifact/com.h2database/h2) y colócalo en `lib/`.

## Compilar y ejecutar

Desde la raíz del repositorio.

**Windows (PowerShell):**

```powershell
javac -d out -cp "lib/*" Main.java datos/*.java operaciones/*.java notificaciones/*.java
java -cp "out;lib/*" Main
```

**Linux / Mac:**

```bash
javac -d out -cp "lib/*" Main.java datos/*.java operaciones/*.java notificaciones/*.java
java -cp "out:lib/*" Main
```

Si los acentos se ven mal en Windows, ejecuta antes `chcp 65001` y agrega `-Dfile.encoding=UTF-8` al comando `java`.

### En VS Code

1. Instala la extensión **Extension Pack for Java**.
2. Abre la carpeta del proyecto con **File → Open Folder**.
3. Abre `Main.java` y presiona **Run** sobre el método `main`.

## Qué prueba `Main.java`

La ejecución recorre, en orden:

1. Inserción ordenada de stands por área, búsqueda, actualización (con reubicación) y borrado.
2. Reserva y anticipo del 15 %: el stand pasa a Ocupado.
3. Pago total con descuento por pronto pago.
4. Rechazo de una editorial no aprobada y de un stand bloqueado.
5. Transferencia de un stand bloqueado.
6. Aviso de pago por vencer y cancelación de una reserva sin anticipo.
7. Mapa final con colores y lista de lugares elegidos.

## Arquitectura

| Paquete | Responsabilidad |
|---------|-----------------|
| `datos` | Representa la información (stand, editorial, reserva, estado, configuración) y la respalda en H2. |
| `operaciones` | Aplica las reglas de negocio: orden del inventario, reservas, pagos, vencimientos, transferencias y mapa. |
| `notificaciones` | Redacta y envía los avisos a través de la interfaz `Notificador`. |

Para enviar correos reales basta con crear otra implementación de `Notificador` (por ejemplo con JavaMail); la lógica del negocio no cambia.

## Documentación

En [DOCUMENTACION.md](DOCUMENTACION.md) están los casos de uso, las historias de usuario, el diagrama de clases, el diagrama de estados y las reglas de negocio.

## Limitaciones

- No tiene interfaz interactiva: se opera por código y pruebas en consola.
- Los correos son simulados (se imprimen y se guardan en un historial).
- H2 es solo respaldo y se pierde al terminar el programa.

## Autor

[Tu nombre] – [Materia / grupo]
