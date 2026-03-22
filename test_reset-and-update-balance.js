import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080/locking-strategies-demo';
const ACCOUNT_ID = '1';
const NEW_BALANCE = '100';

export const options = {
  scenarios: {
    single_reset_and_update_balance: {
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
  const requestName = 'reset_and_update_balance';
  const url = `${BASE_URL}/reset-and-update-balance/${ACCOUNT_ID}/${NEW_BALANCE}`;

  const response = http.post(url, null, {
    tags: { request_name: requestName },
  });

  console.log(
    [
      `\n[${requestName}]`,
      `status: ${response.status}`,
      `url: ${response.url}`,
      `body:`,
      formatBody(response.body),
    ].join('\n'),
  );

  check(response, {
    [`${requestName} returned 200 or 500`]: (res) => res.status === 200 || res.status === 500,
  });
}
