ALTER TABLE public.account_movement
    ALTER COLUMN movement_type TYPE VARCHAR(20)
    USING movement_type::text;

ALTER TABLE public.account_movement
    ADD CONSTRAINT chk_account_movement_movement_type
    CHECK (movement_type IN ('CREDIT', 'DEBIT'));

DROP TYPE public.account_movement_type;
