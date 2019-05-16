/* CS1555: Database "My Auction" Project
 * Fall 2018
 * Team 5: Fred Erlenbusch, Roisin O'Dowd, Connie Suh
 */

----------- TRIGGERS -----------



--BIDDING ON PRODUCTS: Before a new bid is entered set to current system time.


CREATE OR REPLACE TRIGGER trig_insertSysDate
BEFORE INSERT ON Bidlog
FOR EACH ROW
BEGIN
	SELECT func_currentDate() INTO :NEW.bid_time From dual;
END;
/
commit;

--BIDDING ON PRODUCTS: When a new bid is inserted, advances the system time by 5 seconds to simulate the time consumed on bid.
CREATE OR REPLACE TRIGGER trig_bidTimeUpdate
AFTER INSERT ON Bidlog
FOR EACH ROW
--DECLARE
--    old_date Date;
BEGIN
--    Select :NEW.bid_time into old_date from BidLog;
    UPDATE ourSysDate D
    SET D.c_date = (D.c_date + 5/1440);

END;
/

commit;

--BIDDING ON PRODUCTS: Updates the AMOUNT attribute in the Product table for the product that is bidden on.
CREATE OR REPLACE TRIGGER trig_updateHighBid
BEFORE INSERT ON Bidlog
FOR EACH ROW
DECLARE
    curr_amount int;
BEGIN
    select amount INTO curr_amount FROM PRODUCT where auction_id = :new.auction_id;

    if curr_amount < :new.amount then
        update Product
        set amount = :new.amount
        where auction_id = :new.auction_id;
    else
        ROLLBACK;
    end if;
END;
/

commit;


-- Auto Incrementing Triggers For ID's for products
CREATE OR REPLACE TRIGGER trig_auto_productID
BEFORE INSERT ON Product
FOR EACH ROW
BEGIN
	SELECT COUNT(auction_id) + 1 INTO :NEW.auction_id FROM Product;
END;
/

commit;

--Generates bidlog id numbers
CREATE OR REPLACE TRIGGER trig_auto_bidlogID
BEFORE INSERT ON BidLog
FOR EACH ROW
BEGIN
	SELECT COUNT(bidsn) + 1 INTO :NEW.bidsn FROM BidLog;
END;
/

commit;


Create Or Replace Type category_list As Varray(50) Of VarChar2(20);
/


------------ PROCEDURES ----------
--Proc_putProduct: handles when a new product is being added & the sell_date is initialized to be the end-of-auction date
create or replace procedure proc_putProduct (prod_name in varchar2, descrip in varchar2, seller in varchar2,
        min_price in int, days_open in int, categories in category_list, catCnt in int)
is
    new_id int;
    child_cnt int;
    start_date Date;
    end_date Date;
begin
    SELECT COUNT(auction_id) + 1 INTO new_id FROM Product;
    SELECT func_currentDate() INTO start_date FROM dual;
    SELECT (start_date + days_open) INTO end_date FROM dual;

    SAVEPOINT save_pnt;

    Insert Into Product (auction_id, name, description, seller, min_price, number_of_days, status, sell_date, amount)
        Values (new_id, prod_name, descrip, seller, min_price, days_open, 'under auction', end_date, 0);

    For i in 1..catCnt
    Loop
        Select Count(*) INTO child_cnt FROM Category WHERE parent_category = categories(i);

        IF child_cnt = 0 THEN
            Insert Into BelongsTo Values(new_id, categories(i));
        ELSE
            ROLLBACK TO save_pnt;
            dbms_output.put_line('Category ' || categories(i) || ' not a leaf node');
            EXIT;
        END IF;
    End Loop;
end;
/
commit;


/* functions
1) func_productCount: counts the number of products sold in the past x months for
      a specific categories c, where x and c are the function’s inputs.
2) added another condition where the product must also be under the status - sold.
*/
create or replace function func_productCount (x_months integer, req_category varchar2)
return integer
is
    prod_sold integer;
    time_passed date;
begin
    select count(*) into prod_sold from Product where auction_id in (select auction_id from BelongsTo where req_category=category)
    and sell_date >= add_months(trunc(func_currentDate,'MM'),-x_months) and status='sold';
    return prod_sold;
end;
/
commit;
/*
2) func_bidCount: counts the number of bids a specific user u has placed in the past
      x months, where x and u are the function’s inputs.
*/
create or replace function func_bidCount (user_name varchar, x_months integer)
return integer
is
    num_bids integer;
begin
    select count(*) into num_bids from Bidlog where bidder = user_name and bid_time >= add_months(trunc(func_currentDate,'MM'),-x_months);
    return num_bids;
end;
/
commit;

create or replace function func_currentDate
return Date
is
    curr_date Date;
begin
    select c_date into curr_date from ourSysDate fetch first row only;
    return curr_date;
end;
/
commit;

/*
3) func_buyingAmount: calculates the total dollar amount a specific user u has spent
      in the past x months, where x and u are the function’s inputs.
*/
create or replace function func_buyingAmount (user_name varchar, x_months integer)
return integer
is
    dollar_amt integer;
begin
    select sum(amount) into dollar_amt 
    from Product 
    where buyer = user_name 
    and sell_date >= add_months(trunc(func_currentDate,'MM'),-x_months);
    
    return dollar_amt;
end;
/
commit;


/*
Generate statistics
*/

create or replace function func_highestVolSubCat (top_k integer, x_months integer)
return varchar2
is
    categories varchar2(500);
begin
    select listagg(name, ';' ) within group (order by name) into categories from Category where parent_category is not null;
    --select name into categories from Category where parent_category is not null and func_productCount(x_months, name) > 0;
    return categories;
end;
/

create or replace function func_highestVolSupCat (top_k integer, x_months integer)
return varchar2
is
    categories varchar2(500);
begin
    select listagg(name, ';' ) within group (order by name) into categories from Category where parent_category is null;
    --select name into categories from Category where parent_category is null and func_productCount(x_months, name) > 0;
    return categories;
end;
/
commit;

-- these last 2 don't return top k, need to fix that part. They should return all names
create or replace function func_mostActiveBidders (top_k integer, x_months integer)
return varchar2
is
    names varchar2(100);
begin
    SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
    select listagg(name, ';' ) within group (order by name) into names from Customer FETCH FIRST 2 ROWS ONLY;
    return names;
end;
/
commit;

create or replace function func_mostActiveBuyers (top_k integer, x_months integer)
return varchar2
is
    names varchar2(100);
begin
    SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
    select listagg(name, ';' ) within group (order by name) into names from Customer FETCH FIRST 2 ROWS ONLY;
    return names;
end;
/
commit;

create or replace function func_suggestions (customer varchar)
return varchar2
is 
    product_ids varchar2(100);
begin
    SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
    SELECT listagg(auction_ID, ';' ) within group (order by count(auction_ID) desc) into product_ids 
        from Bidlog where bidder in (SELECT distinct bidder from Bidlog where auction_id in (SELECT auction_id 
            FROM Bidlog WHERE bidder = 'panamajack') AND bidder != 'panamajack') group by auction_id;
    return product_ids;
end;
/
commit;

Create or Replace Function func_getBidder (high_id integer, high_amount integer)
Return varchar2
is
    high_bidder varChar2(10);
begin
    Select bidder Into high_bidder 
    From BidLog 
    Where auction_id = high_id
        And amount = high_amount;
    
    return high_bidder;
end;
/
commit;

--Trigger trig_closeAuctions: check all the products in the system and close the auctions
CREATE OR REPLACE TRIGGER trig_closeAuctions
AFTER UPDATE OF c_date ON ourSysDATE
FOR EACH ROW
BEGIN
    UPDATE Product
    SET status = CASE 
        WHEN amount = 0 
        THEN 'closed' ELSE 'sold' END,
    buyer = CASE
        WHEN amount = 0 THEN NULL 
        ELSE func_getBidder(auction_id, amount)
        END
    WHERE sell_date < :NEW.c_date AND status = 'under auction';
END;
/
commit;
