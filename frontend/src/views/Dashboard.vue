<template>
  <div class="space-y-6 max-w-[1400px] mx-auto h-full flex flex-col">
    <!-- Header area -->
    <div class="flex items-center justify-between">
      <div>
        <h2 class="text-2xl font-bold text-white tracking-wide">Métricas en Tiempo Real</h2>
        <p class="text-sm text-[#94a3b8] mt-1">
          Monitorización avanzada desde Prometheus.
          <span v-if="selectedSystem" class="text-indigo-400 font-medium">({{ selectedSystem.alias }})</span>
          <span v-if="lastUpdate" class="text-[#64748b] ml-2 text-xs">· Actualizado: {{ lastUpdate }}</span>
        </p>
      </div>

      <!-- Selector de Sistema + Ventana de tiempo -->
      <div v-if="sistemas.length > 0" class="flex items-center gap-3">
        <label class="text-sm font-medium text-[#94a3b8]">Rango:</label>
        <select
          v-model.number="rangeSeconds"
          @change="onRangeChange"
          class="bg-[#1e293b] border border-[#334155] text-white text-sm rounded-lg focus:ring-indigo-500 focus:border-indigo-500 block w-28 p-2.5 transition-all outline-none"
        >
          <option :value="900">15 min</option>
          <option :value="1800">30 min</option>
          <option :value="3600">1 hora</option>
          <option :value="10800">3 horas</option>
          <option :value="21600">6 horas</option>
          <option :value="86400">24 horas</option>
        </select>

        <label class="text-sm font-medium text-[#94a3b8]">Sistema:</label>
        <select
          v-model="selectedSystem"
          @change="onSystemChange"
          class="bg-[#1e293b] border border-[#334155] text-white text-sm rounded-lg focus:ring-indigo-500 focus:border-indigo-500 block w-48 p-2.5 transition-all outline-none"
        >
          <option v-for="sys in sistemas" :key="sys.id" :value="sys">{{ sys.alias }}</option>
        </select>
        <button @click="fetchMetrics(true)" class="p-2.5 rounded-lg bg-indigo-500/10 text-indigo-400 hover:bg-indigo-500/20 transition-all" title="Refrescar">
          <RefreshCw class="w-4 h-4" :class="{ 'animate-spin': refreshing }" />
        </button>
      </div>
    </div>

    <!-- Empty State -->
    <div v-if="sistemas.length === 0" class="flex-1 flex flex-col items-center justify-center border-2 border-dashed border-[#334155] rounded-2xl bg-[#1e293b]/20">
      <Server class="w-12 h-12 text-[#475569] mb-4" />
      <h3 class="text-lg font-medium text-white mb-2">Sin sistemas configurados</h3>
      <p class="text-[#94a3b8] text-sm mb-4">Vaya a la pestaña de Sistemas para añadir un nuevo microservicio a monitorear.</p>
      <RouterLink to="/sistemas" class="bg-indigo-600 hover:bg-indigo-500 text-white px-5 py-2.5 rounded-lg font-medium transition-all">Ir a Sistemas</RouterLink>
    </div>

    <!-- Initial loading -->
    <div v-else-if="initialLoading" class="flex-1 flex items-center justify-center">
      <Loader2 class="w-10 h-10 text-indigo-500 animate-spin" />
    </div>

    <!-- Dashboard Grid -->
    <div v-else-if="selectedSystem" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 flex-1 auto-rows-max">

      <!-- ================= SPRING BOOT DASHBOARD ================= -->
      <template v-if="selectedSystem.tipoAgenteNombre !== 'dbpostgres'">

        <!-- Stats Row -->
        <div class="col-span-full grid grid-cols-1 sm:grid-cols-3 gap-6 mb-2">
          <!-- Uptime -->
          <div class="bg-[#1e293b] border border-[#334155] rounded-2xl p-5 shadow-lg flex items-center gap-4 relative overflow-hidden group">
            <div class="absolute -right-4 -top-4 w-24 h-24 bg-emerald-500/10 rounded-full blur-2xl group-hover:bg-emerald-500/20 transition-all"></div>
            <div class="p-3 bg-gradient-to-br from-emerald-400/20 to-emerald-600/20 rounded-xl border border-emerald-500/20">
              <Clock class="w-6 h-6 text-emerald-400" />
            </div>
            <div>
              <p class="text-sm font-medium text-[#94a3b8]">Uptime</p>
              <h3 class="text-2xl font-bold text-white mt-1" :title="currentValues.uptime + ' segundos'">{{ formatUptime(currentValues.uptime) }}</h3>
            </div>
          </div>
          <!-- Threads -->
          <div class="bg-[#1e293b] border border-[#334155] rounded-2xl p-5 shadow-lg flex items-center gap-4 relative overflow-hidden group">
            <div class="absolute -right-4 -top-4 w-24 h-24 bg-indigo-500/10 rounded-full blur-2xl group-hover:bg-indigo-500/20 transition-all"></div>
            <div class="p-3 bg-gradient-to-br from-indigo-400/20 to-indigo-600/20 rounded-xl border border-indigo-500/20">
              <Activity class="w-6 h-6 text-indigo-400" />
            </div>
            <div>
              <p class="text-sm font-medium text-[#94a3b8]">Hilos JVM Activos</p>
              <h3 class="text-2xl font-bold text-white mt-1">{{ formatNumber(currentValues.threads) }}</h3>
            </div>
          </div>
          <!-- Process CPU -->
          <div class="bg-[#1e293b] border border-[#334155] rounded-2xl p-5 shadow-lg flex items-center gap-4 relative overflow-hidden group">
            <div class="absolute -right-4 -top-4 w-24 h-24 bg-amber-500/10 rounded-full blur-2xl group-hover:bg-amber-500/20 transition-all"></div>
            <div class="p-3 bg-gradient-to-br from-amber-400/20 to-amber-600/20 rounded-xl border border-amber-500/20">
              <Cpu class="w-6 h-6 text-amber-400" />
            </div>
            <div>
              <p class="text-sm font-medium text-[#94a3b8]">Proceso CPU Actual</p>
              <h3 class="text-2xl font-bold text-white mt-1">
                {{ currentValues.cpu < 0 ? 'N/A' : (currentValues.cpu * 100).toFixed(2) + ' %' }}
              </h3>
            </div>
          </div>
        </div>

        <!-- CPU Chart -->
        <div class="col-span-1 md:col-span-2 lg:col-span-2 bg-[#1e293b] border border-[#334155] rounded-2xl p-5 shadow-lg relative h-[340px] flex flex-col">
          <h3 class="text-sm font-semibold text-[#94a3b8] uppercase tracking-wider mb-4 flex items-center justify-between">
            <span>Uso de CPU (Proceso vs SO)</span>
            <Cpu class="w-4 h-4 text-emerald-400" />
          </h3>
          <div class="flex-1 min-h-0 w-full relative">
            <apexchart type="area" height="100%" :options="cpuChartOptions" :series="cpuSeries"></apexchart>
            <div v-if="cpuSeries[0] && cpuSeries[0].data.length === 0" class="absolute inset-0 flex items-center justify-center text-[#64748b] text-sm pointer-events-none">Sin datos en el rango seleccionado</div>
          </div>
        </div>

        <!-- Memory Radial -->
        <div class="col-span-1 border border-[#334155] rounded-2xl p-5 shadow-lg relative h-[340px] overflow-hidden flex flex-col bg-gradient-to-br from-[#1e293b] to-[#0f172a]">
          <h3 class="text-sm font-semibold text-[#94a3b8] uppercase tracking-wider mb-2 flex items-center justify-between">
            <span>Memoria JVM (Heap)</span>
            <HardDrive class="w-4 h-4 text-indigo-400" />
          </h3>
          <div class="flex-1 min-h-0 w-full relative flex flex-col items-center justify-center">
            <apexchart type="radialBar" height="100%" :options="memoryChartOptions" :series="memorySeries"></apexchart>
            <div class="text-center mt-[-30px] z-10">
              <span class="text-xs text-[#94a3b8] block">Usado: {{ (currentValues.memUsed / 1024 / 1024).toFixed(0) }} MB</span>
              <span class="text-xs text-[#94a3b8] block">Max: {{ (currentValues.memMax / 1024 / 1024).toFixed(0) }} MB</span>
            </div>
          </div>
        </div>

        <!-- HTTP Requests -->
        <div class="col-span-1 md:col-span-2 lg:col-span-2 bg-[#1e293b] border border-[#334155] rounded-2xl p-5 shadow-lg relative h-[320px] flex flex-col">
          <h3 class="text-sm font-semibold text-[#94a3b8] uppercase tracking-wider mb-4 flex items-center justify-between">
            <span>Tasa Peticiones HTTP (Req/s)</span>
            <Globe class="w-4 h-4 text-sky-400" />
          </h3>
          <div class="flex-1 min-h-0 w-full relative">
            <apexchart type="area" height="100%" :options="httpChartOptions" :series="httpSeries"></apexchart>
            <div v-if="httpSeries[0] && httpSeries[0].data.length === 0" class="absolute inset-0 flex items-center justify-center text-[#64748b] text-sm pointer-events-none">Sin tráfico web reciente</div>
          </div>
        </div>

        <!-- GC Pauses -->
        <div class="col-span-1 bg-[#1e293b] border border-[#334155] rounded-2xl p-5 shadow-lg relative h-[320px] flex flex-col">
          <h3 class="text-sm font-semibold text-[#94a3b8] uppercase tracking-wider mb-4 flex items-center justify-between">
            <span>Tiempos de Pausa GC</span>
            <Zap class="w-4 h-4 text-fuchsia-400" />
          </h3>
          <div class="flex-1 min-h-0 w-full relative">
            <apexchart type="area" height="100%" :options="gcChartOptions" :series="gcSeries"></apexchart>
            <div v-if="gcSeries[0] && gcSeries[0].data.length === 0" class="absolute inset-0 flex items-center justify-center text-[#64748b] text-sm pointer-events-none">Sin eventos de GC</div>
          </div>
        </div>
      </template>

      <!-- ================= POSTGRES DASHBOARD ================= -->
      <template v-else-if="selectedSystem.tipoAgenteNombre === 'dbpostgres'">
        <!-- Stats Row -->
        <div class="col-span-full grid grid-cols-1 sm:grid-cols-3 gap-6 mb-2">
          <!-- DB Size -->
          <div class="bg-[#1e293b] border border-[#334155] rounded-2xl p-5 shadow-lg flex items-center gap-4 relative overflow-hidden group">
            <div class="absolute -right-4 -top-4 w-24 h-24 bg-sky-500/10 rounded-full blur-2xl group-hover:bg-sky-500/20 transition-all"></div>
            <div class="p-3 bg-gradient-to-br from-sky-400/20 to-sky-600/20 rounded-xl border border-sky-500/20">
              <Database class="w-6 h-6 text-sky-400" />
            </div>
            <div>
              <p class="text-sm font-medium text-[#94a3b8]">Tamaño Base de Datos</p>
              <h3 class="text-2xl font-bold text-white mt-1">{{ formatBytes(currentValues.dbSize) }}</h3>
            </div>
          </div>
          <!-- DB Connections -->
          <div class="bg-[#1e293b] border border-[#334155] rounded-2xl p-5 shadow-lg flex items-center gap-4 relative overflow-hidden group">
            <div class="absolute -right-4 -top-4 w-24 h-24 bg-purple-500/10 rounded-full blur-2xl group-hover:bg-purple-500/20 transition-all"></div>
            <div class="p-3 bg-gradient-to-br from-purple-400/20 to-purple-600/20 rounded-xl border border-purple-500/20">
              <Users class="w-6 h-6 text-purple-400" />
            </div>
            <div>
              <p class="text-sm font-medium text-[#94a3b8]">Conexiones Activas</p>
              <h3 class="text-2xl font-bold text-white mt-1">{{ formatNumber(currentValues.dbConnections) }}</h3>
            </div>
          </div>
          <!-- Cache Hit Ratio -->
          <div class="bg-[#1e293b] border border-[#334155] rounded-2xl p-5 shadow-lg flex items-center gap-4 relative overflow-hidden group">
            <div class="absolute -right-4 -top-4 w-24 h-24 bg-emerald-500/10 rounded-full blur-2xl group-hover:bg-emerald-500/20 transition-all"></div>
            <div class="p-3 bg-gradient-to-br from-emerald-400/20 to-emerald-600/20 rounded-xl border border-emerald-500/20">
              <Zap class="w-6 h-6 text-emerald-400" />
            </div>
            <div>
              <p class="text-sm font-medium text-[#94a3b8]">Cache Hit Ratio</p>
              <h3 class="text-2xl font-bold mt-1" :class="currentValues.dbCacheRatio >= 95 ? 'text-emerald-400' : 'text-amber-400'">
                {{ currentValues.dbCacheRatio > 0 ? currentValues.dbCacheRatio.toFixed(1) + '%' : 'N/A' }}
              </h3>
            </div>
          </div>
        </div>

        <!-- Rows Chart -->
        <div class="col-span-1 md:col-span-2 lg:col-span-3 bg-[#1e293b] border border-[#334155] rounded-2xl p-5 shadow-lg relative h-[340px] flex flex-col">
          <h3 class="text-sm font-semibold text-[#94a3b8] uppercase tracking-wider mb-4 flex items-center justify-between">
            <span>Tasa de Filas (Rows/s)</span>
            <List class="w-4 h-4 text-sky-400" />
          </h3>
          <div class="flex-1 min-h-0 w-full relative">
            <apexchart type="area" height="100%" :options="dbChartOptions" :series="dbRowsSeries"></apexchart>
            <div v-if="dbRowsHasNoData" class="absolute inset-0 flex items-center justify-center text-[#64748b] text-sm pointer-events-none">Sin actividad de filas en el rango seleccionado</div>
          </div>
        </div>

        <!-- Transactions + Blocks Chart -->
        <div class="col-span-1 md:col-span-2 lg:col-span-3 bg-[#1e293b] border border-[#334155] rounded-2xl p-5 shadow-lg relative h-[340px] flex flex-col">
          <h3 class="text-sm font-semibold text-[#94a3b8] uppercase tracking-wider mb-4 flex items-center justify-between">
            <span>Transacciones (Tx/s) y Bloques (Hits/Reads)</span>
            <Activity class="w-4 h-4 text-emerald-400" />
          </h3>
          <div class="flex-1 min-h-0 w-full relative">
            <apexchart type="area" height="100%" :options="dbChartOptions" :series="dbTxSeries"></apexchart>
            <div v-if="dbTxHasNoData" class="absolute inset-0 flex items-center justify-center text-[#64748b] text-sm pointer-events-none">Sin actividad transaccional en el rango seleccionado</div>
          </div>
        </div>
      </template>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, shallowRef } from 'vue'
import axios from 'axios'
import VueApexCharts from 'vue3-apexcharts'
import { Server, RefreshCw, Loader2, Cpu, HardDrive, Clock, Activity, Globe, Zap, Database, Users, List } from 'lucide-vue-next'

const apexchart = VueApexCharts

const sistemas = ref<any[]>([])
const selectedSystem = ref<any>(null)
const initialLoading = ref(true)
const refreshing = ref(false)
const lastUpdate = ref<string>('')
const rangeSeconds = ref<number>(3600) // Default 1 hora

// Realtime Interval
let refreshInterval: any = null

// Spring Boot Series — initialised with empty datasets to keep charts mounted
const cpuSeries = ref<any[]>([
  { name: 'System CPU', data: [] },
  { name: 'Process CPU', data: [] }
])
const memorySeries = ref<number[]>([0])
const httpSeries = ref<any[]>([{ name: 'Req/s', data: [] }])
const gcSeries = ref<any[]>([{ name: 'GC Pause (s)', data: [] }])

// Postgres Series
const dbRowsSeries = ref<any[]>([
  { name: 'Filas Leídas/s', data: [] },
  { name: 'Filas Insertadas/s', data: [] },
  { name: 'Filas Actualizadas/s', data: [] },
  { name: 'Filas Eliminadas/s', data: [] }
])
const dbTxSeries = ref<any[]>([
  { name: 'Transacciones/s', data: [] },
  { name: 'Bloques Hits/s', data: [] },
  { name: 'Bloques Reads/s', data: [] }
])

const dbRowsHasNoData = computed(() => dbRowsSeries.value.every((s: any) => !s.data || s.data.length === 0))
const dbTxHasNoData = computed(() => dbTxSeries.value.every((s: any) => !s.data || s.data.length === 0))

const currentValues = ref({
  uptime: 0,
  threads: 0,
  cpu: 0,
  memUsed: 1,
  memMax: 1,
  dbSize: 0,
  dbConnections: 0,
  dbCacheRatio: 0
})

// Adaptive step according to range: target ~200 points so the chart is both smooth and readable.
const pickStep = (): string => {
  const target = 200
  const raw = Math.max(15, Math.round(rangeSeconds.value / target))
  // Snap to common Prometheus-friendly buckets
  const buckets = [15, 30, 60, 120, 300, 600, 900, 1800, 3600]
  const chosen = buckets.find(b => b >= raw) ?? 3600
  return `${chosen}s`
}

// Rate window scales with the step so rate() has at least 2 samples per bucket
const pickRateWindow = (): string => {
  const stepSec = parseInt(pickStep(), 10)
  const windowSec = Math.max(60, stepSec * 4)
  if (windowSec >= 3600) return `${Math.round(windowSec / 3600)}h`
  if (windowSec >= 60) return `${Math.round(windowSec / 60)}m`
  return `${windowSec}s`
}

// --- API FETCH LOGIC ---
const loadSistemas = async () => {
  try {
    const res = await axios.get('/api/sistemas')
    sistemas.value = res.data
    const activos = sistemas.value.filter(s => s.monitoreado)
    if (activos.length > 0) {
      selectedSystem.value = activos[0]
      await fetchMetrics(true)
    } else if (sistemas.value.length > 0) {
      selectedSystem.value = sistemas.value[0]
      await fetchMetrics(true)
    } else {
      initialLoading.value = false
    }
  } catch (err) {
    console.error('Error cargando sistemas:', err)
    initialLoading.value = false
  }
}

const getJobTarget = () => {
  if (!selectedSystem.value) return ''
  return selectedSystem.value.tipoAgenteNombre === 'dbpostgres'
    ? `postgres_${selectedSystem.value.alias}`
    : selectedSystem.value.alias
}

// Escape special chars for PromQL label values
const escapeJob = (job: string) => job.replace(/\\/g, '\\\\').replace(/"/g, '\\"')

const queryPrometheusRange = async (query: string, step?: string): Promise<[number, number][]> => {
  if (!selectedSystem.value) return []
  const actualStep = step ?? pickStep()
  const end = Math.floor(Date.now() / 1000)
  const start = end - rangeSeconds.value

  const job = escapeJob(getJobTarget())
  const formattedQuery = query.replace(/JOB_FILTER/g, `job="${job}"`)

  try {
    const res = await axios.get('/prometheus/api/v1/query_range', {
      params: { query: formattedQuery, start, end, step: actualStep },
      timeout: 15000
    })
    if (res.data?.data?.result && res.data.data.result.length > 0) {
      // Sum across any dimensional splits (e.g. jvm_memory_used_bytes splits by area/id)
      const firstSeries = res.data.data.result[0]
      return firstSeries.values.map((v: any[]) => [v[0] * 1000, Number.isFinite(parseFloat(v[1])) ? parseFloat(v[1]) : 0])
    }
    return []
  } catch (err) {
    console.error('Prometheus range query failed', formattedQuery, err)
    return []
  }
}

const queryPrometheusCurrent = async (query: string): Promise<number> => {
  if (!selectedSystem.value) return 0
  const job = escapeJob(getJobTarget())
  const formattedQuery = query.replace(/JOB_FILTER/g, `job="${job}"`)

  try {
    const res = await axios.get('/prometheus/api/v1/query', {
      params: { query: formattedQuery },
      timeout: 10000
    })
    if (res.data?.data?.result && res.data.data.result.length > 0) {
      const val = parseFloat(res.data.data.result[0].value[1])
      return Number.isFinite(val) ? val : 0
    }
    return 0
  } catch(e) {
    return 0
  }
}

const onSystemChange = () => {
  // Reset series for the new system context so we don't mix data visually
  resetSeries()
  initialLoading.value = true
  fetchMetrics(true)
}

const onRangeChange = () => {
  fetchMetrics(true)
}

const resetSeries = () => {
  cpuSeries.value = [
    { name: 'System CPU', data: [] },
    { name: 'Process CPU', data: [] }
  ]
  memorySeries.value = [0]
  httpSeries.value = [{ name: 'Req/s', data: [] }]
  gcSeries.value = [{ name: 'GC Pause (s)', data: [] }]
  dbRowsSeries.value = [
    { name: 'Filas Leídas/s', data: [] },
    { name: 'Filas Insertadas/s', data: [] },
    { name: 'Filas Actualizadas/s', data: [] },
    { name: 'Filas Eliminadas/s', data: [] }
  ]
  dbTxSeries.value = [
    { name: 'Transacciones/s', data: [] },
    { name: 'Bloques Hits/s', data: [] },
    { name: 'Bloques Reads/s', data: [] }
  ]
}

const fetchMetrics = async (_force = false) => {
  if (!selectedSystem.value) return
  refreshing.value = true

  try {
    if (selectedSystem.value.tipoAgenteNombre === 'dbpostgres') {
      // POSTGRES METRICS — names aligned to Micrometer PostgreSQLDatabaseMetrics output
      const rateWin = pickRateWindow()
      const [size, connections, fetched, inserted, updated, deleted, txs, blkHitsRate, blkReadsRate, hits, reads] = await Promise.all([
        // postgres.size → postgres_size_bytes (fallback to postgres_size for older Micrometer)
        queryPrometheusCurrent('(postgres_size_bytes{JOB_FILTER} or postgres_size{JOB_FILTER})'),
        queryPrometheusCurrent('postgres_connections{JOB_FILTER}'),
        queryPrometheusRange(`rate(postgres_rows_fetched_total{JOB_FILTER}[${rateWin}])`),
        queryPrometheusRange(`rate(postgres_rows_inserted_total{JOB_FILTER}[${rateWin}])`),
        queryPrometheusRange(`rate(postgres_rows_updated_total{JOB_FILTER}[${rateWin}])`),
        queryPrometheusRange(`rate(postgres_rows_deleted_total{JOB_FILTER}[${rateWin}])`),
        // postgres_transactions_total = commits + rollbacks combined (Micrometer does not expose the split)
        queryPrometheusRange(`rate(postgres_transactions_total{JOB_FILTER}[${rateWin}])`),
        queryPrometheusRange(`rate(postgres_blocks_hits_total{JOB_FILTER}[${rateWin}])`),
        queryPrometheusRange(`rate(postgres_blocks_reads_total{JOB_FILTER}[${rateWin}])`),
        queryPrometheusCurrent('postgres_blocks_hits_total{JOB_FILTER}'),
        queryPrometheusCurrent('postgres_blocks_reads_total{JOB_FILTER}')
      ])

      currentValues.value.dbSize = size
      currentValues.value.dbConnections = connections

      const totalBlocks = hits + reads
      currentValues.value.dbCacheRatio = totalBlocks > 0 ? (hits / totalBlocks) * 100 : 0

      dbRowsSeries.value = [
        { name: 'Filas Leídas/s', data: fetched },
        { name: 'Filas Insertadas/s', data: inserted },
        { name: 'Filas Actualizadas/s', data: updated },
        { name: 'Filas Eliminadas/s', data: deleted }
      ]

      dbTxSeries.value = [
        { name: 'Transacciones/s', data: txs },
        { name: 'Bloques Hits/s', data: blkHitsRate },
        { name: 'Bloques Reads/s', data: blkReadsRate }
      ]

    } else {
      // SPRING BOOT METRICS (Default)
      const rateWin = pickRateWindow()
      const [uptime, threads, procCpu, sysCpu, memUsedSeries, memMaxSeries, httpRate, gcRate] = await Promise.all([
        queryPrometheusCurrent('process_uptime_seconds{JOB_FILTER}'),
        queryPrometheusCurrent('sum(jvm_threads_live_threads{JOB_FILTER})'),
        queryPrometheusRange('process_cpu_usage{JOB_FILTER}'),
        queryPrometheusRange('system_cpu_usage{JOB_FILTER}'),
        // Sum across memory areas/pools to get the total heap+non-heap
        queryPrometheusRange('sum(jvm_memory_used_bytes{JOB_FILTER, area="heap"})'),
        queryPrometheusRange('sum(jvm_memory_max_bytes{JOB_FILTER, area="heap"})'),
        queryPrometheusRange(`sum(rate(http_server_requests_seconds_count{JOB_FILTER}[${rateWin}]))`),
        queryPrometheusRange(`sum(rate(jvm_gc_pause_seconds_sum{JOB_FILTER}[${rateWin}]))`)
      ])

      currentValues.value.uptime = uptime
      currentValues.value.threads = threads

      const lastMemUsed = memUsedSeries.length > 0 ? memUsedSeries[memUsedSeries.length - 1][1] : 0
      const lastMemMax = memMaxSeries.length > 0 ? memMaxSeries[memMaxSeries.length - 1][1] : 0
      currentValues.value.memUsed = lastMemUsed || 1
      currentValues.value.memMax = lastMemMax > 0 ? lastMemMax : 1

      if (procCpu.length > 0) currentValues.value.cpu = procCpu[procCpu.length - 1][1]

      // Normalize CPU to prevent negative values (JVMs sometimes return -1 if unsupported)
      const normalizedSysCpu = sysCpu.map((v: number[]) => [v[0], Math.max(0, v[1])]) as [number, number][]
      const normalizedProcCpu = procCpu.map((v: number[]) => [v[0], Math.max(0, v[1])]) as [number, number][]

      cpuSeries.value = [
        { name: 'System CPU', data: normalizedSysCpu },
        { name: 'Process CPU', data: normalizedProcCpu }
      ]

      const usedPct = lastMemMax > 0 ? (lastMemUsed / lastMemMax) * 100 : 0
      memorySeries.value = [Number(usedPct.toFixed(1))]

      httpSeries.value = [{ name: 'Req/s', data: httpRate }]
      gcSeries.value = [{ name: 'GC Pause (s)', data: gcRate }]
    }

    lastUpdate.value = new Date().toLocaleTimeString()
  } catch (error) {
    console.error("Error fetching metrics", error)
  } finally {
    refreshing.value = false
    initialLoading.value = false
  }
}

const formatNumber = (num: number) => new Intl.NumberFormat('en-US').format(num)

const formatBytes = (bytes: number) => {
  if (!bytes || bytes <= 0) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.min(units.length - 1, Math.floor(Math.log(bytes) / Math.log(1024)))
  return (bytes / Math.pow(1024, i)).toFixed(2) + ' ' + units[i]
}

const formatUptime = (seconds: number) => {
  if (!seconds || seconds < 0) return '0s'
  const d = Math.floor(seconds / (3600*24))
  const h = Math.floor(seconds % (3600*24) / 3600)
  const m = Math.floor(seconds % 3600 / 60)
  const s = Math.floor(seconds % 60)

  if (d > 0) return `${d}d ${h}h`
  if (h > 0) return `${h}h ${m}m`
  return `${m}m ${s}s`
}

// --- CHART OPTIONS (Minimal Dark Theme) ---
import type { ApexOptions } from 'apexcharts'

// Smooth in-place animation on data update instead of full re-render
const chartBase: ApexOptions = {
  chart: {
    toolbar: { show: false },
    background: 'transparent',
    animations: {
      enabled: true,
      speed: 400,
      dynamicAnimation: { enabled: true, speed: 350 }
    },
    zoom: { enabled: false }
  },
  theme: { mode: 'dark' },
  grid: { borderColor: '#334155', strokeDashArray: 4 },
  dataLabels: { enabled: false },
  tooltip: { theme: 'dark', x: { format: 'dd MMM HH:mm:ss' } },
  noData: { text: 'Sin datos…', style: { color: '#64748b' } }
}

const timeAxis = {
  type: 'datetime' as const,
  labels: {
    style: { colors: '#94a3b8' },
    datetimeUTC: false,
    datetimeFormatter: {
      year: 'yyyy',
      month: "MMM 'yy",
      day: 'dd MMM',
      hour: 'HH:mm',
      minute: 'HH:mm:ss'
    }
  },
  axisBorder: { show: false },
  axisTicks: { show: false }
}

const cpuChartOptions = shallowRef<ApexOptions>({
  ...chartBase,
  colors: ['#4f46e5', '#10b981'],
  stroke: { curve: 'smooth', width: 2 },
  xaxis: timeAxis,
  yaxis: { min: 0, max: 1, labels: { style: { colors: '#94a3b8' }, formatter: (v: number) => (v * 100).toFixed(1) + '%' } },
  fill: { type: 'gradient', gradient: { shadeIntensity: 1, opacityFrom: 0.4, opacityTo: 0.0, stops: [0, 100] } },
  legend: { position: 'top', horizontalAlign: 'right', labels: { colors: '#94a3b8' } }
})

const dbChartOptions = shallowRef<ApexOptions>({
  ...chartBase,
  colors: ['#0ea5e9', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#d946ef'],
  stroke: { curve: 'smooth', width: 2 },
  xaxis: timeAxis,
  yaxis: { min: 0, labels: { style: { colors: '#94a3b8' }, formatter: (v: number) => v.toFixed(2) } },
  fill: { type: 'gradient', gradient: { shadeIntensity: 1, opacityFrom: 0.3, opacityTo: 0.0, stops: [0, 100] } },
  legend: { position: 'top', horizontalAlign: 'right', labels: { colors: '#94a3b8' } }
})

const memoryChartOptions = shallowRef<ApexOptions>({
  ...chartBase,
  chart: { type: 'radialBar', animations: chartBase.chart?.animations },
  plotOptions: {
    radialBar: {
      hollow: { size: '65%' },
      track: { background: '#334155' },
      dataLabels: {
        name: { show: false },
        value: { color: '#f8fafc', fontSize: '24px', fontWeight: 600, formatter: (val: number) => val.toFixed(1) + '%' }
      }
    }
  },
  colors: ['#3b82f6'],
  stroke: { lineCap: 'round' }
})

const httpChartOptions = shallowRef<ApexOptions>({
  ...chartBase,
  colors: ['#0ea5e9'],
  stroke: { curve: 'smooth', width: 2 },
  xaxis: timeAxis,
  yaxis: { min: 0, labels: { style: { colors: '#94a3b8' }, formatter: (v: number) => v.toFixed(2) } },
  fill: { type: 'gradient', gradient: { shadeIntensity: 1, opacityFrom: 0.4, opacityTo: 0.0, stops: [0, 100] } }
})

const gcChartOptions = shallowRef<ApexOptions>({
  ...chartBase,
  colors: ['#d946ef'],
  stroke: { curve: 'stepline', width: 2 },
  fill: { type: 'solid', opacity: 0.2 },
  xaxis: timeAxis,
  yaxis: { min: 0, labels: { style: { colors: '#94a3b8' }, formatter: (v: number) => v.toFixed(4) } },
})

onMounted(() => {
  loadSistemas()
  refreshInterval = setInterval(() => fetchMetrics(false), 15000)
})

onUnmounted(() => {
  if (refreshInterval) clearInterval(refreshInterval)
})
</script>
