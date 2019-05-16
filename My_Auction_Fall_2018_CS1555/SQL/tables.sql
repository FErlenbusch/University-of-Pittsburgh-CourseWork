/* CS1555: Database "My Auction" Project
 * Fall 2018
 * Team 5: Fred Erlenbusch, Roisin O'Dowd, Connie Suh
 */

Drop Table BelongsTo Cascade Constraints;
Drop Table Category Cascade Constraints;
Drop Table Bidlog Cascade Constraints;
Drop Table Product Cascade Constraints;
Drop Table Administrator Cascade Constraints;
Drop Table Customer Cascade Constraints;
Drop Table ourSysDate Cascade Constraints;

commit;

Drop View Users Cascade Constraints;
Drop View Closed Cascade Constraints;
Drop View Withdrawn Cascade Constraints;
Drop View UnderAuction Cascade Constraints;
Drop View Admins Cascade Constraints;
Drop View Sold Cascade Constraints;

commit;

Create Table ourSysDate (
    c_date Date,

    Constraint ourSysDate_PK Primary Key (c_date)
);

Create Table Customer (
    login VarChar2(10),
    password VarChar2(10) Not Null,
    name VarChar2(20) Not Null,
    address VarChar2(30) Not Null,
    email VarChar2(20) Not Null Unique,

    Constraint Customer_PK Primary Key (login)
);

Create Table Administrator (
    login VarChar2(10),
    password VarChar2(10) Not Null,
    name VarChar2(20) Not Null,
    address VarChar2(30) Not Null,
    email VarChar2(20) Not Null Unique,

    Constraint Administrator_PK Primary Key (login)
);

Create Table Product (
    auction_id Int,
    name VarChar2(20) Not Null,
    description VarChar2(30),
    seller VarChar2(10) Not Null,
    min_price Int Not Null,
    number_of_days Int Not Null,
    status VarChar2(15) Not Null,
    buyer VarChar2(10),
    sell_date Date,
    amount int,

    Constraint Product_PK Primary Key (auction_id),
    Constraint Product_Seller_FK Foreign Key (seller)
        References Customer(login),
    Constraint Product_Buyer_FK Foreign Key (buyer)
        References Customer(login),
    Constraint Product_Min_Price_Check
    	Check (min_price >= 0),
    Constraint Product_Amount_Check
    	Check (amount >= 0),
    Constraint Product_Status_Check
    	Check (status In ('under auction', 'sold', 
    						'withdrawn', 'closed'))
);

Create Table BidLog (
    bidsn Int,
    auction_id Int Not Null,
    bidder VarChar2(10) Not Null,
    bid_time Date Not Null,
    amount Int Not Null,

    Constraint BidLog_PK Primary Key (bidsn),
    Constraint BidLog_Product_FK Foreign Key (auction_id)
        References Product(auction_id) On Delete Cascade,
    Constraint BidLog_Customer_FK Foreign Key (bidder)
        References Customer(login),
    Constraint BidLog_Amount_Check 
    	Check (amount >= 0)
);

Create Table Category (
    name VarChar2(20),
    parent_category VarChar2(20),

    Constraint Category_PK Primary Key (name),
    Constraint Category_Parent_FK Foreign Key (parent_category)
        References Category(name) On Delete Cascade,
    Constraint Category_Not_Parent_Check 
    	Check (name <> parent_category)
);

Create Table BelongsTo (
    auction_id int,
    category VarChar2(20),

    Constraint BelongsTo_PK Primary Key (auction_id, category),
    Constraint BelongsTo_Product_FK Foreign Key (auction_id)
        References Product(auction_id),
    Constraint BelongsTo_Category_FK Foreign Key (category)
        References Category(name) On Delete Cascade
);

commit;


Create View Users As
	Select login, name, email
	From Customer;
	
Create View Admins As
	Select login, name, email 
	From Administrator;
	
Create View UnderAuction As
	Select auction_id, name, description, seller
		number_of_days, amount
	From Product
	Where status = 'under auction';

Create View Sold As
	Select auction_id, name, description, seller,
		number_of_days, buyer, amount, sell_date
	From Product
	Where status = 'sold';
	
Create View Withdrawn As
	Select auction_id, name, description, seller,
		number_of_days
	From Product
	Where status = 'withdrawn';

Create View Closed As
	Select auction_id, name, description, seller,
		number_of_days, buyer, amount, sell_date
	From Product
	Where status = 'closed';

commit;

