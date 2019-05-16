import sqlite3 as lite
import csv
import re
con = lite.connect('cs1656.sqlite')

with con:
	cur = con.cursor() 

	########################################################################		
	### CREATE TABLES ######################################################
	########################################################################		
	# DO NOT MODIFY - START 
	cur.execute('DROP TABLE IF EXISTS Actors')
	cur.execute("CREATE TABLE Actors(aid INT, fname TEXT, lname TEXT, gender CHAR(6), PRIMARY KEY(aid))")

	cur.execute('DROP TABLE IF EXISTS Movies')
	cur.execute("CREATE TABLE Movies(mid INT, title TEXT, year INT, rank REAL, PRIMARY KEY(mid))")

	cur.execute('DROP TABLE IF EXISTS Directors')
	cur.execute("CREATE TABLE Directors(did INT, fname TEXT, lname TEXT, PRIMARY KEY(did))")

	cur.execute('DROP TABLE IF EXISTS Cast')
	cur.execute("CREATE TABLE Cast(aid INT, mid INT, role TEXT)")

	cur.execute('DROP TABLE IF EXISTS Movie_Director')
	cur.execute("CREATE TABLE Movie_Director(did INT, mid INT)")
	# DO NOT MODIFY - END

	########################################################################		
	##### READ DATA FROM FILES and INSERT DATA INTO DATABASE ###############
	########################################################################		
	# actors.csv, cast.csv, directors.csv, movie_dir.csv, movies.csv
	# UPDATE THIS
	
	# Read in Actors data into database
	csvFile = open('actors.csv')
	reader = csv.reader(csvFile, delimiter=',')
	for row in reader:
	    cur.execute("INSERT INTO Actors VALUES('" + row[0] + "', '" + row[1] + "', '" 
				+ row[2] + "', '" + row[3] + "')")
	csvFile.close()
	con.commit()
	
	# Read in Movies data into database
	csvFile = open('movies.csv')
	reader = csv.reader(csvFile, delimiter=',')
	for row in reader:
	    cur.execute("INSERT INTO Movies VALUES('" + row[0] + "', '" + row[1] + "', '" 
				+ row[2] + "', '" + row[3] + "')")
	csvFile.close()
	con.commit()

	# Read in Cast data into database
	csvFile = open('cast.csv')
	reader = csv.reader(csvFile, delimiter=',')
	for row in reader:
	    cur.execute("INSERT INTO Cast VALUES('" + row[0] + "', '" + row[1] + "', '" 
				+ row[2] + "')")
	csvFile.close()
	con.commit()
	
	# Read in Directors data into database
	csvFile = open('directors.csv')
	reader = csv.reader(csvFile, delimiter=',')
	for row in reader:
	    cur.execute("INSERT INTO Directors VALUES('" + row[0] + "', '" + row[1] + "', '" 
				+ row[2] + "')")
	csvFile.close()
	con.commit()
	
	# Read in Movie Directors data into database
	csvFile = open('movie_dir.csv')
	reader = csv.reader(csvFile, delimiter=',')
	for row in reader:
	    cur.execute("INSERT INTO Movie_Director VALUES('" + row[0] + "', '" + row[1] + "')")
	csvFile.close()
	con.commit()    

	########################################################################		
	### QUERY SECTION ######################################################
	########################################################################		
	queries = {}

	# DO NOT MODIFY - START 	
	# DEBUG: all_movies ########################
	queries['all_movies'] = '''
SELECT * FROM Movies
'''	
	# DEBUG: all_actors ########################
	queries['all_actors'] = '''
SELECT * FROM Actors
'''	
	# DEBUG: all_cast ########################
	queries['all_cast'] = '''
SELECT * FROM Cast
'''	
	# DEBUG: all_directors ########################
	queries['all_directors'] = '''
SELECT * FROM Directors
'''	
	# DEBUG: all_movie_dir ########################
	queries['all_movie_dir'] = '''
SELECT * FROM Movie_Director
'''	
	# DO NOT MODIFY - END

	########################################################################		
	### INSERT YOUR QUERIES HERE ###########################################
	########################################################################		
	# NOTE: You are allowed to also include other queries here (e.g., 
	# for creating views), that will be executed in alphabetical order.
	# We will grade your program based on the output files q01.csv, 
	# q02.csv, ..., q12.csv

	# Q01 ########################		
	queries['q01'] = '''
	WITH Movie_90s(mid, aid, year) AS (
		SELECT m.mid, c.aid, year FROM Movies m 
		INNER JOIN Cast c ON m.mid = c.mid 
		WHERE m.year BETWEEN 1990 AND 1999),
	Movie_09(mid, aid, year) AS (
		SELECT m.mid, c.aid, year FROM Movies m 
		INNER JOIN Cast c ON m.mid = c.mid 
		WHERE m.year > 2009)
	SELECT DISTINCT a.fname, a.lname FROM (
		SELECT a.aid FROM Movie_90s a 
		INNER JOIN Movie_09 b ON a.aid = b.aid) m
	INNER JOIN Actors a ON a.aid = m.aid
	ORDER BY a.lname, a.fname
	'''	
	
	# Q02 ########################		
	queries['q02'] = '''
	WITH ForceAwakens(year, rank) AS (
		SELECT year, rank FROM Movies
		WHERE title = 'Star Wars VII: The Force Awakens')
	Select m.title, m.year 
	FROM Movies m, ForceAwakens f
	WHERE m.year = f.year 
		AND m.rank > f.rank
	ORDER BY m.title
	'''	

	# Q03 ########################		
	queries['q03'] = '''
	WITH StarWars(aid, cnt) AS (
		SELECT DISTINCT c.aid, COUNT(DISTINCT c.mid)
		FROM Cast c INNER JOIN Movies m 
			ON c.mid = m.mid
		WHERE m.title LIKE '%Star Wars%'
		GROUP BY aid)
	Select a.fname, a.lname 
	FROM Actors a INNER JOIN StarWars s
		ON a.aid = s.aid
	ORDER BY s.cnt DESC, a.lname, a.fname
	'''	

	# Q04 ########################		
	queries['q04'] = '''
	WITH Actors87(aid) AS (
		SELECT c.aid FROM Cast c 
		INNER JOIN Movies m
			ON c.mid = m.mid
		WHERE m.year < 1987),
	Actors87Up(aid) AS (
		SELECT c.aid FROM Cast c 
		INNER JOIN Movies m
			ON c.mid = m.mid
		WHERE m.year > 1986)
	SELECT a.fname, a.lname 
	FROM Actors a
	WHERE a.aid IN Actors87 AND a.aid NOT IN Actors87Up
	ORDER BY a.lname, a.fname
	'''	

	# Q05 ########################		
	queries['q05'] = '''
	SELECT d.fname, d.lname, Count(DISTINCT m.mid) as cnt
	FROM Directors d INNER JOIN Movie_Director m
		ON d.did = m.did
	GROUP BY m.did
	ORDER BY cnt DESC, d.lname, d.fname
	LIMIT 20
	'''	

	# Q06 ########################		
	queries['q06'] = '''
	WITH MovieCast(mid, cnt) AS (
		SELECT mid, COUNT(DISTINCT aid) AS cnt
		FROM Cast GROUP BY mid),
	The20thPlace(cnt) AS (
		SELECT cnt FROM MovieCast
		ORDER BY cnt DESC
		LIMIT 20, 1)
	SELECT m.title, c.cnt
	FROM The20thPlace p, Movies m 
	INNER JOIN MovieCast c
		ON m.mid = c.mid
	WHERE c.cnt >= p.cnt
	ORDER BY c.cnt DESC
	'''	

	# Q07 ########################		
	queries['q07'] = '''
	WITH DCast(aid, mid, role) AS (
		SELECT * FROM Cast
		GROUP BY aid, mid),
	Males (mid, cnt) AS (
		SElECT c.mid, COUNT(a.gender) as cnt
		FROM DCast c INNER JOIN Actors a
			ON c.aid = a.aid
		WHERE a.gender = 'Male'
		GROUP BY c.mid),
	Females (mid, cnt) AS (
		SElECT c.mid, COUNT(a.gender)
		FROM DCast c INNER JOIN Actors a
			ON c.aid = a.aid
		WHERE a.gender = 'Female'
		GROUP By c.mid),
	MoreFemales(mid, fcnt, mcnt) AS (
		SELECT f.mid, f.cnt, m.cnt
		FROM Females f INNER JOIN Males m
			ON f.mid = m.mid
		WHERE f.cnt > m.cnt)
	Select m.title, f.fcnt, f.mcnt
	FROM Movies m INNER JOIN MoreFemales f
		ON m.mid = f.mid
	ORDER BY m.title
	'''	

	# Q08 ########################		
	queries['q08'] = '''
	WITH MDirs(mid, did, fname, lname) AS (
		SELECT m.mid, d.did, d.fname, d.lname
		FROM Movie_Director m INNER JOIN Directors d
			ON m.did = d.did),
	MActors(mid, aid, fname, lname) AS (
		SELECT m.mid, a.aid, a.fname, a.lname
		FROM Cast m INNER JOIN Actors a
			ON m.aid = a.aid),
	ActorDir(mid, aid, afname, alname, did, dfname, dlname) AS (
		SELECT a.mid, aid, a.fname, a.lname, 
			d.did, d.fname, d.lname
		FROM MActors a INNER JOIN MDirs d 
			ON a.mid = d.mid
		WHERE a.fname <> d.fname 
			AND a.lname <> d.lname),
	ActorDirCnt(fname, lname, cnt) AS (
		SELECT afname, alname, COUNT(did) as cnt
		FROM ActorDir GROUP BY aid)
	SELECT * From ActorDirCnt
	WHERE cnt > 5
	ORDER BY cnt DESC
	'''	

	# Q09 ########################		
	queries['q09'] = '''
	WITH SActors(aid, fname, lname, mid) AS (
		SELECT a.aid, a.fname, a.lname, m.mid
		FROM Actors a INNER JOIN Cast m 
			ON a.aid = m.aid
		WHERE a.fname LIKE 'S%'),
	AMovies(aid, fname, lname, mid, year, title) AS (
		SELECT DISTINCT a.*, m.year, m.title
		FROM SActors a INNER JOIN Movies m
			ON a.mid = m.mid),
	Debuts(aid, year) AS (
		SELECT aid, MIN(year) 
		FROM AMovies
		GROUP BY aid)
	SELECT a.fname, a.lname, COUNT(mid) AS cnt
	FROM AMovies a, Debuts d
	WHERE a.aid = d.aid
		AND a.year = d.year
	GROUP BY a.aid
	ORDER BY cnt
	'''	

	# Q10 ########################		
	queries['q10'] = '''
	WITH IDS(aid, mid, did) AS (
		SELECT a.aid, a.mid, d.did
		FROM Cast a INNER JOIN Movie_Director d
			ON a.mid = d.mid),
	LNames(afname, alname, mid, dfname, dlname) AS (
		SELECT a.fname, a.lname, d.mid, d.fname, d.lname
		FROM Actors a INNER JOIN (
			SELECT m.aid, m.mid, d.fname, d.lname
			FROM IDS m INNER JOIN Directors d
				ON m.did = d.did) d
		ON a.aid = d.aid),
	Nepetism(lname, mid) AS (
		SELECT DISTINCT alname, mid
		FROM LNames
		WHERE alname = dlname 
			AND afname <> dfname)
	SELECT n.lname, m.title
	FROM Nepetism n INNER JOIN Movies m
		ON n.mid = m.mid
	ORDER BY n.lname
	'''	

	# Q11 ########################		
	queries['q11'] = '''
	WITH TomHanks(aid, mid) AS (
		SELECT DISTINCT a.aid, m.mid
		FROM Actors a INNER JOIN Cast m
			ON a.aid = m.aid
		WHERE a.fname = 'Tom' 
			AND a.lname = 'Hanks'),
	One(aid) AS (
		SELECT DISTINCT a.aid
		FROM TomHanks t INNER JOIN Cast a
			ON t.mid = a.mid
		WHERE t.aid <> a.aid),
	OneOtherMovies(mid) AS (
		SELECT DISTINCT mid FROM Cast 
		WHERE mid NOT IN (
			SELECT mid FROM TomHanks)
		AND aid IN  One),
	TWO(aid) AS (
		SELECT DISTINCT c.aid
		FROM TomHanks t, Cast c 
		WHERE c.mid IN OneOtherMovies
		AND c.aid NOT IN ONE
		AND c.aid <> t.aid)
	SELECT DISTINCT a.fname, a.lname 
	FROM TWO t INNER JOIN Actors a
		ON t.aid = a.aid
	ORDER BY a.lname, a.fname
	'''	

	# Q12 ########################		
	queries['q12'] = '''
	WITH AMovies(aid, mid, rank) AS (
		SELECT DISTINCT a.aid, a.mid, m.rank
		FROM Cast a INNER JOIN Movies m
			ON a.mid = m.mid),
	Popularity(aid, cnt, pop) AS (
		SELECT aid, COUNT(mid), AVG(rank) as avg
		FROM AMovies GROUP BY aid
		ORDER BY avg DESC LIMIT 20)
	SELECT a.fname, a.lname, p.cnt, p.pop
	FROM Actors a INNER JOIN Popularity p
		ON a.aid = p.aid
	'''	


	########################################################################		
	### SAVE RESULTS TO FILES ##############################################
	########################################################################		
	# DO NOT MODIFY - START 	
	for (qkey, qstring) in sorted(queries.items()):
		try:
			cur.execute(qstring)
			all_rows = cur.fetchall()
			
			print ("=========== ",qkey," QUERY ======================")
			print (qstring)
			print ("----------- ",qkey," RESULTS --------------------")
			for row in all_rows:
				print (row)
			print (" ")

			save_to_file = (re.search(r'q0\d', qkey) or re.search(r'q1[012]', qkey))
			if (save_to_file):
				with open(qkey+'.csv', 'w') as f:
					writer = csv.writer(f)
					writer.writerows(all_rows)
					f.close()
				print ("----------- ",qkey+".csv"," *SAVED* ----------------\n")
		
		except lite.Error as e:
			print ("An error occurred:", e.args[0])
	# DO NOT MODIFY - END
	
