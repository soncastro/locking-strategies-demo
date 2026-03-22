import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080/locking-strategies-demo';
const PNEU_ID = '1';
const VIDA_AFERICAO_1 = '3';
const VIDA_AFERICAO_2 = '2';

export const options = {
  scenarios: {
    two_concurrent_pseudo_cenario_afericao_sem_inconsistencia: {
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
  const url1 = `${BASE_URL}/pseudo-cenario-afericao-pneu-sem-inconsistencia/${PNEU_ID}/${VIDA_AFERICAO_1}`;
  const url2 = `${BASE_URL}/pseudo-cenario-afericao-pneu-sem-inconsistencia/${PNEU_ID}/${VIDA_AFERICAO_2}`;

  const responses = http.batch([
    ['POST', url1, null, { tags: { request_name: 'pseudo_cenario_afericao_sem_inconsistencia_1' } }],
    ['POST', url2, null, { tags: { request_name: 'pseudo_cenario_afericao_sem_inconsistencia_2' } }],
  ]);

  responses.forEach((response, index) => {
    const requestName = `pseudo_cenario_afericao_sem_inconsistencia_${index + 1}`;
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
  });
}
