CREATE OR REPLACE TRIGGER t_validate_leaf_category
BEFORE INSERT OR UPDATE OF categoryid ON products
FOR EACH ROW EXECUTE FUNCTION t_f_validate_leaf_category();

CREATE OR REPLACE TRIGGER t_validate_product_active
BEFORE INSERT ON orderdetails
FOR EACH ROW EXECUTE FUNCTION t_f_validate_product_active();

CREATE OR REPLACE TRIGGER t_validate_payment_method_active
BEFORE INSERT ON payments
FOR EACH ROW EXECUTE FUNCTION t_f_validate_payment_method_active();

create or replace trigger trg_validate_address_active
before insert or update on orders
for each row execute function t_f_validate_address_active();

create or replace trigger trg_validate_order_status_transition
before update on orders
for each row execute function t_f_validate_order_status_transition();

create or replace trigger trg_restore_stock_on_cancel
after update on orders
for each row when (old.status is distinct from new.status) execute function t_f_restore_stock_on_cancel();