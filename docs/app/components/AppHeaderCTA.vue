<script setup lang="ts">
const route = useRoute()
</script>

<template>
  <div class="hidden lg:flex items-center gap-2">
    <UButton
      v-if="route.path === '/'"
      to="/getting-started/installation"
      color="neutral"
      size="sm"
    >
      Get Started
    </UButton>

    <AppHeaderOpenApiMenu />
  </div>
</template>
