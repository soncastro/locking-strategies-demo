import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080/locking-strategies-demo';
const ACCOUNT_ID = __ENV.ACCOUNT_ID || '1';
const DEBIT_AMOUNT_1 = __ENV.DEBIT_AMOUNT_1 || '80.00';
const DEBIT_AMOUNT_2 = __ENV.DEBIT_AMOUNT_2 || '40.00';

export const options = {
  scenarios: {
    two_concurrent_debits: {
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
  const debitUrl1 = `${BASE_URL}/debit-optimistic-locking/${ACCOUNT_ID}/${DEBIT_AMOUNT_1}`;
  const debitUrl2 = `${BASE_URL}/debit-optimistic-locking/${ACCOUNT_ID}/${DEBIT_AMOUNT_2}`;

  const responses = http.batch([
    ['POST', debitUrl1, null, { tags: { request_name: 'debit_optimistic_locking_1' } }],
    ['POST', debitUrl2, null, { tags: { request_name: 'debit_optimistic_locking_2' } }],
  ]);

  responses.forEach((response, index) => {
    const requestName = `debit_optimistic_locking_${index + 1}`;
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
      [`${requestName} returned 200 or 500`]:
        (res) => res.status === 200 || res.status === 500,
    });
  });
}
