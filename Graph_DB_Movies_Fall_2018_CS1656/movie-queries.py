import os
import sys
from neo4j.v1 import GraphDatabase, basic_auth

#connection with authentication
#driver = GraphDatabase.driver("bolt://localhost", auth=basic_auth("neo4j", "cs1656"), encrypted=False)

#connection without authentication
driver = GraphDatabase.driver("bolt://localhost", encrypted=False)

session = driver.session()
transaction = session.begin_transaction()

out_file = open('output.txt', 'w', encoding='utf-8')


#Question 1 
out_file.write('### Q1 ###\n')
result = transaction.run("""MATCH (a:Actor)-[:ACTS_IN]->(m:Movie) 
                            WITH a, count(m) AS cnt 
                            RETURN a.name, cnt 
                            ORDER BY cnt DESC LIMIT 20""")
for record in result:
    out_file.write(record['a.name'] + ', ' + str(record['cnt']) + '\n')
out_file.write('\n')
 
 
#Question 2
out_file.write('### Q2 ###\n') 
result = transaction.run("""MATCH ()-[r:RATED]->(m:Movie) 
                            WITH m, r, max(r.stars) AS max_stars 
                            WHERE max_stars < 4 
                            RETURN m.title""")
for record in result:
    out_file.write(record['m.title'] + '\n')
out_file.write('\n')
 
 
#Question 3
out_file.write('### Q3 ###\n')
result = transaction.run("""MATCH (m:Movie)<-[i:ACTS_IN]->(), 
                                  (m)<-[r:RATED]-() 
                            WITH m, COUNT(DISTINCT i) AS cast_cnt, 
                                 COUNT(r) AS ratings 
                            WHERE ratings > 0 
                            RETURN m.title, cast_cnt 
                            ORDER BY cast_cnt DESC Limit 1""")
for record in result:
    out_file.write(record['m.title'] + ', ' + str(record['cast_cnt']) + '\n')
out_file.write('\n')
 
 
#Question 4
out_file.write('### Q4 ###\n')
result = transaction.run("""MATCH (a:Actor)-[:ACTS_IN]->(m:Movie)<-[:DIRECTED]-(d:Director) 
                            WITH a, COUNT(DISTINCT d.name) AS cnt 
                            WHERE cnt >= 3 
                            RETURN a.name, cnt""")
for record in result:
    out_file.write(record['a.name'] + ', ' + str(record['cnt']) + '\n')
out_file.write('\n')


#Question 5
out_file.write('### Q5 ###\n')
result = transaction.run("""MATCH (bacon:Actor {name:'Kevin Bacon'})-[:ACTS_IN]->(m1)<-[:ACTS_IN]-(one_bacon), 
                                  (one_bacon)-[:ACTS_IN]->(m2)<-[:ACTS_IN]-(two_bacon) 
                            WHERE NOT (bacon)-[:ACTS_IN]->()<-[:ACTS_IN]-(two_bacon)
                            AND bacon <> two_bacon
                            RETURN DISTINCT two_bacon.name""")
for record in result:
    out_file.write(record['two_bacon.name'] + '\n')
out_file.write('\n')


#Question 6
out_file.write('### Q6 ###\n')
result = transaction.run("""MATCH (:Actor {name:'Tom Hanks'})-[:ACTS_IN]->(m) 
                            RETURN DISTINCT m.genre""")
for record in result:
    out_file.write(record['m.genre'] + '\n')
out_file.write('\n')


#Question 7
out_file.write('### Q7 ###\n')
result = transaction.run("""MATCH (d:Director)-[:DIRECTED]->(m:Movie) 
                            WITH d, COUNT(DISTINCT m.genre) AS genre_cnt 
                            WHERE genre_cnt >= 2 
                            RETURN d.name, genre_cnt""")
for record in result:
    out_file.write(record['d.name'] + ', ' + str(record['genre_cnt']) + '\n')
out_file.write('\n')


#Question 8
out_file.write('### Q8 ###\n')
result = transaction.run("""MATCH (d:Director)-[:DIRECTED]->(m:Movie)<-[:ACTS_IN]-(a:Actor) 
                            WITH d, a, count(DISTINCT m) as cnt 
                            RETURN d.name, a.name, cnt 
                            ORDER BY cnt DESC LIMIT 5""")
for record in result:
    out_file.write(record['d.name'] + ', ' + record['a.name'] + ', ' + str(record['cnt']) + '\n')
out_file.write('\n')


transaction.close()
session.close()







