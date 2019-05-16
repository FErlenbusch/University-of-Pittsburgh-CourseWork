# Repository: 2018-09.project-3.template
# Assignment #3: Neo4j / Cypher

> Course: CS 1656 - Introduction to Data Science -- Fall 2018  

### Description
This is the **third assignment** for the CS 1656 -- Introduction to Data Science (CS 2056) class, for the Fall 2018 semester.

### Goal
The goal of this assignment is for you to gain familiarity with Graph Databases in general, and with Neo4j and, its query language, Cypher, in particular.

---

### What to do

In this assignment you are asked to:  
* download neo4j locally,  
* download the Movies database locally,   
* provide Cypher queries that answer 8 questions, and        
* write a Python script ('movie-queries.py') that will run your solutions for the 8 queries and store the query output in a file. 

### Database Model

We will use the Movies database <https://neo4j.com/developer/movie-database/>, which has the following node labels:
* Actor  
* Director  
* Movie  
* Person  
* User  
and the following relationship types (i.e., edge labels):
* ACTS_IN  
* DIRECTED  
* FRIEND  
* RATED  

The nodes in the Movies database have a number of attributes, including the following:
* name (for Actor/Director/Person/User)  
* birthday (for Actor/Director/Person/User)  
* title (for Movie)  
* genre (for Movie)  


### Setup 

You are asked to follow the installation instructions for docker and to utilize the lab material provided through `Lab 06` (October 26, 2018). This will enable you to have a locally running neo4j server, along with an interactive query interface. You will also be able to download the Movies database directly into neo4j.

Please note that although we will use the same database model for testing your submissions. However, it will not necessarily be identical to the one you will download.


### Connecting to neo4j using Python

As part of this repository, you are provided with a sample Python script (`cypher_sample1.py`) that connects to the local graph database (which you have established by following the previous steps).


### Queries

You are asked to provide Cypher queries that provide answers for the following questions. Note that **actors** refers to both male and female actors, unless explicitly specified otherwise. 

* **[Q1]** List the first 20 actors in descending order of the number of films they acted in.  
*OUTPUT*: actor_name, number_of_films_acted_in

* **[Q2]** List the titles of all movies with a review with at most 3 stars.
*OUTPUT*: movie title

* **[Q3]** Find the movie with the largest cast, out of the list of movies that have a review.   
*OUTPUT*: movie_title, number_of_cast_members

* **[Q4]** Find all the actors who have worked with at least 3 different directors (regardless of how many movies they acted in). For example, 3 movies with one director each would satisfy this (provided the directors where different), but also a single movie with 3 directors would satisfy it as well.
*OUTPUT*: actor_name, number_of_directors_he/she_has_worked_with

* **[Q5]** The Bacon number of an actor is the length of the shortest path between the actor and Kevin Bacon in the *"co-acting"* graph. That is, Kevin Bacon has Bacon number 0; all actors who acted in the same movie as him have Bacon number 1; all actors who acted in the same film as some actor with Bacon number 1 have Bacon number 2, etc. *List all actors whose Bacon number is exactly 2* (first name, last name). You can familiarize yourself with the concept, by visiting [The Oracle of Bacon](https://oracleofbacon.org).  
*OUTPUT*: actor_name

* **[Q6]** List which genres have movies where Tom Hanks starred in.  
*OUTPUT*: genre

* **[Q7]** Show which directors have directed movies in at least 2 different genres.  
*OUTPUT*: director name, number of genres

* **[Q8]** Show the top 5 pairs of actor, director combinations, in descending order of frequency of occurrence.   
*OUTPUT*: director's name, actors' name, number of times director directed said actor in a movie


### Output Format (ignore at your own risk!)

You are asked to store the output for running all Cypher queries by your python script in a **single** file, named `output.txt`. For each query, you should have a header line `### Q1 ###`, followed by the results of the query (one row at a time, with commas separating multiple fields). If you do not provide an answer for the query, you should still print the header line in your output file, but leave a blank line after it. Answers should be ordered by query number and separated by a blank line as well.

For example, for the following question:

Q0: show the 3 oldest actors in the database, with the oldest one first.  
*OUTPUT*: name, id

The corresponding Cypher query should be:
```
match (n:Actor) return n.name, n.id order by n.birthday ASC LIMIT 3
```

The output file should be as follows:
```
### Q0 ###
Claudia Cardinale, 4959
Oliver Reed, 936
Anthony Hopkins, 4173
```

Finally, there should be an empty line between different results.


---


### Important notes about grading
It is absolutely imperative that your python program:  
* runs without any syntax or other errors (using Python 3) -- we will run it using the following command:  
`python3 movie-queries.py`  
* generates file `output.txt` with the answers of all 8 queries  
* strictly adheres to the format specifications for output, as explained above.     
