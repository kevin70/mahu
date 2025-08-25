import { defineConfig } from "@hey-api/openapi-ts";

export default defineConfig({
    input: "./dist/mahu-admin-openapi.yaml",
    output: "src/client",
});
