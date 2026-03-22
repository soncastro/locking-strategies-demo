CREATE TABLE public.account (
    
	id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
	customer_id BIGINT NOT NULL,
	balance NUMERIC(19,2) NOT NULL,

	CONSTRAINT pk_account_id 
        PRIMARY KEY (id),
        
	CONSTRAINT fk_account_customer_id 
        FOREIGN KEY (customer_id) 
        REFERENCES public.customer(id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE
);

ALTER TABLE account
ADD COLUMN version BIGINT DEFAULT 0 NOT NULL;