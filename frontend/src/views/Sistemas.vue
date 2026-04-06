<template>
  <div class="space-y-8 max-w-5xl mx-auto">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h2 class="text-2xl font-bold text-white tracking-wide">Gestión de Sistemas</h2>
        <p class="text-sm text-[#94a3b8] mt-1">Registra y administra los microservicios y aplicaciones a monitorear.</p>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      
      <!-- List Sidebar -->
      <div class="lg:col-span-1 space-y-4">
        <h3 class="text-lg font-semibold text-white/90">Sistemas Monitoreados</h3>
        <ul role="list" class="space-y-3">
          <li v-for="sistema in sistemas" :key="sistema.id" class="p-4 rounded-xl border border-[#334155] bg-[#1e293b]/50 backdrop-blur-sm transition-all hover:border-indigo-500/50 hover:bg-[#1e293b]">
            <div class="flex items-center justify-between mb-2">
              <span class="font-medium text-indigo-400 capitalize">{{ sistema.alias }}</span>
              <span class="inline-flex items-center px-2 py-0.5 rounded-full text-[10px] font-bold uppercase tracking-wider" :class="sistema.monitoreado ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20' : 'bg-red-500/10 text-red-400 border border-red-500/20'">
                {{ sistema.monitoreado ? 'Activo' : 'Inactivo' }}
              </span>
            </div>
            <div class="text-xs text-[#94a3b8] truncate font-mono">
              {{ sistema.host }}:{{ sistema.puerto }}{{ sistema.path }}
            </div>
            <div class="text-xs text-[#64748b] mt-1 flex items-center gap-1">
              <Clock class="w-3 h-3"/> Scrape: {{ sistema.intervalo }}
            </div>
          </li>
          <li v-if="sistemas.length === 0" class="p-4 rounded-xl border border-dashed border-[#334155] text-sm text-[#64748b] text-center">
            No hay sistemas registrados aún.
          </li>
        </ul>
      </div>

      <!-- Registration Form -->
      <div class="lg:col-span-2">
        <div class="bg-[#1e293b] border border-[#334155] rounded-2xl shadow-xl overflow-hidden">
          <div class="p-6 border-b border-[#334155]/60 bg-white/5">
            <h3 class="text-lg font-medium text-white flex items-center gap-2">
              <PlusCircle class="w-5 h-5 text-indigo-400"/>
              Registrar Nuevo Sistema
            </h3>
          </div>
          
          <div class="p-6">
            <form @submit.prevent="crearSistema" class="space-y-6">
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-6">
                <!-- Alias -->
                <div class="space-y-1">
                  <label class="text-xs font-semibold uppercase tracking-wider text-[#94a3b8]">Alias (Job Name)</label>
                  <input v-model="form.alias" required type="text" placeholder="ej. Api Pagos" class="w-full bg-[#0f172a] border border-[#334155] rounded-lg px-4 py-2.5 text-white placeholder-[#475569] focus:outline-none focus:ring-2 focus:ring-indigo-500/50 focus:border-indigo-500 transition-all font-medium" />
                </div>

                <!-- Host -->
                <div class="space-y-1">
                  <label class="text-xs font-semibold uppercase tracking-wider text-[#94a3b8]">Host</label>
                  <input v-model="form.host" required type="text" placeholder="ej. localhost" class="w-full bg-[#0f172a] border border-[#334155] rounded-lg px-4 py-2.5 text-white placeholder-[#475569] focus:outline-none focus:ring-2 focus:ring-indigo-500/50 focus:border-indigo-500 transition-all font-mono text-sm" />
                </div>

                <!-- Puerto -->
                <div class="space-y-1">
                  <label class="text-xs font-semibold uppercase tracking-wider text-[#94a3b8]">Puerto</label>
                  <input v-model.number="form.puerto" required type="number" class="w-full bg-[#0f172a] border border-[#334155] rounded-lg px-4 py-2.5 text-white placeholder-[#475569] focus:outline-none focus:ring-2 focus:ring-indigo-500/50 focus:border-indigo-500 transition-all font-mono text-sm" />
                </div>

                <!-- Path -->
                <div class="space-y-1">
                  <label class="text-xs font-semibold uppercase tracking-wider text-[#94a3b8]">Metrics Path</label>
                  <input v-model="form.path" required type="text" class="w-full bg-[#0f172a] border border-[#334155] rounded-lg px-4 py-2.5 text-white placeholder-[#475569] focus:outline-none focus:ring-2 focus:ring-indigo-500/50 focus:border-indigo-500 transition-all font-mono text-sm" />
                </div>
                
                <!-- Intervalo -->
                <div class="space-y-1">
                  <label class="text-xs font-semibold uppercase tracking-wider text-[#94a3b8]">Intervalo Scrape</label>
                  <input v-model="form.intervalo" required type="text" placeholder="15s" class="w-full bg-[#0f172a] border border-[#334155] rounded-lg px-4 py-2.5 text-white placeholder-[#475569] focus:outline-none focus:ring-2 focus:ring-indigo-500/50 focus:border-indigo-500 transition-all font-mono text-sm" />
                </div>
                
                <!-- ID Agente -->
                <div class="space-y-1">
                  <label class="text-xs font-semibold uppercase tracking-wider text-[#94a3b8]">ID Tipo Agente</label>
                  <input v-model.number="form.tipoAgenteId" required type="number" class="w-full bg-[#0f172a] border border-[#334155] rounded-lg px-4 py-2.5 text-white placeholder-[#475569] focus:outline-none focus:ring-2 focus:ring-indigo-500/50 focus:border-indigo-500 transition-all font-mono text-sm" />
                </div>
              </div>
              
              <div class="pt-4 flex items-center justify-between border-t border-[#334155]/60 mt-6">
                <div class="text-sm">
                  <span v-if="successMsg" class="text-emerald-400 flex items-center gap-2"><CheckCircle class="w-4 h-4"/> {{ successMsg }}</span>
                  <span v-if="errorMsg" class="text-red-400 flex items-center gap-2"><AlertCircle class="w-4 h-4"/> {{ errorMsg }}</span>
                </div>
                <button type="submit" class="bg-indigo-600 hover:bg-indigo-500 text-white px-6 py-2.5 rounded-lg shadow-lg shadow-indigo-600/20 font-medium transition-all active:scale-95 flex items-center gap-2">
                  <Save class="w-4 h-4"/>
                  Guardar Sistema
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { PlusCircle, Save, CheckCircle, AlertCircle, Clock } from 'lucide-vue-next'

const sistemas = ref<any[]>([])

const form = ref({
  alias: '',
  host: 'localhost',
  puerto: 8080,
  path: '/actuator/prometheus',
  intervalo: '15s',
  tipoAgenteId: 1
})

const successMsg = ref('')
const errorMsg = ref('')

const cargarSistemas = async () => {
  try {
    const res = await axios.get('/api/sistemas')
    sistemas.value = res.data
  } catch (err: any) {
    console.error('Error cargando sistemas', err)
  }
}

const crearSistema = async () => {
  successMsg.value = ''
  errorMsg.value = ''
  try {
    await axios.post('/api/sistemas', form.value)
    successMsg.value = 'Sistema registrado correctamente.'
    form.value.alias = ''
    cargarSistemas()
    setTimeout(() => { successMsg.value = '' }, 3000)
  } catch (err: any) {
    console.error('Error creando', err)
    errorMsg.value = err.response?.data?.message || 'Error al guardar el sistema.'
  }
}

onMounted(() => {
  cargarSistemas()
})
</script>