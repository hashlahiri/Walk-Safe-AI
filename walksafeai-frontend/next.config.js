/** @type {import('next').NextConfig} */
const nextConfig = {
  turbopack: {
    // Ensure the workspace root is this project (avoids multi-lockfile warnings)
    root: __dirname,
  },
};

module.exports = nextConfig;
