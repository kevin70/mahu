<template>
  <UDropdownMenu
    v-slot="{ open }"
    :modal="false"
    :items="[
      {
        label: 'Docs',
        to: 'https://docs-template.nuxt.dev/',
        color: 'primary',
        checked: true,
        type: 'checkbox',
      },
    ]"
    :content="{ align: 'start' }"
    :ui="{ content: 'min-w-fit' }"
    size="xs"
  >
    <UButton
      label="Docs"
      variant="subtle"
      trailing-icon="i-lucide-chevron-down"
      size="xs"
      class="-mb-[6px] font-semibold rounded-full truncate"
      :class="[open && 'bg-primary/15']"
      :ui="{
        trailingIcon: [
          'transition-transform duration-200',
          open ? 'rotate-180' : undefined,
        ]
          .filter(Boolean)
          .join(' '),
      }"
    />
  </UDropdownMenu>
</template>
