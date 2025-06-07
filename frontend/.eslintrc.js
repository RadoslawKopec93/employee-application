module.exports = {
    root: true,
    env: {
      node: true,
    },
    parser: "vue-eslint-parser",
    parserOptions: {
      parser: "@typescript-eslint/parser",
      ecmaVersion: 2020,
      sourceType: "module",
    },
    extends: [
      "plugin:vue/vue3-essential",
      "@vue/typescript/recommended"
    ],
    rules: {
        "vue/multi-word-component-names": "off",
    },
  };