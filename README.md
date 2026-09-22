# LOLA — Sistema de Gestión de Logística y Envíos

Aplicación de escritorio para gestionar operaciones logísticas: despacho por
urgencia, rastreo de paquetes, red de rutas, asignación de repartos, jerarquía
de centros de distribución y reportes.

Las estructuras de datos y los algoritmos están **implementados desde cero**
(cola de prioridad, tabla hash, grafo ponderado, árbol, ordenamientos y
búsqueda) sin usar las colecciones equivalentes de `java.util`, como exige el
curso.

---

## Cómo ejecutar

Requisitos: **Java 21** y **Maven**.

```bash
make run        # compila y ejecuta desde el código fuente
make test       # ejecuta la suite de pruebas
make package    # genera el jar ejecutable
make run-jar    # compila el jar y lo ejecuta
make help       # lista todos los objetivos
```

La interfaz gráfica se abre en una ventana Swing con pestañas: **Resumen,
Despacho, Rastreo, Red, Repartos, Centros** y **Reportes**.

---

## Funcionalidades

| Pestaña | Qué hace |
| :--- | :--- |
| **Resumen** | Métricas generales: totales de paquetes, rutas, ingresos y pesos. |
| **Despacho** | Extrae el paquete más urgente y lo pasa a `EN_TRANSITO`. |
| **Rastreo** | Consulta un paquete por su número de guía en tiempo constante. |
| **Red** | Grafo de ciudades/rutas y camino más corto por tiempo, costo o distancia. |
| **Repartos** | Divide un lote entre vehículos según capacidad (divide y vencerás). |
| **Centros** | Jerarquía Nacional → Regional → Local y listado por nivel. |
| **Reportes** | Listados ordenados por costo, fecha límite o prioridad. |

---

## Módulos de negocio (`com.dev.modules`)

Son funciones estáticas sin estado: reciben datos y devuelven listas o mapas
nuevos, sin mutar la entrada.

- **`Deliveries`** — paquetes y despacho:
  `dispatchNext` (extrae el más urgente y lo pasa a `EN_TRANSITO`),
  `urgent`, `indexByWaybill` / `findByWaybill` (rastreo `O(1)`),
  `updateStatus`, `filterByStatus` / `filterByPriority`,
  `sortByCost` / `sortByDeadline` / `sortByPriority`, y agregados
  (`totalWeight`, `totalPriceInCents`, `countByPriority`).
- **`Routing`** — rutas y flota:
  `network` / `shortestPath` (grafo no ponderado),
  `weightedNetwork` / `weightedShortestPath` (Dijkstra por tiempo, costo o
  distancia), `sortByExpense` / `sortByDistance` / `sortByTime`,
  `cumulativeTime` / `cumulativeCost` / `cumulativeDistance` (recursivos),
  `partitionByState` y `partitionDeliveries` (divide y vencerás → `Assignment`).
- **`Centers`** — jerarquía de centros:
  `hierarchy` (árbol a partir de `parentId`), `nationalToLocal` (pre-orden) y
  `byLevel` (agrupados por `CenterLevel`).
- **`Analytics`** — métricas agregadas:
  `totalExpense`, `averageExpense`, `mostExpensiveRoute`, `cheapestRoute`,
  `totalDistance`, `revenueInCents`, `averagePriceInCents`,
  `heaviestPackage`, `countByStatus` y `revenueByRoute`.

---

## Estructuras y algoritmos propios (`com.dev.ds`)

| Clase | Uso |
| :--- | :--- |
| `PriorityQueue` / `BucketQueue` | Despacho por urgencia, incluso en `O(1)`. |
| `HashMap` | Índice guía → paquete, búsqueda promedio `O(1)`. |
| `WeightedGraph` | Red de rutas; Dijkstra para el camino de menor peso. |
| `Graph` | Red no ponderada; camino con menos paradas (BFS). |
| `Tree` / `BinaryTree` | Jerarquía de centros y organización de la flotilla. |
| `Sorting` | MergeSort (estable) y QuickSort. |
| `Search` | Búsqueda binaria sobre arreglos ordenados. |
| `Queue` / `Stack` | Recorridos auxiliares. |

Los cálculos acumulados de **tiempo, costo y distancia** sobre cadenas de rutas
usan **recursividad**, y el particionamiento de entregas usa **divide y
vencerás**.

---

## Arquitectura

```
src/main/java/com/dev/
├── lola/      App: punto de entrada (Swing)
├── ui/        Paneles, ventana principal y Store (datos en memoria)
├── modules/   Lógica de negocio: Deliveries, Routing, Centers, Analytics
├── ds/        Estructuras de datos y algoritmos propios
├── domain/    Modelos inmutables (Package, Route, Zone, Vehicle, Center…)
└── data/      Seed: datos deterministas de ejemplo
```

- **`Store`** mantiene los datos en memoria como listas inmutables.
- Los **módulos** son funciones estáticas sin estado que devuelven datos nuevos;
  nunca mutan la entrada.
- **`Seed`** genera datos reproducibles (semilla fija 42): 12 zonas, 14 rutas,
  12 centros, 6 vehículos y 60 paquetes.

---

## Pruebas

Las pruebas unitarias (JUnit 5) cubren `ds`, `modules`, `data`, `ui` y `lola`.
Ejecútalas con:

```bash
make test
```
