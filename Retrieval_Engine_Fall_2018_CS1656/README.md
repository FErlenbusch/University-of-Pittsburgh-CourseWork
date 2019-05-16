# Repository: 2018-09.project-2.template
# Assignment #2: Information Retrieval Engine  

> Course: CS 1656 - Introduction to Data Science -- Fall 2018   

### Description
This is the **second assignment** for the CS 1656 -- Introduction to Data Science (CS 2056) class, for the Fall 2018 semester.

### Goal
The goal of this assignment is to help you better understand information retrieval by building a simple information retrieval system.

---

### What to do
In this assignment you will build a simple information retrieval engine that processes a set of documents, creates an inverted index to speed up information retrieval, and returns a ranked list of document filenames that match the specified keyword search, along with relevance scores and a breakdown of weights for all keywords.

You will actually need to build two different Python programs: `pire-index.py` and `pire-search.py`, as follows.

#### Pitt Information Retrieval Engine / Indexer Program (`pire-index.py`)
This program will read all files that are in subdirectory `input/` (assuming they are all text files) and construct an inverted index. Before constructing the inverted index, the program should:
* convert all characters to lower case,  
* eliminate punctuation,  
* eliminate numbers, and  
* perform stemming using the `nltk` stemmer.  

The inverted index should have for every word: a list of documents the word appears in, along with how many times the word appeared per document. Additionally, for every word, there should be a count of how many documents the word appears in (in order to compute its inverse document frequency). There should also be a count of the total number of documents. All this information needs to be in a single data structure (suggest *dict*).

Before the program terminates, it should store the created data structure as a JSON object in a file named `inverted-index.json`. 

> Directory `input/` has nine sample documents (`doc1.txt` ... `doc9.txt`) that can be used for testing your program. You are encouraged to test your program with other documents as well. We will grade your programs with additional document collections as well. 
>

#### Pitt Information Retrieval Engine / Search Program (`pire-search.py`)
This program will read the JSON object from file `inverted-index.json` and a set of keywords from file `keywords.txt` and will produce an ordered list of document filenames, along with their relevance scores and a breakdown of weights for all keywords.

The keyword file will contain one set of keywords per line, that are space separated. It may have one ore more lines.
> A sample `keywords.txt` file is provided.
>

The relevance score for each document will be computed as explained in class. 
> w(key, doc) = (1 + log2 freq(key,doc)) * log2 (N / n(doc))
> where n(doc) is the number of documents that contain keyword key and N is the total number of documents
>

The output of your program should be in the following format:

```
------------------------------------------------------------
keywords = pittsburgh steelers 

[1] file=doc3.txt score=3.532495
    weight(pittsburgh)=0.362570
    weight(steelers)=3.169925
...
```
> A sample output, for the keywords contained in the `keywords.txt` file is provided in the file `output.txt` 
>  

Note that in cases of documents with the same relevance score, their ranks are tied and the same number is used for all (for example, there are four documents ranked [5] in the sample output. You should order those by filename order. 

### Important notes about grading
It is absolutely imperative that your python programs:  
* run without any syntax or other errors (using Python 3) -- we will run them using the following command:  
`python3 pire-index.py`  and 
`python3 pire-search.py`
* strictly adhere to the format specifications for input and output, as explained above.     

