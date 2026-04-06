<template>
  <div class="space-y-6 max-w-[1400px] mx-auto h-full flex flex-col">
    <!-- Header area -->
    <div class="flex items-center justify-between">
      <div>
        <h2 class="text-2xl font-bold text-white tracking-wide">Métricas en Tiempo Real</h2>
        <p class="text-sm text-[#94a3b8] mt-1">
          Monitorización avanzada desde Prometheus.
          <span v-if="selectedSystem" class="text-indigo-400 font-medium">({{ selectedSystem.alias }})</span>
        </p>
      </div>

      <!-- Selector de Sistema -->
      <div v-if="sistemas.length > 0" class="flex items-center gap-3">
        <label class="text-sm font-medium text-[#94a3b8]">Sistema:</label>
        <select 
          v-model="selectedSystem" 
          @change="fetchMetrics" 
          class="bg-[#1e293b] border border-[#334155] text-white text-sm rounded-lg focus:ring-indigo-500 focus:border-indigo-500 block w-48 p-2.5 transition-all outline-none"
        >
          <option v-for="sys in sistemas" :key="sys.id" :value="sys">{{ sys.alias }}</option>
        </select>
        <button @click="fetchMetrics" class="p-2.5 rounded-lg bg-indigo-500/10 text-indigo-400 hover:bg-indigo-500/20 transition-all" title="Refrescar">
          <RefreshCw class="w-4 h-4" :class="{ 'animate-spin': loading }" />
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

    <!-- Dashboard Grid -->
    <div v-else-if="selectedSystem" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 flex-1 auto-rows-max">
      
      <!-- Stats Row (Span full width) -->
      <div class="col-span-full grid grid-cols-1 sm:grid-cols-3 gap-6 mb-2">
        
        <!-- Uptime Stats Card -->
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

        <!-- Live Threads Stats Card -->
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

        <!-- System CPU Global -->
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
          <apexchart v-if="!loading" type="area" height="100%" :options="cpuChartOptions" :series="cpuSeries"></apexchart>
          <div v-else class="absolute inset-0 flex items-center justify-center"><Loader2 class="w-8 h-8 text-indigo-500 animate-spin" /></div>
        </div>
      </div>

      <!-- Memory Used Chart (Gauge/Radial) -->
      <div class="col-span-1 border border-[#334155] rounded-2xl p-5 shadow-lg relative h-[340px] overflow-hidden flex flex-col bg-gradient-to-br from-[#1e293b] to-[#0f172a]">
        <h3 class="text-sm font-semibold text-[#94a3b8] uppercase tracking-wider mb-2 flex items-center justify-between">
          <span>Memoria JVM</span>
          <HardDrive class="w-4 h-4 text-indigo-400" />
        </h3>
        <div class="flex-1 min-h-0 w-full relative flex flex-col items-center justify-center">
          <apexchart v-if="!loading" type="radialBar" height="100%" :options="memoryChartOptions" :series="memorySeries"></apexchart>
          <div v-else class="absolute inset-0 flex items-center justify-center"><Loader2 class="w-8 h-8 text-indigo-500 animate-spin" /></div>
          
          <div class="text-center mt-[-30px] z-10" v-if="!loading">
            <span class="text-xs text-[#94a3b8] block">Usado: {{ (currentValues.memUsed / 1024 / 1024).toFixed(0) }} MB</span>
            <span class="text-xs text-[#94a3b8] block">Max: {{ (currentValues.memMax / 1024 / 1024).toFixed(0) }} MB</span>
          </div>
        </div>
      </div>

      <!-- HTTP Request Rate Chart -->
      <div class="col-span-1 md:col-span-2 lg:col-span-2 bg-[#1e293b] border border-[#334155] rounded-2xl p-5 shadow-lg relative h-[320px] flex flex-col">
        <h3 class="text-sm font-semibold text-[#94a3b8] uppercase tracking-wider mb-4 flex items-center justify-between">
          <span>Tasa Peticiones HTTP (Req/s)</span>
          <Globe class="w-4 h-4 text-sky-400" />
        </h3>
        <div class="flex-1 min-h-0 w-full relative">
          <apexchart v-if="!loading && httpSeries.length > 0 && httpSeries[0].data.length > 0" type="bar" height="100%" :options="httpChartOptions" :series="httpSeries"></apexchart>
          <div v-else-if="!loading" class="h-full flex items-center justify-center text-[#64748b] text-sm">Sin tráfico web reciente</div>
          <div v-else class="absolute inset-0 flex items-center justify-center"><Loader2 class="w-8 h-8 text-indigo-500 animate-spin" /></div>
        </div>
      </div>

      <!-- GC Pauses Area -->
      <div class="col-span-1 bg-[#1e293b] border border-[#334155] rounded-2xl p-5 shadow-lg relative h-[320px] flex flex-col">
        <h3 class="text-sm font-semibold text-[#94a3b8] uppercase tracking-wider mb-4 flex items-center justify-between">
          <span>Tiempos de Pausa GC</span>
          <Zap class="w-4 h-4 text-fuchsia-400" />
        </h3>
        <div class="flex-1 min-h-0 w-full relative">
          <apexchart v-if="!loading" type="area" height="100%" :options="gcChartOptions" :series="gcSeries"></apexchart>
          <div v-else class="absolute inset-0 flex items-center justify-center"><Loader2 class="w-8 h-8 text-indigo-500 animate-spin" /></div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, shallowRef } from 'vue'
import axios from 'axios'
import VueApexCharts from 'vue3-apexcharts'
import { Server, RefreshCw, Loader2, Cpu, HardDrive, Clock, Activity, Globe, Zap } from 'lucide-vue-next'

const apexchart = VueApexCharts

const sistemas = ref<any[]>([])
const selectedSystem = ref<any>(null)
const loading = ref(false)

// Realtime Interval
let refreshInterval: any = null

// Data series
const cpuSeries = ref<any[]>([])
const memorySeries = ref<number[]>([0])
const httpSeries = ref<any[]>([])
const gcSeries = ref<any[]>([])

const currentValues = ref({
  uptime: 0,
  threads: 0,
  cpu: 0,
  memUsed: 1,
  memMax: 1
})

// --- API FETCH LOGIC ---
const loadSistemas = async () => {
  try {
    const res = await axios.get('/api/sistemas')
    sistemas.value = res.data
    const activos = sistemas.value.filter(s => s.monitoreado)
    if (activos.length > 0) {
      selectedSystem.value = activos[0]
      fetchMetrics()
    } else if (sistemas.value.length > 0) {
      selectedSystem.value = sistemas.value[0]
      fetchMetrics()
    }
  } catch (err) {
    console.error('Error cargando sistemas:', err)
  }
}

const queryPrometheusRange = async (query: string, step = '15s') => {
  if (!selectedSystem.value) return []
  const end = Math.floor(Date.now() / 1000)
  const start = end - 3600 // Última hora
  
  // Format query substituting {job=sysAlias} where necessary
  const formattedQuery = query.replace('{job}', `{job="${selectedSystem.value.alias}"}`)
  
  try {
    const res = await axios.get('/prometheus/api/v1/query_range', {
      params: { query: formattedQuery, start, end, step }
    })
    if (res.data?.data?.result && res.data.data.result.length > 0) {
      return res.data.data.result[0].values.map((v: any[]) => [v[0] * 1000, parseFloat(v[1])])
    }
    return []
  } catch (err) {
    console.error('Prometheus range query failed', err)
    return []
  }
}

const queryPrometheusCurrent = async (query: string) => {
  if (!selectedSystem.value) return 0
  const formattedQuery = query.replace('{job}', `{job="${selectedSystem.value.alias}"}`)
  
  try {
    const res = await axios.get('/prometheus/api/v1/query', { params: { query: formattedQuery } })
    if (res.data?.data?.result && res.data.data.result.length > 0) {
      return parseFloat(res.data.data.result[0].value[1])
    }
    return 0
  } catch(e) {
    return 0
  }
}

const fetchMetrics = async () => {
  if (!selectedSystem.value) return
  loading.value = true
  
  try {
    // 1. Current Stats
    const [uptime, threads, procCpu, sysCpu, memUsed, memMax] = await Promise.all([
      queryPrometheusCurrent('process_uptime_seconds{job}'),
      queryPrometheusCurrent('jvm_threads_live_threads{job}'),
      queryPrometheusRange('process_cpu_usage{job}'),
      queryPrometheusRange('system_cpu_usage{job}'),
      queryPrometheusCurrent('jvm_memory_used_bytes{job}'),
      queryPrometheusCurrent('jvm_memory_max_bytes{job}')
    ])
    
    currentValues.value.uptime = uptime
    currentValues.value.threads = threads
    currentValues.value.memUsed = memUsed || 1
    currentValues.value.memMax = memMax || 1
    
    if (procCpu.length > 0) currentValues.value.cpu = procCpu[procCpu.length - 1][1]

    // Normalize CPU to prevent negative values (JVMs sometimes return -1 if unsupported)
    const normalizedSysCpu = sysCpu.map((v: number[]) => [v[0], Math.max(0, v[1])])
    const normalizedProcCpu = procCpu.map((v: number[]) => [v[0], Math.max(0, v[1])])

    cpuSeries.value = [
      { name: 'System CPU', data: normalizedSysCpu },
      { name: 'Process CPU', data: normalizedProcCpu }
    ]

    const usedPct = memMax > 0 ? (memUsed / memMax) * 100 : 0
    memorySeries.value = [usedPct]

    // HTTP Rate (Last hour)
    const httpRate = await queryPrometheusRange('sum(rate(http_server_requests_seconds_count{job}[5m]))', '30s')
    httpSeries.value = [{ name: 'Req/s', data: httpRate }]

    // GC Pause Time Rate
    const gcRate = await queryPrometheusRange('rate(jvm_gc_pause_seconds_sum{job}[5m])', '30s')
    gcSeries.value = [{ name: 'GC Pause (s)', data: gcRate }]

  } catch (error) {
    console.error("Error fetching metrics", error)
  } finally {
    loading.value = false
  }
}

const formatNumber = (num: number) => new Intl.NumberFormat('en-US').format(num)

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

const chartBase: ApexOptions = {
  chart: { toolbar: { show: false }, background: 'transparent' },
  theme: { mode: 'dark' },
  grid: { borderColor: '#334155', strokeDashArray: 4 },
  dataLabels: { enabled: false },
  tooltip: { theme: 'dark' },
}

const cpuChartOptions = shallowRef<ApexOptions>({
  ...chartBase,
  colors: ['#4f46e5', '#10b981'],
  stroke: { curve: 'smooth', width: 2 },
  xaxis: { type: 'datetime', labels: { style: { colors: '#94a3b8' }, datetimeFormatter: { hour: 'HH:mm' } }, axisBorder: { show: false }, axisTicks: { show: false } },
  yaxis: { min: 0, labels: { style: { colors: '#94a3b8' }, formatter: (v: number) => (v * 100).toFixed(1) + '%' } },
  fill: { type: 'gradient', gradient: { shadeIntensity: 1, opacityFrom: 0.4, opacityTo: 0.0, stops: [0, 100] } },
  legend: { position: 'top', horizontalAlign: 'right', labels: { colors: '#94a3b8' } }
})

const memoryChartOptions = shallowRef<ApexOptions>({
  ...chartBase,
  chart: { type: 'radialBar' },
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
  stroke: { width: 0 },
  xaxis: { type: 'datetime', labels: { style: { colors: '#94a3b8' } }, axisBorder: { show: false }, axisTicks: { show: false } },
  yaxis: { labels: { style: { colors: '#94a3b8' }, formatter: (v: number) => v.toFixed(2) } },
  grid: { borderColor: '#334155' },
  plotOptions: { bar: { borderRadius: 2, columnWidth: '60%' } }
})

const gcChartOptions = shallowRef<ApexOptions>({
  ...chartBase,
  colors: ['#d946ef'],
  stroke: { curve: 'stepline', width: 2 },
  fill: { type: 'solid', opacity: 0.2 },
  xaxis: { type: 'datetime', labels: { style: { colors: '#94a3b8' } }, axisBorder: { show: false }, axisTicks: { show: false } },
  yaxis: { labels: { style: { colors: '#94a3b8' }, formatter: (v: number) => v.toFixed(4) } },
})

onMounted(() => {
  loadSistemas()
  refreshInterval = setInterval(fetchMetrics, 15000)
})

onUnmounted(() => {
  if (refreshInterval) clearInterval(refreshInterval)
})
</script>