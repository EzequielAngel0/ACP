# ACP - Aplicación para Conductores de Autocamiones

**Versión actual:** `v1.0`  
**Fecha de lanzamiento:** 01 de Octubre de 2025  

Bienvenido a **ACP**, una aplicación diseñada específicamente para conductores de autocamiones. Esta herramienta busca simplificar la gestión de pasajeros, emisión de boletos y registro de viajes, con funcionalidades robustas tanto offline como sincronizadas.

---

## 🎯 Descripción general

ACP permite a los conductores realizar tareas esenciales de manera rápida y eficiente, tales como:

- Registrar pasajeros con origen, destino y descuentos aplicables.
- Calcular automáticamente los precios y emitir boletos impresos vía Bluetooth.
- Gestionar viajes registrados de forma local y sincronizarlos con Firestore.
- Exportar toda la información de los viajes a archivos Excel (.xlsx) para análisis posterior.

Esta es la **primera versión pública** de la aplicación, enfocada en ofrecer un funcionamiento robusto y eficiente en zonas con conectividad limitada.

---

## ✨ Funcionalidades principales

- 🚌 **Selección inicial del número de camión**, persistente durante la sesión.
- 📍 **Ruta del viaje preestablecida** (ejemplo: Guadalajara → Tepic), incluida en todos los boletos.
- 👤 **Registro de pasajeros** con datos como origen, destino y tipo de descuento (estudiantes, adultos mayores, etc.).
- 🧾 **Impresión automática de boletos** con fecha y hora al registrar un pasajero.
- 🔄 **Sincronización con Firestore** para respaldar los datos de tickets registrados.
- 📤 **Exportación a Excel (.xlsx)** con información detallada de cada viaje:
  - ID, origen, destino, precio, fecha, hora, número de camión, ruta y más.
- ✅ **Gestión de permisos** al primer inicio (Bluetooth, almacenamiento, impresión).
- 🛑 **Finalización de viaje segura**, con guardado de datos y cierre limpio de sesión.

---

## 🛠️ Mejoras y correcciones recientes

- ✅ Impresión automática de boletos con fecha y hora actual.
- 🧠 Persistencia de la ruta seleccionada al inicio del viaje.
- 📁 Corrección en la exportación a Excel, incluyendo el campo del número de camión.
- 🔧 Optimización del diseño y ubicación de botones para mejorar la experiencia de usuario.

---

## 📦 Instalación

1. **Descargar:** Obtén el archivo `ACP.apk` desde la sección **Assets** en la pestaña de [Releases](https://github.com/EzequielAngel0/ViajesApp/releases).  
2. **Instalar:** Copia el archivo en tu dispositivo Android e instálalo directamente.  
3. **Permisos:** Acepta los permisos solicitados al abrir la aplicación por primera vez (Bluetooth, almacenamiento, etc.).

---

## 📢 Notas importantes de la versión

- **Estado actual:**  
  Aunque la aplicación ya es funcional, aún está en desarrollo activo. Se invita a los usuarios a probarla y compartir comentarios para optimizar su desempeño.

- **Impresión Bluetooth:**  
  La función de impresión está integrada, pero podría presentar errores en algunos casos debido a pruebas limitadas.

- **Futuras actualizaciones:**  
  - Ampliación de rutas de autobuses.
  - Optimización de procesos y detalles de diseño.
  - Desarrollo de aplicaciones complementarias para clientes y taquillas.

---

## 📄 Licencia

Este proyecto está bajo licencia EULA (Acuerdo de Licencia de Usuario Final). Consulta el archivo [EULA.md](./EULA.md) para más detalles sobre los términos de uso.

---

<h2 align="center">📫 Contáctame</h2>

<table align="center">
  <tr>
    <td style="padding-left: 15px;">
      <ul style="list-style-type: none; padding-left: 0;">
        <li>
          <a href="https://www.linkedin.com/in/angelezequiel">
            <img src="https://img.shields.io/badge/LinkedIn-0077B5?style=flat-square&logo=linkedin&logoColor=white"/>
          </a>
        </li>
        <li>
          <a href="https://x.com/Ezequiel27Angel">
            <img src="https://img.shields.io/badge/X-000000?style=flat-square&logo=x&logoColor=white"/>
          </a>
        </li>
        <li>
          <a href="https://discord.com/users/angelezequiel">
            <img src="https://img.shields.io/badge/Discord-5865F2?style=flat-square&logo=discord&logoColor=white"/>
          </a>
        </li>
        <li>
          <a href="mailto:barbosalomeliangelezequiel@gmail.com">
            <img src="https://img.shields.io/badge/Email-D14836?style=flat-square&logo=gmail&logoColor=white"/>
          </a>
        </li>
      </ul>
    </td>
  </tr>
</table>
