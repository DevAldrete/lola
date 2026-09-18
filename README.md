# Sistema de Gestión de Logística y Envíos (Escenario 2)

Sistema integral de gestión de operaciones logísticas, rastreo de paquetes, planificación de rutas y optimización de entregas construido mediante **estructuras de datos avanzadas** y **algoritmos de optimización** desarrollados a la medida.

---

## Descripción General

Este proyecto resuelve los desafíos operativos de una empresa de logística moderna:
- **Gestión e ingesta de paquetes y rutas** con volúmenes mínimos de operación (50 paquetes, 10 rutas/centros).
- **Procesamiento de envíos prioritarios y urgentes**.
- **Modelado de red de transporte interurbana y distribución geográfica**.
- **Búsqueda e indexación instantánea de guías y paquetes**.
- **Cálculo recursivo de tiempos e iteración jerárquica de la flotilla**.

> **Nota Teórica / Técnica**: Las estructuras de datos principales y algoritmos de ordenamiento/búsqueda fueron implementados desde cero sin depender de bibliotecas preconstruidas (`java.util.PriorityQueue`, `java.util.HashMap`, etc.) para cumplir con los lineamientos del curso.

---

## Cobertura de Requisitos y Estructuras de Datos

| Estructura / Algoritmo | Componente en el Sistema | Problema que Resuelve | Ventaja Operativa |
| :--- | :--- | :--- | :--- |
| **Cola de Prioridad** (`PriorityQueue`) | Módulo de Despacho de Urgencias | Priorización de paquetes urgentes/expres en tiempo real. | Permite extraer el paquete con mayor urgencia en $O(1)$ sin ordenar la lista completa. |
| **Tabla Hash** (`HashMap`) | Rastreo por Número de Guía | Consulta e indexación inmediata de un paquete usando su `idGuia`. | Búsqueda en tiempo promedio constante $O(1)$, evitando recorridos lineales $O(n)$. |
| **Grafo Dirigido/Ponderado** | Red de Rutas y Cobertura (10+ Nodos) | Representación de nodos (centros/ciudades) y aristas (rutas con tiempos/distancias). | Facilita el modelado realista de conexiones entre centros de distribución y transporte. |
| **Árbol Binario** | Jerarquía de Centros y Flotilla | Organización de la estructura organizacional y de distribución (Nacional $\rightarrow$ Regional $\rightarrow$ Local). | Organización jerárquica con búsquedas e inserciones logarítmicas $O(\log n)$. |
| **Divide y Vencerás** | Particionamiento Geográfico | División de una gran zona o volumen de entregas en subzonas de trabajo. | Reduce la complejidad de asignación asignando subgrupos a flotillas independientes. |
| **Recursividad** | Análisis Acumulado de Rutas | Cálculo del tiempo, costo y distancia total acumulada al transitar una cadena de rutas. | Permite recorrer tramos encadenados de forma concisa y elegante. |
| **Algoritmo de Ordenamiento** (ej. QuickSort/MergeSort) | Reportes Consolidados | Clasificación de listas de envíos o rutas por fecha, costo o prioridad. | Organización eficiente $O(n \log n)$ para la generación de reportes operativos. |
| **Algoritmo de Búsqueda** (ej. Búsqueda Binaria) | Consultas Secundarias | Búsqueda sobre catálogos o arreglos ordenados por criterios secundarios. | Búsqueda logarítmica $O(\log n)$ cuando no se utiliza la clave primaria de la Hash Table. |

---

## Historias de Usuario (BDD - Given / When / Then)

### Historia 1: Procesar Despacho Urgente
**Como** Coordinador de Operaciones  
**Quiero** procesar de manera inmediata las entregas clasificadas como críticas  
**Para** garantizar que los medicamentos u envíos prioritarios salgan primero.

* **Escenario 1: Extracción exitosa del paquete más urgente**
  * **GIVEN** que la Cola de Prioridad contiene paquetes con prioridades de 1 (Normal) a 5 (Urgencia Crítica),
  * **WHEN** el usuario selecciona la opción "Despachar Siguiente Envió",
  * **THEN** el sistema extrae e identifica el paquete de prioridad 5 (máxima) en tiempo $O(1)$ y actualiza su estado a `EN_TRANSITO`.

---

### Historia 2: Rastrear Paquete por Número de Guía
**Como** Agente de Servicio al Cliente  
**Quiero** consultar la información detallada de un paquete usando su código de guía  
**Para** informarle la localización exacta al cliente en tiempo real.

* **Escenario 1: Búsqueda exitosa en tiempo constante**
  * **GIVEN** un sistema cargado con 50 o más paquetes registrados en el `HashMap`,
  * **WHEN** el agente ingresa la guía `"MX-99823"`,
  * **THEN** el sistema recupera inmediatamente en $O(1)$ la entidad `Paquete` mostrando su origen, destino, costo y estado sin iterar sobre los demás registros.

* **Escenario 2: Guía no registrada**
  * **GIVEN** la tabla Hash de paquetes,
  * **WHEN** el agente busca la guía `"NON-EXISTENT"`,
  * **THEN** el sistema notifica que la guía no existe sin provocar errores de ejecución ni retrasos.

---

### Historia 3: Visualizar Red de Rutas y Calcular Tiempos
**Como** Planificador de Transportes  
**Quiero** visualizar las conexiones de la red logística y calcular el tiempo acumulado de un trayecto  
**Para** optimizar las salidas entre centros de distribución.

* **Escenario 1: Travesía recursiva de rutas**
  * **GIVEN** un Grafo cargado con al menos 10 nodos (ciudades/centros) y sus respectivas aristas (rutas con tiempos),
  * **WHEN** se solicita calcular el costo/tiempo acumulado de la ruta `"Centro A -> Centro B -> Centro C"`,
  * **THEN** la función recursiva realiza la suma de cada tramo y despliega el tiempo total exacto acumulado.

---

### Historia 4: Asignación de Entregas por Zonas (Divide y Vencerás)
**Como** Supervisor de Logística  
**Quiero** subdividir un lote masivo de entregas en sectores geográficos más pequeños  
**Para** asignar flotillas de manera eficiente.

* **Escenario 1: Partición geográfica de entregas**
  * **GIVEN** un conjunto amplio de paquetes asignados a una región metropolitana,
  * **WHEN** se ejecuta la función de particionamiento geográfico,
  * **THEN** el algoritmo Divide y Vencerás segmenta la lista en cuadrantes independientes y distribuye equilibradamente la carga entre los vehículos disponibles.

---

### Historia 5: Generar Reportes Ordenados
**Como** Director de Logística  
**Quiero** emitir reportes ordenados por costo de envío o fecha límite  
**Para** analizar la rentabilidad y prioridades operativas.

* **Escenario 1: Generación de reporte ordenado por costo**
  * **GIVEN** la lista completa de paquetes en memoria,
  * **WHEN** se solicita el reporte de "Paquetes ordenados por Costo de Transporte",
  * **THEN** el sistema aplica el algoritmo de ordenamiento explícito (QuickSort/MergeSort) y muestra la lista tabulada desde el menor al mayor costo.
