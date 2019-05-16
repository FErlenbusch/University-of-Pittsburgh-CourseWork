
/* CS1555: Database "My Auction" Project 
 * Fall 2018
 * Team 5: Fred Erlenbusch, Roisin O'Dowd, Connie Suh
 */

Insert Into ourSysDate 
    Values (To_Date ('2018-nov-18/00:00:00','YYYY-MON-DD/HH24:MI:SS'));
commit;


Insert Into Customer
    Values ('builderbob', 'bobpass', 'Bob Builder', '123 Pitt St, Pittsburgh, PA', 'bob@builder.com');
Insert Into Customer
    Values ('cindywreck', 'cindypass', 'Cindy Lou', '555 5th Ave, Pittsburgh, PA', 'cindy@yahoo.com');
Insert Into Customer
    Values ('panamajack', 'jackpass', 'Jack Smith', '432 10th Ave, Pittsburgh, PA', 'jack@google.com');
commit;


Insert Into Administrator
    Values ('admin', 'root', 'Team 5 Admin', '555 Admin Ln, Pittsburgh, PA', 'admin@myauction.com');
commit;


Insert Into Category 
    Values ('Antiques', null);
Insert Into Category
    Values ('Jewlery', null);
Insert Into Category
    Values ('Electronics', null);
Insert Into Category
    Values ('Sports', null);
Insert Into Category
    Values ('Office Supplies', null);
Insert Into Category
    Values ('FootBall', 'Sports');
Insert Into Category 
    Values ('BaseBall', 'Sports');
Insert Into Category
    Values ('Helmets', 'FootBall');
Insert Into Category
    Values ('Mittens', 'BaseBall');
Insert Into Category
    Values ('Phones', 'Electronics');
Insert Into Category
    Values ('Game Console', 'Electronics');
Insert Into Category
    Values ('Apple', 'Phones');
Insert Into Category
    Values ('Android', 'Phones');
                                                          
commit;


Insert Into Product
    Values (0, 'Signed Baseball', 'Signed by the Stellers', 'panamajack', 100, 5, 'under auction', 
            null, null, 0);
Insert Into Product
    Values (0, 'Gold Ring', '24k Gold Wedding Ring', 'builderbob', 500, 10, 'under auction', 
            null, null, 0);
Insert Into Product
    Values (0, 'Antique Knife', 'Antique Knife from WWII', 'cindywreck', 100, 3, 'under auction', 
            null, null, 0);
Insert Into Product
    Values (0, '2012 Macbook Pro', 'Working 2012 15in Macbook Pro', 'panamajack', 50, 5, 'under auction', 
            null, null, 0);
Insert Into Product
    Values (0, 'Red Stapler', 'Have you seen my stapler?', 'builderbob', 1000, 5, 'under auction', 
            null, null, 0);
Insert Into Product
    Values (0, 'Signed Football', 'Signed by the TrailBlazers', 'builderbob', 1000, 5, 'under auction', 
            null, null, 500);
commit;


Insert Into BelongsTo
    Values (1, 'BaseBall');
Insert Into BelongsTo
    Values (2, 'Jewlery');
Insert Into BelongsTo
    Values (3, 'Antiques');
Insert Into BelongsTo
    Values (4, 'Electronics');
Insert Into BelongsTo
    Values (5, 'Office Supplies');
Insert Into BelongsTo 
    Values (6, 'FootBall');
commit;

Insert Into BidLog 
    Values (0, 5, 'cindywreck', null, 500);
Insert Into BidLog 
    Values (0, 5, 'panamajack', null, 600);
Insert Into BidLog 
    Values (0, 5, 'cindywreck', null, 900);
Insert Into BidLog 
    Values (0, 5, 'panamajack', null, 1100);
Insert Into BidLog 
    Values (0, 4, 'cindywreck', null, 5);
Insert Into BidLog 
    Values (0, 4, 'builderbob', null, 15);
Insert Into BidLog 
    Values (0, 1, 'cindywreck', null, 120);
Insert Into BidLog 
    Values (0, 2, 'panamajack', null, 450);
Insert Into BidLog 
    Values (0, 3, 'builderbob', null, 60);
Insert Into BidLog 
    Values (0, 3, 'panamajack', null, 150);
Insert Into BidLog
    Values (0, 6, 'panamajack', null, 1200);
Insert Into BidLog
    Values (0, 6, 'cindywreck', null, 1500);
commit;

