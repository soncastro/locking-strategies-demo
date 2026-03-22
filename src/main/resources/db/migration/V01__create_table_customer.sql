CREATE TABLE if NOT EXISTS public.customer (
    
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,

    CONSTRAINT pk_customer_id
        PRIMARY KEY (id),
    CONSTRAINT uk_customer_name
        UNIQUE (name)
);
