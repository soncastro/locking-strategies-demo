import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080/locking-strategies-demo';

export const options = {
  scenarios: {
    single_reset_request: {
      executor: 'shared-iterations',
      vus: 1,
      iterations: 1,
      maxDuration: '30s',
    },
  },
};

function sanitizePayload(value) {
  if (Array.isArray(value)) {
    return value.map(sanitizePayload);
  }

  if (value && typeof value === 'object') {
    return Object.fromEntries(
      Object.entries(value)
        .filter(([key]) => !['trace', 'stackTrace'].includes(key))
        .map(([key, entryValue]) => [key, sanitizePayload(entryValue)]),
    );
  }

  return value;
}

function formatBody(body) {
  if (!body) {
    return '<empty>';
  }

  try {
    return JSON.stringify(sanitizePayload(JSON.parse(body)), null, 2);
  } catch (_) {
    return body;
  }
}

export default function () {
  const resetUrl = `${BASE_URL}/reset`;
  const response = http.post(resetUrl, null, { tags: { request_name: 'reset' } });

  console.log(
    [
      '\n[reset]',
      `status: ${response.status}`,
      `url: ${response.url}`,
      'body:',
      formatBody(response.body),
    ].join('\n'),
  );

  check(response, {
    'reset returned 200': (res) => res.status === 200,
  });
}
