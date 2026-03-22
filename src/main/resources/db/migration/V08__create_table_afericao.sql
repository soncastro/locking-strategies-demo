CREATE TABLE public.afericao (
    
	id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
	pneu_id BIGINT NOT NULL,
	vida INT NOT NULL,

	CONSTRAINT pk_afericao_id 
        PRIMARY KEY (id),
        
	CONSTRAINT fk_afericao_pneu_id 
        FOREIGN KEY (pneu_id) 
        REFERENCES public.pneu(id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE
);

INSERT INTO public.afericao (id, pneu_id, vida)
OVERRIDING SYSTEM VALUE
VALUES (1, 1, 2);

SELECT setval(
    pg_get_serial_sequence('public.afericao', 'id'),
    (SELECT COALESCE(MAX(id), 1) FROM public.afericao),
    true
);
