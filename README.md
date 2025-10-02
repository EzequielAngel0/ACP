# 🚍 ACP

Una aplicación móvil para la gestión de boletos de transporte público, diseñada para conductores de autobuses. Permite iniciar viajes, registrar pasajeros, calcular descuentos, generar boletos e imprimirlos por Bluetooth. La app funciona con base de datos local (Room) y sincronización con Firestore.

---

## 🧭 Características principales

- Registro de pasajeros con origen y destino.
- Cálculo automático del precio con base en descuentos:
  - Adulto mayor
  - Estudiante
  - Menor de edad
  - Asociado
- Generación automática de boleto con:
  - Número de boleto
  - Precio final
  - Ruta del viaje
  - Fecha y hora del registro
- Impresión vía Bluetooth al detectar una impresora llamada “Printer”.
- Almacenamiento local en base de datos Room.
- Sincronización posterior a Firestore (opcional).

---

## 🖨 Ejemplo de boleto impreso

```
Boleto
------------------------------
Camión:          11
Origen:          Guadalajara
Destino:         Puerto Vallarta
------------------------------
Número de boleto: 123456789
Precio:           $25.00
Fecha:            01/10/2025
Hora:             16:45:22
------------------------------
Para información de su siguiente viaje
llame a la terminal de destino.
```

---

## 🚦 Flujo de la aplicación

1. **SeleccionarCamionActivity**
   - Aparece al abrir la app por primera vez.
   - El usuario elige el número de camión (6, 11, 18).
   - Se guarda en `SharedPreferences` y no vuelve a mostrarse.

2. **MainActivity**
   - Menú principal con botones para:
     - Iniciar viaje
     - Registrar pasajero
     - Finalizar viaje
     - Sincronizar

3. **RegistrarPasajeroActivity**
   - El usuario selecciona origen/destino, aplica descuento (uno por pasajero).
   - Se calcula el precio y se imprime el ticket al guardar.
   - El ticket se guarda en Room y muestra mensaje de éxito.

---

## 🛑 Permisos requeridos

Se solicitan al abrir la app:

- `BLUETOOTH_CONNECT` (Android 12+)
- `BLUETOOTH_ADMIN`
- `BLUETOOTH`
- `ACCESS_FINE_LOCATION` (necesario para detectar dispositivos Bluetooth)

La validación se hace en `SeleccionarCamionActivity` para evitar redundancia.

---

## 🗃 Estructura de datos

La tabla local `TicketEntity` contiene:

| Campo              | Tipo     | Descripción                            |
|--------------------|----------|----------------------------------------|
| id                 | Int      | ID incremental del boleto              |
| idViaje            | String   | ID del viaje activo                    |
| origen             | String   | Lugar de partida                       |
| destino            | String   | Lugar de destino                       |
| precio             | Double   | Precio final con descuento             |
| fecha              | String   | Fecha del registro                     |
| hora               | String   | Hora del registro                      |
| numeroCamion       | String   | Número del camión asignado             |
| descuentoAplicado  | String   | Tipo de descuento aplicado             |
| sincronizado       | Boolean  | Si ya fue sincronizado a Firestore     |
| ruta               | String   | Ruta seleccionada al iniciar el viaje  |

---

## 🧠 Aprendizaje obtenido

Con esta aplicación se aprendió a:

- Manejar Bluetooth en Android para impresión.
- Usar Room como base de datos local.
- Aplicar lógica de negocio con descuentos.
- Almacenar datos persistentes con `SharedPreferences`.
- Controlar flujos con una pantalla que solo aparece una vez.
- Implementar permisos condicionales por versión de Android.
- Construir interfaces dinámicas con `Spinner`, `CheckBox`, y `TextView`.

---

## 🛠 Herramientas usadas

- **Android Studio**
- **Kotlin**
- **Room Database**
- **Bluetooth API**
- **Firestore (opcional para sincronizar)**
- **SharedPreferences**

---

## 📂 Autor

**Barbosa Lomelí Angel Ezequiel**  
Alumno CETI Tonalá Virtual  
ID: 22300183
