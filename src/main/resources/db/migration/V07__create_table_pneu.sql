CREATE TABLE if NOT EXISTS public.pneu (
    
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,

    CONSTRAINT pk_pneu_id
        PRIMARY KEY (id)
);

INSERT INTO public.pneu (id)
OVERRIDING SYSTEM VALUE
VALUES (1);

SELECT setval(
    pg_get_serial_sequence('public.pneu', 'id'),
    (SELECT COALESCE(MAX(id), 1) FROM public.pneu),
    true
);

