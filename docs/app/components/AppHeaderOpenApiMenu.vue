<script setup lang="ts">
const openApiItems = [[
  {
    label: 'Admin OpenAPI 文档',
    description: '查看管理端接口文档成品',
    icon: 'i-lucide-file-code-2',
    to: '/openapi/admin/openapi.html',
    target: '_blank',
    rel: 'noopener noreferrer',
  },
  {
    label: 'Admin OpenAPI YAML',
    description: '查看管理端 OpenAPI 原始契约文件',
    icon: 'i-lucide-file-text',
    to: '/openapi/admin/openapi.yaml',
    target: '_blank',
    rel: 'noopener noreferrer',
  },
]]
</script>

<template>
  <UDropdownMenu
    :items="openApiItems"
    :content="{ align: 'start', side: 'bottom', sideOffset: 8 }"
    :ui="{ content: 'min-w-72' }"
  >
    <UButton
      color="neutral"
      variant="ghost"
      trailing-icon="i-lucide-chevron-down"
      aria-label="OpenAPI 菜单"
    >
      OpenAPI
    </UButton>
  </UDropdownMenu>
</template>
