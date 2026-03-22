INSERT INTO public.account (id, customer_id, balance)
OVERRIDING SYSTEM VALUE
VALUES (1, 1, 0);

SELECT setval(
    pg_get_serial_sequence('public.account', 'id'),
    (SELECT COALESCE(MAX(id), 1) FROM public.account),
    true
);