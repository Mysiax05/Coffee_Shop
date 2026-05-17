CREATE OR REPLACE PROCEDURE p_check_product_exists(f_product_id int)
AS $$
BEGIN
    if not exists(select 1 from products where productid = f_product_id) then
        raise exception 'Product with ID % does not exist', f_product_id;
    end if;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE p_check_category_exists(f_category_id int)
AS $$
BEGIN
    if not exists(select 1 from categories where categoryid = f_category_id) then
        raise exception 'Category with ID % does not exist', f_category_id;
    end if;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE p_check_payment_method_exists(f_payment_method_id int)
AS $$
BEGIN
    if not exists(select 1 from paymentmethods where paymentmethodid = f_payment_method_id) then
        raise exception 'Payment method with ID % does not exist', f_payment_method_id;
    end if;
END;
$$ LANGUAGE plpgsql;