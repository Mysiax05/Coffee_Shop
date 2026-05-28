create view vw_active_products as
    select productid, categoryid, name, price, stock, isactive, attributes
    from products
    where isactive = true;

create view vw_active_paymentmethods as
    select paymentmethodid, provider, type, isactive
    from paymentmethods
    where isactive = true;

create view vw_active_addresses as
    select addressid, customerid, label, street, city, postalcode, country, isactive, isdefault
    from addresses
    where isactive = true;