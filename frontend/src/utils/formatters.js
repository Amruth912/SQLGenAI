export function formatDuration(ms) {
  if (ms < 1) return '< 1 ms';
  if (ms < 1000) return `${Math.round(ms)} ms`;
  return `${(ms / 1000).toFixed(2)} s`;
}

export function formatNumber(num) {
  if (num === null || num === undefined) return '0';
  return new Intl.NumberFormat().format(num);
}
