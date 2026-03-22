INSERT INTO public.customer (id, name)
OVERRIDING SYSTEM VALUE
VALUES (1, 'Fulano');

SELECT setval(
    pg_get_serial_sequence('public.customer', 'id'),
    (SELECT COALESCE(MAX(id), 1) FROM public.customer),
    true
);
