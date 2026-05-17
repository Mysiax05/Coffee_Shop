CREATE OR REPLACE TRIGGER t_validate_leaf_category
BEFORE INSERT OR UPDATE OF categoryid ON products
FOR EACH ROW EXECUTE FUNCTION t_f_validate_leaf_category();

CREATE OR REPLACE TRIGGER t_validate_product_active
BEFORE INSERT ON orderdetails
FOR EACH ROW EXECUTE FUNCTION t_f_validate_product_active();

CREATE OR REPLACE TRIGGER t_validate_payment_method_active
BEFORE INSERT ON payments
FOR EACH ROW EXECUTE FUNCTION t_f_validate_payment_method_active();

CREATE OR REPLACE TRIGGER t_set_paid_at
BEFORE UPDATE ON payments
FOR EACH ROW EXECUTE FUNCTION t_f_set_paid_at();