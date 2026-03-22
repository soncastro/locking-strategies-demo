CREATE TYPE public.account_movement_type AS ENUM ('CREDIT', 'DEBIT');

CREATE TABLE public.account_movement (
    
	id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
	account_id BIGINT NOT NULL,
        movement_type public.account_movement_type NOT NULL,
        amount NUMERIC(19,2) NOT NULL,

	CONSTRAINT pk_account_movement_id 
        PRIMARY KEY (id),

	CONSTRAINT fk_account_movement_account_id 
        FOREIGN KEY (account_id) 
        REFERENCES public.account(id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE
);
