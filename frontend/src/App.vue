<script setup lang="ts">
import { RouterView, RouterLink, useRoute } from 'vue-router'
import { LayoutDashboard, Server, Settings, Activity } from 'lucide-vue-next'

const route = useRoute()
</script>

<template>
  <div class="flex h-screen bg-[#0f172a] text-[#f8fafc] font-sans selection:bg-indigo-500/30 overflow-hidden">
    <!-- Sidebar -->
    <aside class="w-64 flex-shrink-0 bg-[#1e293b] border-r border-[#334155] flex flex-col transition-all duration-300">
      <!-- Logo Area -->
      <div class="h-16 flex items-center px-6 border-b border-[#334155]">
        <div class="flex items-center gap-2 text-indigo-400">
          <Activity class="w-6 h-6" />
          <span class="font-bold text-lg tracking-wide text-white">Sistema de Monitoreo</span>
        </div>
      </div>

      <!-- Navigation -->
      <nav class="flex-1 py-6 px-4 space-y-2 overflow-y-auto">
        <RouterLink 
          to="/" 
          class="flex items-center gap-3 px-3 py-2.5 rounded-lg transition-colors font-medium"
          :class="route.path === '/' || route.path.startsWith('/dashboard') ? 'bg-indigo-500/10 text-indigo-400' : 'text-[#94a3b8] hover:bg-[#334155]/50 hover:text-white'"
        >
          <LayoutDashboard class="w-5 h-5" />
          Dashboard
        </RouterLink>

        <RouterLink 
          to="/sistemas" 
          class="flex items-center gap-3 px-3 py-2.5 rounded-lg transition-colors font-medium"
          :class="route.path.startsWith('/sistemas') ? 'bg-indigo-500/10 text-indigo-400' : 'text-[#94a3b8] hover:bg-[#334155]/50 hover:text-white'"
        >
          <Server class="w-5 h-5" />
          Sistemas
        </RouterLink>
      </nav>

      <!-- Bottom Actions -->
      <div class="p-4 border-t border-[#334155]">
        <div class="flex flex-col gap-2">
          <a href="http://localhost:3004" target="_blank" class="flex items-center justify-between px-3 py-2 text-sm text-[#94a3b8] hover:text-white hover:bg-[#334155]/50 rounded-lg transition-colors">
            <span class="flex items-center gap-2"><Settings class="w-4 h-4"/> Grafana</span>
            <span class="text-[10px] opacity-50">↗</span>
          </a>
          <a href="http://localhost:9090" target="_blank" class="flex items-center justify-between px-3 py-2 text-sm text-[#94a3b8] hover:text-white hover:bg-[#334155]/50 rounded-lg transition-colors">
            <span class="flex items-center gap-2"><Settings class="w-4 h-4"/> Prometheus</span>
            <span class="text-[10px] opacity-50">↗</span>
          </a>
        </div>
      </div>
    </aside>

    <!-- Main Content -->
    <main class="flex-1 flex flex-col h-screen overflow-hidden bg-[#0f172a]">
      <!-- Header -->
      <header class="h-16 flex items-center justify-between px-8 border-b border-[#334155]/60 bg-[#1e293b]/50 backdrop-blur-md sticky top-0 z-10">
        <h1 class="text-xl font-semibold">{{ route.path === '/' ? 'Dashboard' : 'Sistemas' }}</h1>
        
        <div class="flex items-center gap-4">
          <div class="w-8 h-8 rounded-full bg-gradient-to-tr from-indigo-500 to-purple-500 flex items-center justify-center text-sm font-bold shadow-lg shadow-indigo-500/20">
            DE
          </div>
        </div>
      </header>
      
      <!-- Page Content -->
      <div class="flex-1 overflow-y-auto p-8 relative">
        <RouterView />
      </div>
    </main>
  </div>
</template>